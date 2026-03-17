CREATE TABLE IF NOT EXISTS lancamentos_processados (
    id_lancamento VARCHAR(100) NOT NULL,
    processado_em TIMESTAMP    NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_lancamentos_processados PRIMARY KEY (id_lancamento)
);