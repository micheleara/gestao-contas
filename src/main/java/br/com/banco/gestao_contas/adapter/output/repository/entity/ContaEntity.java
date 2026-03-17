package br.com.banco.gestao_contas.adapter.output.repository.entity;

import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contas")
public class ContaEntity {

    @Id
    @Column(name = "num_conta", length = 20)
    private String numConta;

    @Column(name = "nome_cliente", nullable = false, length = 100)
    private String nomeCliente;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusConta status;

    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public ContaEntity() {
    }

    public ContaEntity(String numConta, String nomeCliente, StatusConta status, BigDecimal saldo, LocalDateTime atualizadoEm) {
        this.numConta = numConta;
        this.nomeCliente = nomeCliente;
        this.status = status;
        this.saldo = saldo;
        this.atualizadoEm = atualizadoEm;
    }

    public String getNumConta() { return numConta; }
    public void setNumConta(String numConta) { this.numConta = numConta; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}