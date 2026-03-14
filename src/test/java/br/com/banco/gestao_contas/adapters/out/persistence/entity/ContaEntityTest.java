package br.com.banco.gestao_contas.adapters.out.persistence.entity;

import br.com.banco.gestao_contas.domain.model.StatusConta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ContaEntityTest {

    @Test
    void testGettersAndSetters() {
        ContaEntity entity = new ContaEntity();

        String numConta = "12345-6";
        String nomeCliente = "João da Silva";
        StatusConta status = StatusConta.ATIVA;
        BigDecimal saldo = new BigDecimal("1500.50");
        LocalDateTime atualizadoEm = LocalDateTime.now();

        entity.setNumConta(numConta);
        entity.setNomeCliente(nomeCliente);
        entity.setStatus(status);
        entity.setSaldo(saldo);
        entity.setAtualizadoEm(atualizadoEm);

        assertEquals(numConta, entity.getNumConta());
        assertEquals(nomeCliente, entity.getNomeCliente());
        assertEquals(status, entity.getStatus());
        assertEquals(saldo, entity.getSaldo());
        assertEquals(atualizadoEm, entity.getAtualizadoEm());
    }
}
