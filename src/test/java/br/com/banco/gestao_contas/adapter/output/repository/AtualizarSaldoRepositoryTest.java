package br.com.banco.gestao_contas.adapter.output.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarSaldoRepositoryTest {

    @Mock
    private EntityManager em;

    @Mock
    private Query selectQuery;

    @Mock
    private Query updateQuery;

    private AtualizarSaldoRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AtualizarSaldoRepository();
        ReflectionTestUtils.setField(repository, "em", em);

        when(em.createNativeQuery(contains("SELECT"))).thenReturn(selectQuery);
        when(selectQuery.setParameter(anyString(), any())).thenReturn(selectQuery);
        when(selectQuery.getSingleResult()).thenReturn(new BigDecimal("1000.00"));

        when(em.createNativeQuery(contains("UPDATE"))).thenReturn(updateQuery);
        when(updateQuery.setParameter(anyString(), any())).thenReturn(updateQuery);
        when(updateQuery.executeUpdate()).thenReturn(1);
    }

    @Test
    void atualizar_deveExecutarSelectForUpdateEUpdate() {
        repository.atualizar("1234-5", new BigDecimal("100.00"));

        verify(selectQuery).getSingleResult();
        verify(updateQuery).executeUpdate();
    }

    @Test
    void atualizar_devePassarValorCorretoNoUpdate() {
        repository.atualizar("1234-5", new BigDecimal("-50.00"));

        verify(updateQuery).setParameter("valor", new BigDecimal("-50.00"));
        verify(updateQuery).setParameter("conta", "1234-5");
    }
}