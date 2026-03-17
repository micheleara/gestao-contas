package br.com.banco.gestao_contas.adapter.output.producer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaContaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ConsultaContaProducer producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        producer = new ConsultaContaProducer(kafkaTemplate, objectMapper);
        ReflectionTestUtils.setField(producer, "topicResponse", "encargos.conta.consulta.response");
    }

    @Test
    @SuppressWarnings("unchecked")
    void publicar_deveEnviarMensagemComCorrelationIdNoHeader() {
        ConsultaContaResponseEvent response = new ConsultaContaResponseEvent(
                "corr-123", "1234-5", "João Silva", "ATIVA", new BigDecimal("1000.00"));

        producer.publicar(response, "corr-123");

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        ProducerRecord<String, String> record = captor.getValue();
        assertEquals("encargos.conta.consulta.response", record.topic());
        assertEquals("1234-5", record.key());

        byte[] headerValue = record.headers().lastHeader("correlationId").value();
        assertEquals("corr-123", new String(headerValue, StandardCharsets.UTF_8));
    }

    @Test
    @SuppressWarnings("unchecked")
    void publicar_payloadDeveConterDadosDaConta() throws Exception {
        ConsultaContaResponseEvent response = new ConsultaContaResponseEvent(
                "corr-456", "9876-1", "Maria", "BLOQUEIO_JUDICIAL", new BigDecimal("500.00"));

        producer.publicar(response, "corr-456");

        ArgumentCaptor<ProducerRecord<String, String>> captor = ArgumentCaptor.forClass(ProducerRecord.class);
        verify(kafkaTemplate).send(captor.capture());

        String payload = captor.getValue().value();
        assertTrue(payload.contains("9876-1"));
        assertTrue(payload.contains("Maria"));
        assertTrue(payload.contains("BLOQUEIO_JUDICIAL"));
    }

    @Test
    void publicar_kafkaTemplateLancaExcecao_naoDevePropagar() {
        ConsultaContaResponseEvent response = new ConsultaContaResponseEvent(
                "corr-789", "1234-5", "João", "ATIVA", BigDecimal.ZERO);
        doThrow(new RuntimeException("broker indisponível")).when(kafkaTemplate).send(any(ProducerRecord.class));

        assertDoesNotThrow(() -> producer.publicar(response, "corr-789"));
    }
}