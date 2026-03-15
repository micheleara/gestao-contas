package br.com.banco.gestao_contas.adapters.in.kafka.dto;

public record ConsultaContaRequestEvent(
        String correlationId,
        String numeroConta
) {}