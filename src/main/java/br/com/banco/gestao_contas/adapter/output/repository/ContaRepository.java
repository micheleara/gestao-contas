package br.com.banco.gestao_contas.adapter.output.repository;

import br.com.banco.gestao_contas.adapter.output.mapper.ContaMapper;
import br.com.banco.gestao_contas.adapter.output.repository.entity.ContaEntity;
import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import br.com.banco.gestao_contas.port.output.ContaRepositoryOutputPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ContaRepository implements ContaRepositoryOutputPort {

    private final SpringDataContaRepository repository;
    private final ContaMapper mapper;

    public ContaRepository(SpringDataContaRepository repository, ContaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Conta> findByNumConta(String numConta) {
        return repository.findByNumConta(numConta).map(mapper::toDomain);
    }

    @Override
    public List<Conta> findByStatus(StatusConta status) {
        return repository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Conta save(Conta conta) {
        ContaEntity entity = mapper.toEntity(conta);
        ContaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public long count() {
        return repository.count();
    }
}