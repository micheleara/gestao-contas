package br.com.banco.gestao_contas.adapters.in.kafka.dto;

import java.math.BigDecimal;

public record EventoLancamento(
        String idLancamento,
        String numeroConta,
        String tipoLancamento,
        BigDecimal valor
) {}