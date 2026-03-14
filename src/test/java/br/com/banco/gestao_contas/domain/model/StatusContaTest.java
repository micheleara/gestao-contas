package br.com.banco.gestao_contas.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StatusContaTest {

    @Test
    void testValues() {
        StatusConta[] values = StatusConta.values();
        assertEquals(4, values.length);
        assertEquals(StatusConta.ATIVA, StatusConta.valueOf("ATIVA"));
        assertEquals(StatusConta.INATIVA, StatusConta.valueOf("INATIVA"));
        assertEquals(StatusConta.BLOQUEADA, StatusConta.valueOf("BLOQUEADA"));
        assertEquals(StatusConta.ENCERRADA, StatusConta.valueOf("ENCERRADA"));
    }
}
