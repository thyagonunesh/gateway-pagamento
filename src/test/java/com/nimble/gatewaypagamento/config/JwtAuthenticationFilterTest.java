package com.nimble.gatewaypagamento.config;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter filter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    private final String secret = "r8F2qT5yV3n1bJx9mZQ4c7uK2sN5vL6wR0yA1pB8dEo=";
    private final String validToken = Jwts.builder()
            .setSubject("12345678900")
            .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, "r8F2qT5yV3n1bJx9mZQ4c7uK2sN5vL6wR0yA1pB8dEo=")
            .compact();

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter();
        ReflectionTestUtils.setField(filter, "chaveSecreta", secret); // seta o campo private

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveAutenticarQuandoTokenValido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);

        filter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("12345678900", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void deveContinuarQuandoNaoHaHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void deveRetornar401QuandoTokenInvalido() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer tokenInvalido");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        assertTrue(sw.toString().contains("Token inválido ou expirado"));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void deveContinuarQuandoHeaderNaoComecaComBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token qualquer"); // não começa com Bearer

        filter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, times(1)).doFilter(request, response);
    }

}
