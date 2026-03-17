package br.com.banco.gestao_contas.adapter.input.controller;

import br.com.banco.gestao_contas.core.domain.model.Conta;
import br.com.banco.gestao_contas.core.domain.model.StatusConta;
import br.com.banco.gestao_contas.port.input.ConsultaContasInputPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultaContasController.class)
class TesteConsultaContasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConsultaContasInputPort consultaContasInputPort;

    @Test
    void deveRetornarContaQuandoEncontrada() throws Exception {
        Conta conta = new Conta("12345", "Fulano", StatusConta.ATIVA, new BigDecimal("100.50"), LocalDateTime.now());
        Mockito.when(consultaContasInputPort.consultarPorNumero("12345")).thenReturn(Optional.of(conta));

        mockMvc.perform(get("/api/v1/consulta-contas")
                        .param("num_conta", "12345")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numConta").value("12345"))
                .andExpect(jsonPath("$.nomeCliente").value("Fulano"));
    }

    @Test
    void deveRetornarNotFoundQuandoContaNaoExistir() throws Exception {
        Mockito.when(consultaContasInputPort.consultarPorNumero("99999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/consulta-contas")
                        .param("num_conta", "99999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}