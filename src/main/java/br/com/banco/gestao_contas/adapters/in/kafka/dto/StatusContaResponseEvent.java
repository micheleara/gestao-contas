package br.com.banco.gestao_contas.adapters.in.kafka.dto;

import java.math.BigDecimal;

public record StatusContaResponseEvent(
        String numConta,
        String nomeCliente,
        String status,
        BigDecimal saldo
) {}