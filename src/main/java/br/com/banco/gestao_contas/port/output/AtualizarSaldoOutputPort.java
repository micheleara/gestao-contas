package br.com.banco.gestao_contas.port.output;

import java.math.BigDecimal;

public interface AtualizarSaldoOutputPort {

    void atualizar(String numeroConta, String tipoLancamento, BigDecimal valor);
}