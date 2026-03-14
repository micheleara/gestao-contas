package br.com.banco.gestao_contas.application.ports.in;

import br.com.banco.gestao_contas.domain.model.Conta;
import java.util.Optional;

public interface ConsultaContasUseCase {
    
    Optional<Conta> consultarPorNumero(String numConta);

}
