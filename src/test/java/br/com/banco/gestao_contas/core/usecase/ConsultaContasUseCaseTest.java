package br.com.banco.gestao_contas.core.usecase;

import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import br.com.banco.gestao_contas.port.output.ContaRepositoryOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaContasUseCaseTest {

    @Mock
    private ContaRepositoryOutputPort contaRepositoryOutputPort;

    private ConsultaContasUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultaContasUseCase(contaRepositoryOutputPort);
    }

    @Test
    void consultarPorNumero_deveRetornarContaQuandoEncontrada() {
        Conta conta = new Conta("12345", "Fulano", StatusConta.ATIVA, new BigDecimal("100.50"), LocalDateTime.now());
        when(contaRepositoryOutputPort.findByNumConta("12345")).thenReturn(Optional.of(conta));

        Optional<Conta> result = useCase.consultarPorNumero("12345");

        assertTrue(result.isPresent());
        assertEquals("12345", result.get().getNumConta());
        verify(contaRepositoryOutputPort, times(1)).findByNumConta("12345");
    }

    @Test
    void consultarPorNumero_deveRetornarVazioQuandoNaoEncontrada() {
        when(contaRepositoryOutputPort.findByNumConta("abc")).thenReturn(Optional.empty());

        Optional<Conta> result = useCase.consultarPorNumero("abc");

        assertTrue(result.isEmpty());
        verify(contaRepositoryOutputPort, times(1)).findByNumConta("abc");
    }
}