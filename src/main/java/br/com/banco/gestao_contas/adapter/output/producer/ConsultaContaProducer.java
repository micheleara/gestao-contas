package br.com.banco.gestao_contas.adapter.output.producer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ConsultaContaProducer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaContaProducer.class);

    @Value("${kafka.topics.consulta-conta-response}")
    private String topicResponse;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ConsultaContaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicar(ConsultaContaResponseEvent response, String correlationId) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            ProducerRecord<String, String> record = new ProducerRecord<>(topicResponse, response.numeroConta(), payload);
            record.headers().add("correlationId", correlationId.getBytes(StandardCharsets.UTF_8));
            kafkaTemplate.send(record);
        } catch (Exception e) {
            log.error("Erro ao publicar resposta de consulta conta: {}", e.getMessage(), e);
        }
    }
}