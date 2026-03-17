# gestao-contas

![Versão](https://img.shields.io/badge/versão-0.0.1--SNAPSHOT-blue)
![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.11-brightgreen)
![Build](https://img.shields.io/badge/build-passing-brightgreen)

Microsserviço responsável pela gestão de contas bancárias dentro de um ecossistema de microserviços. Processa eventos Kafka para atualização de saldo e consulta de contas, expondo também uma API REST para consultas síncronas.

---

## Índice

- [Visão Geral](#visão-geral)
- [Tecnologias](#tecnologias)
- [Arquitetura](#arquitetura)
- [Pré-requisitos](#pré-requisitos)
- [Instalação](#instalação)
- [Configuração](#configuração)
- [Uso](#uso)
- [API](#api)
- [Tópicos Kafka](#tópicos-kafka)
- [Testes](#testes)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Contribuição](#contribuição)

---

## Visão Geral

O `gestao-contas` é um microsserviço Spring Boot que centraliza as operações sobre contas bancárias. Ele consome eventos assíncronos via Apache Kafka para atualizar saldos e responder a consultas de outros serviços, além de oferecer um endpoint REST para consultas síncronas.

**Principais funcionalidades:**

- Atualização de saldo por débito ou crédito via evento Kafka
- Consulta de dados da conta (número, nome do cliente, status, saldo) via Kafka request/response com correlação
- Consulta de status da conta via Kafka
- Endpoint REST para consulta direta de conta por número
- Dead Letter Queue (DLQ) com 3 tentativas e backoff de 2 segundos para mensagens com erro

---

## Tecnologias

| Categoria       | Tecnologia              | Versão     |
|-----------------|-------------------------|------------|
| Linguagem       | Java                    | 21         |
| Framework       | Spring Boot             | 3.5.11     |
| Mensageria      | Apache Kafka            | —          |
| Banco de Dados  | PostgreSQL              | —          |
| ORM             | Spring Data JPA         | —          |
| Migrations      | Flyway                  | 11.x       |
| Cobertura       | JaCoCo                  | 0.8.12     |
| Testes          | JUnit 5 + Mockito       | —          |

---

## Arquitetura

O serviço segue a **Arquitetura Hexagonal (Ports & Adapters)**, mantendo o núcleo de negócio isolado de frameworks e infraestrutura.

```
                        ┌──────────────────────────────────┐
                        │            CORE                  │
  ┌──────────────┐      │  ┌────────────────────────────┐  │      ┌─────────────────────┐
  │  Kafka       │─────▶│  │  AtualizarSaldoUseCase     │  │─────▶│  AtualizarSaldo     │
  │  Consumer    │      │  │  ConsultaContasUseCase     │  │      │  Repository (JPA)   │
  └──────────────┘      │  └────────────────────────────┘  │      └─────────────────────┘
                        │                                  │
  ┌──────────────┐      │  Ports (interfaces puras)        │      ┌─────────────────────┐
  │  REST        │─────▶│  ├── input/                      │─────▶│  Kafka              │
  │  Controller  │      │  │   AtualizarSaldoInputPort     │      │  Producer           │
  └──────────────┘      │  │   ConsultaContasInputPort     │      └─────────────────────┘
                        │  └── output/                     │
                        │      AtualizarSaldoOutputPort    │      ┌─────────────────────┐
                        │      ContaRepositoryOutputPort   │─────▶│  PostgreSQL         │
                        └──────────────────────────────────┘      └─────────────────────┘
```

- **`core/domain`** — modelos de domínio (`Conta`, `StatusConta`) sem dependências externas
- **`core/usecase`** — regras de negócio puras, sem anotações Spring
- **`port/input`** — contratos que os adapters de entrada devem chamar
- **`port/output`** — contratos que os adapters de saída devem implementar
- **`adapter/input`** — consumers Kafka e controller REST
- **`adapter/output`** — producers Kafka e repositórios JPA
- **`config`** — configurações Spring (wiring de use cases, Kafka, Flyway)

---

## Pré-requisitos

- Java >= 21
- Maven >= 3.9
- PostgreSQL >= 14 (acessível via variável de ambiente)
- Apache Kafka >= 3.x (acessível via variável de ambiente)


---

```

O serviço sobe na porta **8081**.

---

## API

Base URL: `http://localhost:8081`

| Método | Endpoint                                        | Descrição                          |
|--------|-------------------------------------------------|------------------------------------|
| GET    | `/api/v1/consulta-contas?num_conta={numConta}`  | Consulta os dados de uma conta     |
| GET    | `/actuator/health`                              | Status de saúde do serviço         |
| GET    | `/actuator/info`                                | Informações da aplicação           |
| GET    | `/actuator/metrics`                             | Métricas da aplicação              |

**Exemplo de resposta — consulta de conta:**

```json
{
  "numeroConta": "12345-6",
  "nomeCliente": "João Silva",
  "status": "ATIVA",
  "saldo": 1500.00
}
```

---

## Tópicos Kafka

| Tópico                          | Direção  | Descrição                                          |
|---------------------------------|----------|----------------------------------------------------|
| `encargos.conta.atualizar-saldo`| Consume  | Recebe eventos de débito/crédito para atualizar o saldo |
| `encargos.conta.consulta.request` | Consume | Recebe requisições de consulta de conta com `correlationId` |
| `encargos.conta.consulta.response` | Publica | Publica resposta da consulta de conta             |
| `encargos.conta.consulta-status` | Consume | Recebe requisições de consulta de status da conta |
| `encargos.conta.resposta-status` | Publica | Publica resposta com status da conta              |

**Formato do evento de atualização de saldo:**

```json
{
  "numeroConta": "12345-6",
  "tipoLancamento": "DEBITO",
  "valor": 100.00
}
```

> `tipoLancamento` aceita `DEBITO` (subtrai do saldo) ou `CREDITO` (adiciona ao saldo).

**Resiliência:** mensagens que falham após 3 tentativas (backoff de 2s) são encaminhadas para DLQ (Dead Letter Topic).

---

## Testes

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório de cobertura
./mvnw verify
```

O relatório de cobertura JaCoCo é gerado em `target/site/jacoco/index.html`.

O projeto exige **mínimo de 80% de cobertura de linhas** — o build falha caso não seja atingido.

Os testes de integração utilizam H2 em memória e `@EmbeddedKafka` para simular o broker.

---

## Estrutura do Projeto

```
gestao-contas/
├── src/
│   ├── main/
│   │   ├── java/br/com/banco/gestao_contas/
│   │   │   ├── adapter/
│   │   │   │   ├── input/
│   │   │   │   │   ├── consumer/          # Kafka consumers (atualizar saldo, consulta)
│   │   │   │   │   │   └── dto/           # DTOs dos eventos consumidos
│   │   │   │   │   └── controller/        # REST controllers
│   │   │   │   └── output/
│   │   │   │       ├── producer/          # Kafka producers (resposta de consultas)
│   │   │   │       ├── repository/        # Repositórios JPA
│   │   │   │       │   └── entity/        # Entidades JPA
│   │   │   │       └── mapper/            # Mapeamento entity ↔ domain
│   │   │   ├── config/                    # Configurações Spring (Kafka, Flyway, UseCases)
│   │   │   ├── core/
│   │   │   │   ├── domain/model/          # Modelos de domínio (Conta, StatusConta)
│   │   │   │   └── usecase/               # Casos de uso (regras de negócio)
│   │   │   └── port/
│   │   │       ├── input/                 # Interfaces de entrada (driven by adapters)
│   │   │       └── output/                # Interfaces de saída (implemented by adapters)
│   │   └── resources/
│   │       ├── db/migration/              # Scripts Flyway (V3, V4)
│   │       ├── application.yaml           # Configuração base
│   │       └── application-default.yaml   # Configuração do ambiente padrão
│   └── test/
│       ├── java/                          # Testes unitários e de integração
│       └── resources/                     # Configuração dos testes (H2, EmbeddedKafka)
├── .documento/                            # Documentação interna do projeto
├── pom.xml                                # Dependências e plugins Maven
└── mvnw / mvnw.cmd                        # Maven Wrapper
```
