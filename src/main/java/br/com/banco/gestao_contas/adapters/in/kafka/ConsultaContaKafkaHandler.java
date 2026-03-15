package br.com.banco.gestao_contas.adapters.in.kafka;

import br.com.banco.gestao_contas.adapters.in.kafka.dto.ConsultaContaRequestEvent;
import br.com.banco.gestao_contas.adapters.in.kafka.dto.ConsultaContaResponseEvent;
import br.com.banco.gestao_contas.application.ports.in.ConsultaContasUseCase;
import br.com.banco.gestao_contas.domain.model.Conta;
import br.com.banco.gestao_contas.domain.model.StatusConta;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class ConsultaContaKafkaHandler {

    private static final Logger log = LoggerFactory.getLogger(ConsultaContaKafkaHandler.class);

    static final String TOPIC_REQUEST = "encargos.conta.consulta.request";
    static final String TOPIC_RESPONSE = "encargos.conta.consulta.response";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ConsultaContasUseCase consultaContasUseCase;

    public ConsultaContaKafkaHandler(KafkaTemplate<String, String> kafkaTemplate,
                                     ObjectMapper objectMapper,
                                     ConsultaContasUseCase consultaContasUseCase) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.consultaContasUseCase = consultaContasUseCase;
    }

    @KafkaListener(topics = TOPIC_REQUEST, groupId = "sistema-contas-encargos")
    public void consumirConsulta(ConsumerRecord<String, String> record) {
        try {
            ConsultaContaRequestEvent request = objectMapper.readValue(record.value(), ConsultaContaRequestEvent.class);

            Optional<Conta> contaOpt = consultaContasUseCase.consultarPorNumero(request.numeroConta());

            ConsultaContaResponseEvent response = contaOpt
                    .map(conta -> new ConsultaContaResponseEvent(
                            request.correlationId(),
                            conta.getNumConta(),
                            conta.getNomeCliente(),
                            mapStatus(conta.getStatus()),
                            conta.getSaldo()))
                    .orElseGet(() -> new ConsultaContaResponseEvent(
                            request.correlationId(),
                            request.numeroConta(),
                            "",
                            "CANCELADA",
                            BigDecimal.ZERO));

            String payload = objectMapper.writeValueAsString(response);

            ProducerRecord<String, String> responseRecord = new ProducerRecord<>(
                    TOPIC_RESPONSE,
                    response.numeroConta(),
                    payload);
            responseRecord.headers().add("correlationId", request.correlationId().getBytes(StandardCharsets.UTF_8));

            kafkaTemplate.send(responseRecord);

        } catch (Exception e) {
            log.error("Erro ao processar consulta de conta: {}", e.getMessage(), e);
        }
    }

    String mapStatus(StatusConta status) {
        return status.name();
    }
}