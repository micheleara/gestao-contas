package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.adapter.input.consumer.dto.EventoLancamento;
import br.com.banco.gestao_contas.port.input.AtualizarSaldoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AtualizarSaldoConsumer {

    private static final Logger log = LoggerFactory.getLogger(AtualizarSaldoConsumer.class);

    private final AtualizarSaldoInputPort atualizarSaldoInputPort;
    private final ObjectMapper objectMapper;

    public AtualizarSaldoConsumer(AtualizarSaldoInputPort atualizarSaldoInputPort,
                                  ObjectMapper objectMapper) {
        this.atualizarSaldoInputPort = atualizarSaldoInputPort;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.atualizar-saldo}")
    public void onAtualizarSaldo(String mensagem) {
        try {
            EventoLancamento evento = objectMapper.readValue(mensagem, EventoLancamento.class);
            atualizarSaldoInputPort.atualizar(
                    evento.numeroConta(),
                    evento.tipoLancamento(),
                    evento.valor());
        } catch (Exception e) {
            log.error("Erro ao processar atualização de saldo: {}", e.getMessage(), e);
        }
    }
}