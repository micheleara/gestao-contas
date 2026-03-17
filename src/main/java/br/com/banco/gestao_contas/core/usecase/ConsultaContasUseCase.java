package br.com.banco.gestao_contas.core.usecase;

import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import br.com.banco.gestao_contas.port.output.ContaRepositoryOutputPort;

import java.util.Optional;

public class ConsultaContasUseCase implements ConsultaContasInputPort {

    private final ContaRepositoryOutputPort contaRepositoryOutputPort;

    public ConsultaContasUseCase(ContaRepositoryOutputPort contaRepositoryOutputPort) {
        this.contaRepositoryOutputPort = contaRepositoryOutputPort;
    }

    @Override
    public Optional<Conta> consultarPorNumero(String numConta) {
        return contaRepositoryOutputPort.findByNumConta(numConta);
    }
}