package br.com.banco.gestao_contas.core.usecase;

import br.com.banco.gestao_contas.port.output.AtualizarSaldoOutputPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarSaldoUseCaseTest {

    @Mock
    private AtualizarSaldoOutputPort outputPort;

    private AtualizarSaldoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new AtualizarSaldoUseCase(outputPort);
    }

    @Test
    void debitoDevePassarValorNegativoParaOutputPort() {
        useCase.atualizar("1234-5", "DEBITO", new BigDecimal("100.00"));

        verify(outputPort).atualizar("1234-5", new BigDecimal("-100.00"));
    }

    @Test
    void creditoDevePassarValorPositivoParaOutputPort() {
        useCase.atualizar("1234-5", "CREDITO", new BigDecimal("200.00"));

        verify(outputPort).atualizar("1234-5", new BigDecimal("200.00"));
    }

    @Test
    void tipoDesconhecidoDevePassarValorSemNegacao() {
        useCase.atualizar("1234-5", "ESTORNO", new BigDecimal("50.00"));

        verify(outputPort).atualizar("1234-5", new BigDecimal("50.00"));
    }
}