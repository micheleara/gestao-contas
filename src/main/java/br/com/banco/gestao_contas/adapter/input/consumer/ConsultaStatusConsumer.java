package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.EventoLancamento;
import br.com.banco.gestao_contas.adapter.input.consumer.dto.StatusContaResponseEvent;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ConsultaStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaStatusConsumer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ConsultaContasInputPort consultaContasInputPort;

    @Value("${kafka.topics.consulta-status-response}")
    private String topicResponse;

    public ConsultaStatusConsumer(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  ConsultaContasInputPort consultaContasInputPort) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.consultaContasInputPort = consultaContasInputPort;
    }

    @KafkaListener(topics = "${kafka.topics.consulta-status-request}")
    public void onConsultaStatus(String mensagem) {
        try {
            EventoLancamento evento = objectMapper.readValue(mensagem, EventoLancamento.class);

            Optional<Conta> contaOpt = consultaContasInputPort.consultarPorNumero(evento.numeroConta());

            StatusContaResponseEvent response = contaOpt
                    .map(conta -> new StatusContaResponseEvent(
                            conta.getNumConta(),
                            conta.getNomeCliente(),
                            conta.getStatus().name(),
                            conta.getSaldo()))
                    .orElseGet(() -> new StatusContaResponseEvent(
                            evento.numeroConta(),
                            "",
                            "CANCELADA",
                            BigDecimal.ZERO));

            String payload = objectMapper.writeValueAsString(response);
            kafkaTemplate.send(topicResponse, response.numConta(), payload);

        } catch (Exception e) {
            log.error("Erro ao processar consulta de status: {}", e.getMessage(), e);
        }
    }
}