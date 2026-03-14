package br.com.banco.gestao_contas.adapters.in.controller;

import br.com.banco.gestao_contas.application.ports.in.ConsultaContasUseCase;
import br.com.banco.gestao_contas.domain.model.Conta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/consulta-contas")
public class TesteConsultaContasController {

    private final ConsultaContasUseCase consultaContasUseCase;

    public TesteConsultaContasController(ConsultaContasUseCase consultaContasUseCase) {
        this.consultaContasUseCase = consultaContasUseCase;
    }

    @GetMapping
    public ResponseEntity<Conta> consultarConta(@RequestParam("num_conta") String numConta) {
        Optional<Conta> conta = consultaContasUseCase.consultarPorNumero(numConta);
        return conta.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
