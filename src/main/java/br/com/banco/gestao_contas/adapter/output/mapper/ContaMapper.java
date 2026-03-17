package br.com.banco.gestao_contas.adapter.output.mapper;

import br.com.banco.gestao_contas.adapter.output.repository.entity.ContaEntity;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {

    public Conta toDomain(ContaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Conta(
                entity.getNumConta(),
                entity.getNomeCliente(),
                entity.getStatus(),
                entity.getSaldo(),
                entity.getAtualizadoEm()
        );
    }

    public ContaEntity toEntity(Conta domain) {
        if (domain == null) {
            return null;
        }
        return new ContaEntity(
                domain.getNumConta(),
                domain.getNomeCliente(),
                domain.getStatus(),
                domain.getSaldo(),
                domain.getAtualizadoEm()
        );
    }
}