package br.com.banco.gestao_contas.core.usecase;

import br.com.banco.gestao_contas.port.input.AtualizarSaldoInputPort;
import br.com.banco.gestao_contas.port.output.AtualizarSaldoOutputPort;

import java.math.BigDecimal;

public class AtualizarSaldoUseCase implements AtualizarSaldoInputPort {

    private final AtualizarSaldoOutputPort atualizarSaldoOutputPort;

    public AtualizarSaldoUseCase(AtualizarSaldoOutputPort atualizarSaldoOutputPort) {
        this.atualizarSaldoOutputPort = atualizarSaldoOutputPort;
    }

    @Override
    public void atualizar(String numeroConta, String tipoLancamento, BigDecimal valor) {
        atualizarSaldoOutputPort.atualizar(numeroConta, tipoLancamento, valor);
    }
}