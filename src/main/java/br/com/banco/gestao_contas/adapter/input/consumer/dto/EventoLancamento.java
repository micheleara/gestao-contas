package br.com.banco.gestao_contas.adapter.input.consumer.dto;

import java.math.BigDecimal;

public record EventoLancamento(
        String idLancamento,
        String numeroConta,
        String tipoLancamento,
        BigDecimal valor
) {}