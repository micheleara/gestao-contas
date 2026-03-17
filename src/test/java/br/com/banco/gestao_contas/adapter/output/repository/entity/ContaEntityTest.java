package br.com.banco.gestao_contas.adapter.output.repository.entity;

import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaEntityTest {

    @Test
    void construtorCompletoDevePreencherTodosOsCampos() {
        LocalDateTime agora = LocalDateTime.now();
        ContaEntity entity = new ContaEntity("12345-6", "João da Silva", StatusConta.ATIVA,
                new BigDecimal("1500.50"), agora);

        assertEquals("12345-6", entity.getNumConta());
        assertEquals("João da Silva", entity.getNomeCliente());
        assertEquals(StatusConta.ATIVA, entity.getStatus());
        assertEquals(new BigDecimal("1500.50"), entity.getSaldo());
        assertEquals(agora, entity.getAtualizadoEm());
    }
}