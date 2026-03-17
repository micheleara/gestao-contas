package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.StatusContaResponseEvent;
import br.com.banco.gestao_contas.adapter.output.producer.ConsultaStatusProducer;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaStatusConsumerTest {

    @Mock
    private ConsultaContasInputPort consultaContasInputPort;

    @Mock
    private ConsultaStatusProducer consultaStatusProducer;

    private ConsultaStatusConsumer consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        consumer = new ConsultaStatusConsumer(consultaContasInputPort, consultaStatusProducer, objectMapper);
    }

    @Test
    void onConsultaStatus_contaEncontrada_publicaResponseComDados() throws Exception {
        Conta conta = new Conta("1234-5", "Maria", StatusConta.ATIVA, new BigDecimal("3000.00"), LocalDateTime.now());
        when(consultaContasInputPort.consultarPorNumero("1234-5")).thenReturn(Optional.of(conta));

        consumer.onConsultaStatus("{\"numeroConta\":\"1234-5\"}");

        ArgumentCaptor<StatusContaResponseEvent> captor = ArgumentCaptor.forClass(StatusContaResponseEvent.class);
        verify(consultaStatusProducer).publicar(captor.capture());

        StatusContaResponseEvent response = captor.getValue();
        assertEquals("1234-5", response.numConta());
        assertEquals("Maria", response.nomeCliente());
        assertEquals("ATIVA", response.status());
        assertEquals(new BigDecimal("3000.00"), response.saldo());
    }

    @Test
    void onConsultaStatus_contaNaoEncontrada_publicaResponseComStatusCancelada() throws Exception {
        when(consultaContasInputPort.consultarPorNumero("9999-0")).thenReturn(Optional.empty());

        consumer.onConsultaStatus("{\"numeroConta\":\"9999-0\"}");

        ArgumentCaptor<StatusContaResponseEvent> captor = ArgumentCaptor.forClass(StatusContaResponseEvent.class);
        verify(consultaStatusProducer).publicar(captor.capture());

        assertEquals("CANCELADA", captor.getValue().status());
        assertEquals(BigDecimal.ZERO, captor.getValue().saldo());
    }

    @Test
    void onConsultaStatus_payloadInvalido_naoPublicaResponse() {
        consumer.onConsultaStatus("payload-invalido");

        verify(consultaStatusProducer, never()).publicar(any());
    }
}