package com.nimble.gatewaypagamento.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveRetornar401QuandoTokenAusente() throws Exception {
        mockMvc.perform(get("/alguma-rota-protegida"))
                .andExpect(status().isUnauthorized())
                .andExpect(status().reason("Token ausente ou inválido"));
    }
}
