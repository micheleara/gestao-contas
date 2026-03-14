package br.com.banco.gestao_contas.application.services;

import br.com.banco.gestao_contas.application.ports.out.ContaRepositoryPort;
import br.com.banco.gestao_contas.domain.model.Conta;
import br.com.banco.gestao_contas.domain.model.StatusConta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ConsultaContasServiceTest {

    @Mock
    private ContaRepositoryPort contaRepositoryPort;

    @InjectMocks
    private ConsultaContasService consultaContasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consultarPorNumero_deveRetornarConta() {
        Conta conta = new Conta("12345", "Fulano", StatusConta.ATIVA, new BigDecimal("100.50"), LocalDateTime.now());
        when(contaRepositoryPort.findByNumConta("12345")).thenReturn(Optional.of(conta));

        Optional<Conta> result = consultaContasService.consultarPorNumero("12345");

        assertTrue(result.isPresent());
        assertEquals("12345", result.get().getNumConta());
        verify(contaRepositoryPort, times(1)).findByNumConta("12345");
    }

    @Test
    void consultarPorNumero_deveRetornarVazio() {
        when(contaRepositoryPort.findByNumConta("abc")).thenReturn(Optional.empty());

        Optional<Conta> result = consultaContasService.consultarPorNumero("abc");

        assertTrue(result.isEmpty());
        verify(contaRepositoryPort, times(1)).findByNumConta("abc");
    }
}
