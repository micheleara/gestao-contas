package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaRequestEvent;
import br.com.banco.gestao_contas.adapter.input.consumer.dto.ConsultaContaResponseEvent;
import br.com.banco.gestao_contas.adapter.output.producer.ConsultaContaProducer;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class ConsultaContaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ConsultaContaConsumer.class);

    private final ConsultaContasInputPort consultaContasInputPort;
    private final ConsultaContaProducer consultaContaProducer;
    private final ObjectMapper objectMapper;

    public ConsultaContaConsumer(ConsultaContasInputPort consultaContasInputPort,
                                 ConsultaContaProducer consultaContaProducer,
                                 ObjectMapper objectMapper) {
        this.consultaContasInputPort = consultaContasInputPort;
        this.consultaContaProducer = consultaContaProducer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.consulta-conta-request}", groupId = "sistema-contas-encargos")
    public void consumirConsulta(ConsumerRecord<String, String> record) {
        try {
            ConsultaContaRequestEvent request = objectMapper.readValue(record.value(), ConsultaContaRequestEvent.class);

            Optional<Conta> contaOpt = consultaContasInputPort.consultarPorNumero(request.numeroConta());

            ConsultaContaResponseEvent response = contaOpt
                    .map(conta -> new ConsultaContaResponseEvent(
                            request.correlationId(),
                            conta.getNumConta(),
                            conta.getNomeCliente(),
                            conta.getStatus().name(),
                            conta.getSaldo()))
                    .orElseGet(() -> new ConsultaContaResponseEvent(
                            request.correlationId(),
                            request.numeroConta(),
                            "",
                            "CANCELADA",
                            BigDecimal.ZERO));

            consultaContaProducer.publicar(response, request.correlationId());

        } catch (Exception e) {
            log.error("Erro ao processar consulta de conta: {}", e.getMessage(), e);
        }
    }
}