package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaRequestEvent;
import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaResponseEvent;
import br.com.banco.gestao_contas.adapter.output.producer.ConsultaContaProducer;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
class ConsultaContaConsumerTest {

    @Mock
    private ConsultaContasInputPort consultaContasInputPort;

    @Mock
    private ConsultaContaProducer consultaContaProducer;

    private ConsultaContaConsumer consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String TOPIC_REQUEST = "encargos.conta.consulta.request";

    @BeforeEach
    void setUp() {
        consumer = new ConsultaContaConsumer(consultaContasInputPort, consultaContaProducer, objectMapper);
    }

    @Test
    void consumirConsulta_contaEncontrada_publicaResponseComDadosDaConta() throws Exception {
        Conta conta = new Conta("9762-7", "João da Silva", StatusConta.ATIVA, new BigDecimal("5000.00"), LocalDateTime.now());
        when(consultaContasInputPort.consultarPorNumero("9762-7")).thenReturn(Optional.of(conta));

        ConsultaContaRequestEvent request = new ConsultaContaRequestEvent("9878", "9762-7");
        ConsumerRecord<String, String> record = new ConsumerRecord<>(TOPIC_REQUEST, 0, 0, "9762-7",
                objectMapper.writeValueAsString(request));

        consumer.consumirConsulta(record);

        ArgumentCaptor<ConsultaContaResponseEvent> responseCaptor = ArgumentCaptor.forClass(ConsultaContaResponseEvent.class);
        ArgumentCaptor<String> correlationCaptor = ArgumentCaptor.forClass(String.class);
        verify(consultaContaProducer).publicar(responseCaptor.capture(), correlationCaptor.capture());

        ConsultaContaResponseEvent response = responseCaptor.getValue();
        assertEquals("9762-7", response.numeroConta());
        assertEquals("João da Silva", response.nomeCliente());
        assertEquals("ATIVA", response.status());
        assertEquals(new BigDecimal("5000.00"), response.saldo());
        assertEquals("9878", correlationCaptor.getValue());
    }

    @Test
    void consumirConsulta_contaNaoEncontrada_publicaResponseComStatusCancelada() throws Exception {
        when(consultaContasInputPort.consultarPorNumero("9999-0")).thenReturn(Optional.empty());

        ConsultaContaRequestEvent request = new ConsultaContaRequestEvent("1111", "9999-0");
        ConsumerRecord<String, String> record = new ConsumerRecord<>(TOPIC_REQUEST, 0, 0, "9999-0",
                objectMapper.writeValueAsString(request));

        consumer.consumirConsulta(record);

        ArgumentCaptor<ConsultaContaResponseEvent> captor = ArgumentCaptor.forClass(ConsultaContaResponseEvent.class);
        verify(consultaContaProducer).publicar(captor.capture(), any());

        assertEquals("CANCELADA", captor.getValue().status());
        assertEquals(BigDecimal.ZERO, captor.getValue().saldo());
    }

    @Test
    void consumirConsulta_payloadInvalido_naoPublicaResponse() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(TOPIC_REQUEST, 0, 0, null, "payload-invalido");

        consumer.consumirConsulta(record);

        verify(consultaContaProducer, never()).publicar(any(), any());
    }
}