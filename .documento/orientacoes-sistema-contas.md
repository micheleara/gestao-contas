# Sistema de Contas — Atualização de Saldo via Evento

## O que precisa ser feito

O sistema de contas já existe e funciona. A tabela `contas` e o repository `SpringDataContaRepository` com `findByNumConta` e `findByStatus` não mudam. O que será adicionado é a capacidade de consumir dois eventos do Kafka e responder/agir sobre eles.

## O que já existe (não mexer)

```java
@Repository
public interface SpringDataContaRepository extends JpaRepository<ContaEntity, String> {
    Optional<ContaEntity> findByNumConta(String numConta);
    List<ContaEntity> findByStatus(StatusConta status);
}
```

Tabela `contas`:

| Coluna | Tipo | Descrição |
|--------|------|-----------|
| num_conta | VARCHAR(20) | Número da conta |
| nome_cliente | VARCHAR(100) | Nome do titular |
| status | VARCHAR(20) | ATIVA / CANCELADA / BLOQUEIO_JUDICIAL |
| saldo | NUMERIC(15,2) | Saldo atual (default 0.00) |
| atualizado_em | TIMESTAMP | Última atualização |

Nenhuma coluna será adicionada, nenhuma será removida. A estrutura permanece idêntica.

## Payload recebido do producer

O serviço de processamento envia os eventos com o seguinte payload:

```json
{
  "idLancamento": "LAN-001",
  "numeroConta": "00123",
  "tipoLancamento": "DEBITO",
  "valor": 15.50
}
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| idLancamento | String | Identificador do lançamento |
| numeroConta | String | Número da conta (também usado como chave da mensagem Kafka) |
| tipoLancamento | String | DEBITO ou CREDITO (serializado via TipoLancamento.name()) |
| valor | BigDecimal | Valor do lançamento (sempre positivo) |

O `numeroConta` é a chave da mensagem Kafka, garantindo que todos os eventos da mesma conta vão para a mesma partição e são processados em ordem.

O campo `valor` vem sempre positivo. O sistema de contas deve usar o `tipoLancamento` para determinar a operação: se DEBITO subtrai, se CREDITO soma.

## O que será adicionado

Dois listeners Kafka e um método de atualização de saldo.

### 1. Listener do evento ① — Consulta de status

O serviço de processamento publica no tópico `encargos.conta.consulta-status` com o payload acima. O sistema de contas deve:

- Consumir o evento
- Extrair o `numeroConta` do payload
- Fazer `findByNumConta` (que já existe)
- Responder no tópico `encargos.conta.resposta-status` com:

```json
{
  "numConta": "00123",
  "nomeCliente": "Michele Oliveira",
  "status": "ATIVA",
  "saldo": 1000.00
}
```

Se a conta não for encontrada, responder com status indicando erro. Esse listener é apenas leitura — não altera nada no banco.

### 2. Listener do evento ⑦ — Atualização de saldo

O serviço de processamento publica no tópico `encargos.conta.atualizar-saldo` com o mesmo payload. Esse evento SÓ é publicado após receber a confirmação do sistema contábil (evento ⑥).

O sistema de contas deve:

- Consumir o evento
- Extrair `numeroConta`, `tipoLancamento` e `valor`
- Determinar o sinal da operação com base no `tipoLancamento`:
  - `DEBITO` → subtrai do saldo (`saldo = saldo - valor`)
  - `CREDITO` → soma ao saldo (`saldo = saldo + valor`)
- Executar a atualização atômica do saldo
- Garantir que não haja conflito de concorrência

### 3. Método de atualização atômica do saldo

A atualização deve ser feita de forma segura usando `SELECT ... FOR UPDATE` seguido do `UPDATE`. Isso previne race conditions quando múltiplos eventos chegam para a mesma conta simultaneamente.

Lógica no service:

```java
@Service
@Transactional
public class AtualizarSaldoService {

    @PersistenceContext
    private EntityManager em;

    public void atualizar(String numeroConta, String tipoLancamento, BigDecimal valor) {

        // Trava a linha da conta (ninguém mais mexe até o COMMIT)
        em.createNativeQuery(
            "SELECT saldo FROM contas WHERE num_conta = :conta FOR UPDATE")
            .setParameter("conta", numeroConta)
            .getSingleResult();

        // Determina o sinal com base no tipo
        BigDecimal valorOperacao = tipoLancamento.equals("DEBITO")
            ? valor.negate()   // subtrai
            : valor;           // soma

        // Atualiza atomicamente
        em.createNativeQuery(
            "UPDATE contas SET saldo = saldo + :valor, atualizado_em = now() WHERE num_conta = :conta")
            .setParameter("valor", valorOperacao)
            .setParameter("conta", numeroConta)
            .executeUpdate();
    }
}
```

O ponto crítico é o `FOR UPDATE`: ele trava a linha no banco até o fim da transação, impedindo que outra thread leia um saldo desatualizado e sobrescreva com um valor errado.

A operação `saldo = saldo + valor` é atômica no PostgreSQL. Combinada com o `FOR UPDATE`, garante consistência total mesmo com eventos chegando em paralelo.

### Por que não fazer apenas `conta.setSaldo(novoSaldo)` e `repository.save(conta)`

Esse padrão é perigoso:

```
Thread A: lê saldo = 1000.00
Thread B: lê saldo = 1000.00
Thread A: calcula 1000.00 - 15.50 = 984.50, grava 984.50
Thread B: calcula 1000.00 - 50.00 = 950.00, grava 950.00  ← ERRADO
```

O saldo final deveria ser 934.50, mas ficou 950.00 — a atualização da Thread A foi perdida. Com `FOR UPDATE` + `saldo = saldo + valor`, isso não acontece.

## DTO para deserializar o evento

```java
public record EventoLancamento(
    String idLancamento,
    String numeroConta,
    String tipoLancamento,
    BigDecimal valor
) {}
```

## Exemplo do listener de atualização de saldo

```java
@Component
public class AtualizarSaldoListener {

    private final AtualizarSaldoService atualizarSaldoService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${kafka.topics.atualizar-saldo}")
    public void onAtualizarSaldo(String mensagem) {
        EventoLancamento evento = objectMapper.readValue(mensagem, EventoLancamento.class);
        atualizarSaldoService.atualizar(
            evento.numeroConta(),
            evento.tipoLancamento(),
            evento.valor()
        );
    }
}
```

## Dependência adicional no pom.xml

O projeto já deve ter Spring Data JPA e PostgreSQL. A única dependência nova é o Kafka:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

## Configuração no application.yml

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: sistema-contas
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

kafka:
  topics:
    consulta-status-request: encargos.conta.consulta-status
    consulta-status-response: encargos.conta.resposta-status
    atualizar-saldo: encargos.conta.atualizar-saldo
```

## Resumo das alterações no sistema de contas

| O que | Ação | Impacto |
|-------|------|---------|
| Tabela `contas` | Nenhuma alteração | Zero |
| `SpringDataContaRepository` | Nenhuma alteração | Zero |
| `findByNumConta` / `findByStatus` | Continuam iguais | Zero |
| Dependência `spring-kafka` | Adicionar no pom.xml | Baixo |
| `EventoLancamento` | Criar (novo) | Record para deserializar o payload |
| `ConsultaStatusListener` | Criar (novo) | Listener que lê e responde |
| `AtualizarSaldoListener` | Criar (novo) | Listener que consome evento ⑦ |
| `AtualizarSaldoService` | Criar (novo) | Lógica de UPDATE atômico |
| `application.yml` | Adicionar config Kafka | Baixo |

São 4 classes novas, 1 dependência e configuração. O código existente não é tocado.
