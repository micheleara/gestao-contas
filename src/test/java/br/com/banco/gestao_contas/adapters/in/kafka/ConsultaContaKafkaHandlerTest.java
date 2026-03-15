package br.com.banco.gestao_contas.adapters.in.kafka;

import br.com.banco.gestao_contas.adapters.in.kafka.dto.ConsultaContaRequestEvent;
import br.com.banco.gestao_contas.adapters.in.kafka.dto.ConsultaContaResponseEvent;
import br.com.banco.gestao_contas.application.ports.in.ConsultaContasUseCase;
import br.com.banco.gestao_contas.domain.model.Conta;
import br.com.banco.gestao_contas.domain.model.StatusConta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ConsultaContaKafkaHandlerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ConsultaContasUseCase consultaContasUseCase;

    @InjectMocks
    private ConsultaContaKafkaHandler handler;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new ConsultaContaKafkaHandler(kafkaTemplate, objectMapper, consultaContasUseCase);
    }

    @Test
    void consumirConsulta_contaEncontrada_publicaResponseComDadosDaConta() throws Exception {
        Conta conta = new Conta("9762-7", "João da Silva", StatusConta.ATIVA, new BigDecimal("5000.00"), LocalDateTime.now());
        when(consultaContasUseCase.consultarPorNumero("9762-7")).thenReturn(Optional.of(conta));

        ConsultaContaRequestEvent request = new ConsultaContaRequestEvent("9878", "9762-7");
        String payload = objectMapper.writeValueAsString(request);
        ConsumerRecord<String, String> record = new ConsumerRecord<>(ConsultaContaKafkaHandler.TOPIC_REQUEST, 0, 0, "9762-7", payload);

        handler.consumirConsulta(record);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        ProducerRecord<String, String> sent = captor.getValue();
        assertEquals(ConsultaContaKafkaHandler.TOPIC_RESPONSE, sent.topic());
        assertEquals("9762-7", sent.key());

        ConsultaContaResponseEvent response = objectMapper.readValue(sent.value(), ConsultaContaResponseEvent.class);
        assertEquals("9878", response.correlationId());
        assertEquals("9762-7", response.numeroConta());
        assertEquals("João da Silva", response.nomeCliente());
        assertEquals("ATIVA", response.status());
        assertEquals(new BigDecimal("5000.00"), response.saldo());

        String correlationIdHeader = new String(sent.headers().lastHeader("correlationId").value());
        assertEquals("9878", correlationIdHeader);
    }

    @Test
    void consumirConsulta_contaNaoEncontrada_publicaResponseComStatusCancelada() throws Exception {
        when(consultaContasUseCase.consultarPorNumero("9999-0")).thenReturn(Optional.empty());

        ConsultaContaRequestEvent request = new ConsultaContaRequestEvent("1111", "9999-0");
        String payload = objectMapper.writeValueAsString(request);
        ConsumerRecord<String, String> record = new ConsumerRecord<>(ConsultaContaKafkaHandler.TOPIC_REQUEST, 0, 0, "9999-0", payload);

        handler.consumirConsulta(record);

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        ProducerRecord<String, String> sent = captor.getValue();
        ConsultaContaResponseEvent response = objectMapper.readValue(sent.value(), ConsultaContaResponseEvent.class);
        assertEquals("1111", response.correlationId());
        assertEquals("9999-0", response.numeroConta());
        assertEquals("CANCELADA", response.status());
        assertEquals(BigDecimal.ZERO, response.saldo());
    }

    @Test
    void consumirConsulta_payloadInvalido_naoPublicaResponse() {
        ConsumerRecord<String, String> record = new ConsumerRecord<>(ConsultaContaKafkaHandler.TOPIC_REQUEST, 0, 0, null, "payload-invalido");

        handler.consumirConsulta(record);

        verify(kafkaTemplate, never()).send(any(ProducerRecord.class));
    }

    @Test
    void mapStatus_ativa_retornaAtiva() {
        assertEquals("ATIVA", handler.mapStatus(StatusConta.ATIVA));
    }

    @Test
    void mapStatus_cancelada_retornaCancelada() {
        assertEquals("CANCELADA", handler.mapStatus(StatusConta.CANCELADA));
    }

    @Test
    void mapStatus_bloqueioJudicial_retornaBloqueioJudicial() {
        assertEquals("BLOQUEIO_JUDICIAL", handler.mapStatus(StatusConta.BLOQUEIO_JUDICIAL));
    }

    @Test
    void mapStatus_indisponivel_retornaIndisponivel() {
        assertEquals("INDISPONIVEL", handler.mapStatus(StatusConta.INDISPONIVEL));
    }
}