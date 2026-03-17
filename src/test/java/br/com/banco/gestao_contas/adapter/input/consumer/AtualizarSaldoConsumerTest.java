package br.com.banco.gestao_contas.adapter.input.consumer;

import br.com.banco.gestao_contas.port.input.AtualizarSaldoInputPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtualizarSaldoConsumerTest {

    @Mock
    private AtualizarSaldoInputPort atualizarSaldoInputPort;

    private AtualizarSaldoConsumer consumer;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        consumer = new AtualizarSaldoConsumer(atualizarSaldoInputPort, objectMapper);
    }

    @Test
    void onAtualizarSaldo_mensagemValida_deveInvocarInputPort() {
        String mensagem = "{\"numeroConta\":\"1234-5\",\"tipoLancamento\":\"DEBITO\",\"valor\":100.00}";

        consumer.onAtualizarSaldo(mensagem);

        verify(atualizarSaldoInputPort).atualizar("1234-5", "DEBITO", new BigDecimal("100.00"));
    }

    @Test
    void onAtualizarSaldo_tipoCredito_deveInvocarInputPortComCredito() {
        String mensagem = "{\"numeroConta\":\"9876-1\",\"tipoLancamento\":\"CREDITO\",\"valor\":250.00}";

        consumer.onAtualizarSaldo(mensagem);

        verify(atualizarSaldoInputPort).atualizar("9876-1", "CREDITO", new BigDecimal("250.00"));
    }

    @Test
    void onAtualizarSaldo_payloadInvalido_naoDeveInvocarInputPort() {
        consumer.onAtualizarSaldo("payload-invalido");

        verify(atualizarSaldoInputPort, never()).atualizar(any(), any(), any());
    }

    @Test
    void onAtualizarSaldo_inputPortLancaExcecao_naoDevePropagar() {
        String mensagem = "{\"numeroConta\":\"1234-5\",\"tipoLancamento\":\"DEBITO\",\"valor\":50.00}";
        doThrow(new RuntimeException("erro simulado")).when(atualizarSaldoInputPort)
                .atualizar(any(), any(), any());

        consumer.onAtualizarSaldo(mensagem);

        verify(atualizarSaldoInputPort).atualizar("1234-5", "DEBITO", new BigDecimal("50.00"));
    }
}