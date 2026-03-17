package br.com.banco.gestao_contas.adapter.input.consumer.dto;

import java.math.BigDecimal;

public record StatusContaResponseEvent(
        String numConta,
        String nomeCliente,
        String status,
        BigDecimal saldo
) {}