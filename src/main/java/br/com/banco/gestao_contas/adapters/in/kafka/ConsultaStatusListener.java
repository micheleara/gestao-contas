package br.com.banco.gestao_contas.adapters.in.kafka;

import br.com.banco.gestao_contas.adapters.in.kafka.dto.EventoLancamento;
import br.com.banco.gestao_contas.adapters.in.kafka.dto.StatusContaResponseEvent;
import br.com.banco.gestao_contas.application.ports.in.ConsultaContasUseCase;
import br.com.banco.gestao_contas.domain.model.Conta;
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
public class ConsultaStatusListener {

    private static final Logger log = LoggerFactory.getLogger(ConsultaStatusListener.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ConsultaContasUseCase consultaContasUseCase;

    @Value("${kafka.topics.consulta-status-response}")
    private String topicResponse;

    public ConsultaStatusListener(KafkaTemplate<String, String> kafkaTemplate,
                                  ObjectMapper objectMapper,
                                  ConsultaContasUseCase consultaContasUseCase) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.consultaContasUseCase = consultaContasUseCase;
    }

    @KafkaListener(topics = "${kafka.topics.consulta-status-request}")
    public void onConsultaStatus(String mensagem) {
        try {
            EventoLancamento evento = objectMapper.readValue(mensagem, EventoLancamento.class);

            Optional<Conta> contaOpt = consultaContasUseCase.consultarPorNumero(evento.numeroConta());

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