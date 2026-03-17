package br.com.banco.gestao_contas.core.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusContaTest {

    @Test
    void devePossuirQuatroValores() {
        StatusConta[] values = StatusConta.values();
        assertEquals(4, values.length);
        assertEquals(StatusConta.ATIVA, StatusConta.valueOf("ATIVA"));
        assertEquals(StatusConta.CANCELADA, StatusConta.valueOf("CANCELADA"));
        assertEquals(StatusConta.BLOQUEIO_JUDICIAL, StatusConta.valueOf("BLOQUEIO_JUDICIAL"));
        assertEquals(StatusConta.INDISPONIVEL, StatusConta.valueOf("INDISPONIVEL"));
    }
}