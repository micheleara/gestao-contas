package br.com.banco.gestao_contas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Conta {

    private String numConta;
    private String nomeCliente;
    private StatusConta status;
    private BigDecimal saldo;
    private LocalDateTime atualizadoEm;

    public Conta() {
    }

    public Conta(String numConta, String nomeCliente, StatusConta status, BigDecimal saldo, LocalDateTime atualizadoEm) {
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
