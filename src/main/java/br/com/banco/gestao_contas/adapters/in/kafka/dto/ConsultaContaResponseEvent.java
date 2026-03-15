package br.com.banco.gestao_contas.adapters.in.kafka.dto;

import java.math.BigDecimal;

public record ConsultaContaResponseEvent(
        String correlationId,
        String numeroConta,
        String nomeCliente,
        String status,
        BigDecimal saldo
) {}