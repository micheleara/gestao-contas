package br.com.banco.gestao_contas.port.input;

import java.math.BigDecimal;

public interface AtualizarSaldoInputPort {

    void atualizar(String numeroConta, String tipoLancamento, BigDecimal valor);
}