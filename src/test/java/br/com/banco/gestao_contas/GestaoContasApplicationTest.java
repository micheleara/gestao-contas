package br.com.banco.gestao_contas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GestaoContasApplicationTest {

    @Test
    void contextLoads() {
        // Apenas valida que o contexto sobe e a aplicação carrega com sucesso
        GestaoContasApplication.main(new String[] {});
    }
}
