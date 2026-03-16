package br.com.banco.gestao_contas.application.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class AtualizarSaldoService {

    @PersistenceContext
    private EntityManager em;

    public void atualizar(String numeroConta, String tipoLancamento, BigDecimal valor) {

        em.createNativeQuery(
                "SELECT saldo FROM contas WHERE num_conta = :conta FOR UPDATE")
                .setParameter("conta", numeroConta)
                .getSingleResult();

        BigDecimal valorOperacao = tipoLancamento.equals("DEBITO")
                ? valor.negate()
                : valor;

        em.createNativeQuery(
                "UPDATE contas SET saldo = saldo + :valor, atualizado_em = now() WHERE num_conta = :conta")
                .setParameter("valor", valorOperacao)
                .setParameter("conta", numeroConta)
                .executeUpdate();
    }
}