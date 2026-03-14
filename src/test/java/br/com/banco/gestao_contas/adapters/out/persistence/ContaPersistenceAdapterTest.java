package br.com.banco.gestao_contas.adapters.out.persistence;

import br.com.banco.gestao_contas.application.ports.out.ContaRepositoryPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ContaPersistenceAdapterTest {

    @Autowired
    private ContaRepositoryPort contaRepositoryPort;

    @Test
    void deveConectarAoBancoHexagonal() {
        long total = contaRepositoryPort.count();
        assertNotNull(total);
        System.out.println("✅ Conexão Hexagonal com banco OK. Total de contas: " + total);
    }
}
