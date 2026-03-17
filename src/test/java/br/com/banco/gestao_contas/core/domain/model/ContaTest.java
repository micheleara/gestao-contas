package br.com.banco.gestao_contas.core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    @Test
    void construtorCompletoDevePreencherTodosOsCampos() {
        LocalDateTime agora = LocalDateTime.now();
        Conta conta = new Conta("12345-6", "João da Silva", StatusConta.ATIVA, new BigDecimal("1500.50"), agora);

        assertEquals("12345-6", conta.getNumConta());
        assertEquals("João da Silva", conta.getNomeCliente());
        assertEquals(StatusConta.ATIVA, conta.getStatus());
        assertEquals(new BigDecimal("1500.50"), conta.getSaldo());
        assertEquals(agora, conta.getAtualizadoEm());
    }

    @Test
    void settersDevemAtualizarCampos() {
        Conta conta = new Conta();
        conta.setNumConta("99999-9");
        conta.setStatus(StatusConta.CANCELADA);
        conta.setSaldo(BigDecimal.ZERO);

        assertEquals("99999-9", conta.getNumConta());
        assertEquals(StatusConta.CANCELADA, conta.getStatus());
        assertEquals(BigDecimal.ZERO, conta.getSaldo());
    }
}