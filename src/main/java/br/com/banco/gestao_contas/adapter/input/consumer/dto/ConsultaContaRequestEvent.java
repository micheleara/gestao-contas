package br.com.banco.gestao_contas.adapter.input.consumer.dto;

public record ConsultaContaRequestEvent(
        String correlationId,
        String numeroConta
) {}