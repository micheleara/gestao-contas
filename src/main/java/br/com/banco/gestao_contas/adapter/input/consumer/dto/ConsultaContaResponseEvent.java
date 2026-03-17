package br.com.banco.gestao_contas.adapter.input.consumer.dto;

import java.math.BigDecimal;

public record ConsultaContaResponseEvent(
        String correlationId,
        String numeroConta,
        String nomeCliente,
        String status,
        BigDecimal saldo
) {}