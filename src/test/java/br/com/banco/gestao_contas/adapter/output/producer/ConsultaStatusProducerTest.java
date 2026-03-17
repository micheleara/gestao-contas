package br.com.banco.gestao_contas.adapter.output.producer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.StatusContaResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaStatusProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    private ConsultaStatusProducer producer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        producer = new ConsultaStatusProducer(kafkaTemplate, objectMapper);
        ReflectionTestUtils.setField(producer, "topicResponse", "encargos.conta.resposta-status");
    }

    @Test
    void publicar_deveEnviarMensagemNoTopicoCorreto() {
        StatusContaResponseEvent response = new StatusContaResponseEvent(
                "1234-5", "João Silva", "ATIVA", new BigDecimal("2000.00"));

        producer.publicar(response);

        verify(kafkaTemplate).send(eq("encargos.conta.resposta-status"), eq("1234-5"), any(String.class));
    }

    @Test
    void publicar_payloadDeveConterDadosDoStatus() {
        StatusContaResponseEvent response = new StatusContaResponseEvent(
                "9876-1", "Maria", "BLOQUEIO_JUDICIAL", new BigDecimal("300.00"));

        producer.publicar(response);

        verify(kafkaTemplate).send(any(String.class), eq("9876-1"), argThat(payload ->
                payload.contains("9876-1") && payload.contains("BLOQUEIO_JUDICIAL")));
    }

    @Test
    void publicar_kafkaTemplateLancaExcecao_naoDevePropagar() {
        StatusContaResponseEvent response = new StatusContaResponseEvent(
                "1234-5", "João", "ATIVA", BigDecimal.ZERO);
        doThrow(new RuntimeException("broker indisponível")).when(kafkaTemplate)
                .send(any(String.class), any(String.class), any(String.class));

        assertDoesNotThrow(() -> producer.publicar(response));
    }
}