package com.nimble.gatewaypagamento.config;

import com.nimble.gatewaypagamento.GatewayPagamentoApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GatewayPagamentoApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // força reinicialização do contexto
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // Garante que não haja autenticação antes de cada teste
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @AfterEach
    void cleanup() {
        // Limpa o SecurityContext após cada teste
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }

    @Test
    void deveRetornar401QuandoTokenAusente() throws Exception {
        mockMvc.perform(get("/alguma-rota-protegida")
                        .with(anonymous())) // remove autenticação anônima
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        mockMvc.perform(get("/alguma-rota-protegida")
                        .header("Authorization", "Bearer token-invalido"))
                .andExpect(status().isUnauthorized()); // espera 401
    }

    // Aqui você pode adicionar outros testes de segurança, como token válido, roles, etc.
}
