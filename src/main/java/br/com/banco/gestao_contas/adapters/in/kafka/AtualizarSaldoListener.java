package br.com.banco.gestao_contas.adapters.in.kafka;

import br.com.banco.gestao_contas.adapters.in.kafka.dto.EventoLancamento;
import br.com.banco.gestao_contas.application.services.AtualizarSaldoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AtualizarSaldoListener {

    private static final Logger log = LoggerFactory.getLogger(AtualizarSaldoListener.class);

    private final AtualizarSaldoService atualizarSaldoService;
    private final ObjectMapper objectMapper;

    public AtualizarSaldoListener(AtualizarSaldoService atualizarSaldoService,
                                  ObjectMapper objectMapper) {
        this.atualizarSaldoService = atualizarSaldoService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${kafka.topics.atualizar-saldo}")
    public void onAtualizarSaldo(String mensagem) {
        try {
            EventoLancamento evento = objectMapper.readValue(mensagem, EventoLancamento.class);
            atualizarSaldoService.atualizar(
                    evento.numeroConta(),
                    evento.tipoLancamento(),
                    evento.valor());
        } catch (Exception e) {
            log.error("Erro ao processar atualização de saldo: {}", e.getMessage(), e);
        }
    }
}