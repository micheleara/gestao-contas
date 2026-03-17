package br.com.banco.gestao_contas.adapter.output.repository;

import br.com.banco.gestao_contas.port.output.AtualizarSaldoOutputPort;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
@Transactional
public class AtualizarSaldoRepository implements AtualizarSaldoOutputPort {

    @PersistenceContext
    private EntityManager em;

    @Override
    public void atualizar(String numeroConta, BigDecimal valorFinal) {
        em.createNativeQuery(
                "SELECT saldo FROM contas WHERE num_conta = :conta FOR UPDATE")
                .setParameter("conta", numeroConta)
                .getSingleResult();

        em.createNativeQuery(
                "UPDATE contas SET saldo = saldo + :valor, atualizado_em = now() WHERE num_conta = :conta")
                .setParameter("valor", valorFinal)
                .setParameter("conta", numeroConta)
                .executeUpdate();
    }
}