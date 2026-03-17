package br.com.banco.gestao_contas.config;

import br.com.banco.gestao_contas.core.usecase.AtualizarSaldoUseCase;
import br.com.banco.gestao_contas.core.usecase.ConsultaContasUseCase;
import br.com.banco.gestao_contas.port.input.AtualizarSaldoInputPort;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import br.com.banco.gestao_contas.port.output.AtualizarSaldoOutputPort;
import br.com.banco.gestao_contas.port.output.ContaRepositoryOutputPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public ConsultaContasInputPort consultaContasInputPort(ContaRepositoryOutputPort contaRepositoryOutputPort) {
        return new ConsultaContasUseCase(contaRepositoryOutputPort);
    }

    @Bean
    public AtualizarSaldoInputPort atualizarSaldoInputPort(AtualizarSaldoOutputPort atualizarSaldoOutputPort) {
        return new AtualizarSaldoUseCase(atualizarSaldoOutputPort);
    }
}