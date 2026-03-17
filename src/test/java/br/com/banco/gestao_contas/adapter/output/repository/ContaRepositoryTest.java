package br.com.banco.gestao_contas.adapter.output.repository;

import br.com.banco.gestao_contas.adapter.output.mapper.ContaMapper;
import br.com.banco.gestao_contas.adapter.output.repository.entity.ContaEntity;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaRepositoryTest {

    @Mock
    private SpringDataContaRepository springDataRepository;

    @Mock
    private ContaMapper mapper;

    @InjectMocks
    private ContaRepository contaRepository;

    @Test
    void findByNumConta_deveRetornarContaQuandoEncontrada() {
        ContaEntity entity = new ContaEntity("1234-5", "Ana", StatusConta.ATIVA, BigDecimal.TEN, LocalDateTime.now());
        Conta conta = new Conta("1234-5", "Ana", StatusConta.ATIVA, BigDecimal.TEN, LocalDateTime.now());

        when(springDataRepository.findByNumConta("1234-5")).thenReturn(Optional.of(entity));
        when(mapper.toDomain(entity)).thenReturn(conta);

        Optional<Conta> result = contaRepository.findByNumConta("1234-5");

        assertTrue(result.isPresent());
        assertEquals("1234-5", result.get().getNumConta());
    }

    @Test
    void findByNumConta_deveRetornarVazioQuandoNaoEncontrada() {
        when(springDataRepository.findByNumConta("9999")).thenReturn(Optional.empty());

        assertTrue(contaRepository.findByNumConta("9999").isEmpty());
    }

    @Test
    void findByStatus_deveRetornarListaDeDominio() {
        ContaEntity entity = new ContaEntity("1111-1", "Bob", StatusConta.CANCELADA, BigDecimal.ZERO, LocalDateTime.now());
        Conta conta = new Conta("1111-1", "Bob", StatusConta.CANCELADA, BigDecimal.ZERO, LocalDateTime.now());

        when(springDataRepository.findByStatus(StatusConta.CANCELADA)).thenReturn(List.of(entity));
        when(mapper.toDomain(entity)).thenReturn(conta);

        List<Conta> result = contaRepository.findByStatus(StatusConta.CANCELADA);

        assertEquals(1, result.size());
        assertEquals("1111-1", result.get(0).getNumConta());
    }

    @Test
    void count_deveRetornarTotalDeRegistros() {
        when(springDataRepository.count()).thenReturn(42L);

        assertEquals(42L, contaRepository.count());
    }
}