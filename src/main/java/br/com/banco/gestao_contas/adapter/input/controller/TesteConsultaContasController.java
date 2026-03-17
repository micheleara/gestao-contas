package br.com.banco.gestao_contas.adapter.input.controller;

import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/consulta-contas")
public class TesteConsultaContasController {

    private final ConsultaContasInputPort consultaContasInputPort;

    public TesteConsultaContasController(ConsultaContasInputPort consultaContasInputPort) {
        this.consultaContasInputPort = consultaContasInputPort;
    }

    @GetMapping
    public ResponseEntity<Conta> consultarConta(@RequestParam("num_conta") String numConta) {
        Optional<Conta> conta = consultaContasInputPort.consultarPorNumero(numConta);
        return conta.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}