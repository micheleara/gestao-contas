package br.com.banco.gestao_contas.adapter.output.repository;

import br.com.banco.gestao_contas.adapter.output.repository.entity.ContaEntity;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpringDataContaRepository extends JpaRepository<ContaEntity, String> {

    Optional<ContaEntity> findByNumConta(String numConta);

    List<ContaEntity> findByStatus(StatusConta status);
}