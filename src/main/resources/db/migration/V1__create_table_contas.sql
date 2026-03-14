CREATE TABLE IF NOT EXISTS contas (
    num_conta       VARCHAR(20)     NOT NULL,
    nome_cliente    VARCHAR(100)    NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    saldo           DECIMAL(15,2)   NOT NULL DEFAULT 0.00,
    atualizado_em   TIMESTAMP       NOT NULL DEFAULT NOW(),

    CONSTRAINT pk_contas PRIMARY KEY (num_conta),
    CONSTRAINT chk_status CHECK (status IN ('ATIVA', 'INATIVA', 'BLOQUEADA', 'ENCERRADA')),
    CONSTRAINT chk_saldo CHECK (saldo >= 0)
);

CREATE INDEX IF NOT EXISTS idx_contas_status ON contas (status);
