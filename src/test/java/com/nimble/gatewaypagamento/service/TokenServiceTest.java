package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.Funcao;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;
    private String chaveBase64;

    @BeforeEach
    void setup() {
        tokenService = new TokenService();
        chaveBase64 = Base64.getEncoder().encodeToString("minhaChaveDeTesteBemBrandeESegura123456".getBytes());
        ReflectionTestUtils.setField(tokenService, "chaveSecreta", chaveBase64);
        ReflectionTestUtils.setField(tokenService, "expiracao", 100000L);
    }

    @Test
    void deveGerarTokenValido() {
        Usuario usuario = Usuario.builder()
                .cpf("12345678900")
                .email("teste@email.com")
                .funcao(Funcao.USUARIO)
                .build();

        String token = tokenService.gerarToken(usuario);
        assertNotNull(token);

        Claims claims = Jwts.parser()
                .setSigningKey(chaveBase64)
                .parseClaimsJws(token)
                .getBody();

        assertEquals("12345678900", claims.getSubject());
        assertEquals("teste@email.com", claims.get("email"));
        assertEquals("USUARIO", claims.get("papel"));
        assertTrue(claims.getExpiration().after(new Date()));
    }
}
