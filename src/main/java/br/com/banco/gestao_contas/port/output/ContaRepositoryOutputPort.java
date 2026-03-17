package br.com.banco.gestao_contas.port.output;

import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;

import java.util.List;
import java.util.Optional;

public interface ContaRepositoryOutputPort {

    Optional<Conta> findByNumConta(String numConta);

    List<Conta> findByStatus(StatusConta status);

    Conta save(Conta conta);

    long count();
}