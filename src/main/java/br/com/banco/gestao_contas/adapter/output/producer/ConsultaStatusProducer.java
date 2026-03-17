package br.com.banco.gestao_contas.adapter.output.producer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.StatusContaResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ConsultaStatusProducer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaStatusProducer.class);

    @Value("${kafka.topics.consulta-status-response}")
    private String topicResponse;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ConsultaStatusProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publicar(StatusContaResponseEvent response) {
        try {
            String payload = objectMapper.writeValueAsString(response);
            kafkaTemplate.send(topicResponse, response.numConta(), payload);
        } catch (Exception e) {
            log.error("Erro ao publicar resposta de consulta status: {}", e.getMessage(), e);
        }
    }
}