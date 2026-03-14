package br.com.banco.gestao_contas;

import br.com.banco.gestao_contas.domain.Exemplo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GestaoContasApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationMainRuns() {
		GestaoContasApplication.main(new String[]{"--spring.main.web-application-type=none"});
	}

	@Test
	void exemploMainRuns() {
		Exemplo.main(new String[]{});
	}

}
