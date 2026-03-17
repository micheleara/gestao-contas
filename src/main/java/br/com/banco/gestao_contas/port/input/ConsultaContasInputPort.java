package br.com.banco.gestao_contas.port.input;

import br.com.banco.gestao_contas.core.domain.model.Conta;

import java.util.Optional;

public interface ConsultaContasInputPort {

    Optional<Conta> consultarPorNumero(String numConta);
}