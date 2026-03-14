package br.com.banco.gestao_contas.adapters.out.persistence.mapper;

import br.com.banco.gestao_contas.adapters.out.persistence.entity.ContaEntity;
import br.com.banco.gestao_contas.domain.model.Conta;
import br.com.banco.gestao_contas.domain.model.StatusConta;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ContaMapperTest {

    private final ContaMapper mapper = new ContaMapper();

    @Test
    void testToDomain() {
        LocalDateTime now = LocalDateTime.now();
        ContaEntity entity = new ContaEntity("111", "Maju", StatusConta.ATIVA, new BigDecimal("100"), now);

        Conta domain = mapper.toDomain(entity);

        assertNotNull(domain);
        assertEquals("111", domain.getNumConta());
        assertEquals("Maju", domain.getNomeCliente());
        assertEquals(StatusConta.ATIVA, domain.getStatus());
        assertEquals(new BigDecimal("100"), domain.getSaldo());
        assertEquals(now, domain.getAtualizadoEm());
    }

    @Test
    void testToDomainNull() {
        assertNull(mapper.toDomain(null));
    }

    @Test
    void testToEntity() {
        LocalDateTime now = LocalDateTime.now();
        Conta domain = new Conta("222", "Juca", StatusConta.INATIVA, new BigDecimal("200"), now);

        ContaEntity entity = mapper.toEntity(domain);

        assertNotNull(entity);
        assertEquals("222", entity.getNumConta());
        assertEquals("Juca", entity.getNomeCliente());
        assertEquals(StatusConta.INATIVA, entity.getStatus());
        assertEquals(new BigDecimal("200"), entity.getSaldo());
        assertEquals(now, entity.getAtualizadoEm());
    }

    @Test
    void testToEntityNull() {
        assertNull(mapper.toEntity(null));
    }
}
