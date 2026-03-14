package br.com.banco.gestao_contas.domain.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    @Test
    void testGettersAndSetters() {
        Conta conta = new Conta();

        String numConta = "12345-6";
        String nomeCliente = "João da Silva";
        StatusConta status = StatusConta.ATIVA;
        BigDecimal saldo = new BigDecimal("1500.50");
        LocalDateTime atualizadoEm = LocalDateTime.now();

        conta.setNumConta(numConta);
        conta.setNomeCliente(nomeCliente);
        conta.setStatus(status);
        conta.setSaldo(saldo);
        conta.setAtualizadoEm(atualizadoEm);

        assertEquals(numConta, conta.getNumConta());
        assertEquals(nomeCliente, conta.getNomeCliente());
        assertEquals(status, conta.getStatus());
        assertEquals(saldo, conta.getSaldo());
        assertEquals(atualizadoEm, conta.getAtualizadoEm());
    }
}
