package br.com.banco.gestao_contas.application.services;

import br.com.banco.gestao_contas.application.ports.in.ConsultaContasUseCase;
import br.com.banco.gestao_contas.application.ports.out.ContaRepositoryPort;
import br.com.banco.gestao_contas.domain.model.Conta;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ConsultaContasService implements ConsultaContasUseCase {

    private final ContaRepositoryPort contaRepositoryPort;

    public ConsultaContasService(ContaRepositoryPort contaRepositoryPort) {
        this.contaRepositoryPort = contaRepositoryPort;
    }

    @Override
    public Optional<Conta> consultarPorNumero(String numConta) {
        return contaRepositoryPort.findByNumConta(numConta);
    }
}
