package br.com.banco.gestao_contas.application.ports.out;

import br.com.banco.gestao_contas.domain.model.Conta;
import br.com.banco.gestao_contas.domain.model.StatusConta;

import java.util.List;
import java.util.Optional;

public interface ContaRepositoryPort {

    Optional<Conta> findByNumConta(String numConta);

    List<Conta> findByStatus(StatusConta status);
    
    Conta save(Conta conta);
    
    long count();
}
