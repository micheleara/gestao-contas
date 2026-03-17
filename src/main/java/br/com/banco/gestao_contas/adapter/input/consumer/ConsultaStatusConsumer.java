package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaStatusRequestEvent;
import br.com.banco.gestao_contas.adapter.input.consumer.dto.StatusContaResponseEvent;
import br.com.banco.gestao_contas.adapter.output.producer.ConsultaStatusProducer;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ConsultaStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaStatusConsumer.class);

    private final ConsultaContasInputPort consultaContasInputPort;
    private final ConsultaStatusProducer consultaStatusProducer;
    private final ObjectMapper objectMapper;

    public ConsultaStatusConsumer(ConsultaContasInputPort consultaContasInputPort,
                                  ConsultaStatusProducer consultaStatusProducer,
                                  ObjectMapper objectMapper) {
        this.consultaContasInputPort = consultaContasInputPort;
        this.consultaStatusProducer = consultaStatusProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.consulta-status-request}")
    public void onConsultaStatus(String mensagem) {
        try {
            ConsultaStatusRequestEvent evento = objectMapper.readValue(mensagem, ConsultaStatusRequestEvent.class);

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

            consultaStatusProducer.publicar(response);

        } catch (Exception e) {
            log.error("Erro ao processar consulta de status: {}", e.getMessage(), e);
        }
    }
}