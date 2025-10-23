package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AutorizacaoServiceTest {

    private WebClient webClient;
    private AutorizacaoService autorizacaoService;

    private RequestHeadersUriSpec uriSpec; // raw type
    private ResponseSpec responseSpec;

    @BeforeEach
    void setup() {
        webClient = mock(WebClient.class);
        uriSpec = mock(RequestHeadersUriSpec.class);
        responseSpec = mock(ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(java.net.URI.class))).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);

        autorizacaoService = new AutorizacaoService(webClient);
    }

    @Test
    void deveAutorizarPagamentoCartaoQuandoRespostaTrue() {
        Map<String, Object> response = Map.of(
                "data", Map.of("authorized", true),
                "status", "success"
        );
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenReturn(Mono.just(response));

        boolean autorizado = autorizacaoService.autorizarPagamentoCartao("1234", "12/30", "123", BigDecimal.TEN);
        assertTrue(autorizado);
    }

    @Test
    void naoDeveAutorizarPagamentoCartaoQuandoRespostaFalse() {
        Map<String, Object> response = Map.of(
                "data", Map.of("authorized", false),
                "status", "fail"
        );
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenReturn(Mono.just(response));

        boolean autorizado = autorizacaoService.autorizarPagamentoCartao("1234", "12/30", "123", BigDecimal.TEN);
        assertFalse(autorizado);
    }

    @Test
    void deveAutorizarDepositoQuandoRespostaTrue() {
        Map<String, Object> response = Map.of(
                "data", Map.of("authorized", true),
                "status", "success"
        );
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenReturn(Mono.just(response));

        boolean autorizado = autorizacaoService.autorizarDeposito(BigDecimal.valueOf(100));
        assertTrue(autorizado);
    }

    @Test
    void naoDeveAutorizarDepositoQuandoRespostaFalse() {
        Map<String, Object> response = Map.of(
                "data", Map.of("authorized", false),
                "status", "fail"
        );
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenReturn(Mono.just(response));

        boolean autorizado = autorizacaoService.autorizarDeposito(BigDecimal.valueOf(100));
        assertFalse(autorizado);
    }

    @Test
    void deveLancarExcecaoQuandoWebClientFalha() {
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenThrow(new RuntimeException("Falha HTTP"));

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class, () ->
                autorizacaoService.autorizarPagamentoCartao("1234", "12/30", "123", BigDecimal.TEN));
    }

    @Test
    void deveLancarExcecaoQuandoWebClientResponseException() {
        WebClient webClient = mock(WebClient.class);
        AutorizacaoService autorizacaoService = spy(new AutorizacaoService(webClient));

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri((URI) any())).thenReturn(uriSpec);
        when(uriSpec.retrieve()).thenReturn(responseSpec);

        // Simula WebClientResponseException
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenThrow(WebClientResponseException.create(400, "Bad Request", null, null, null));

        PagamentoDeCobrancaNaoAutorizadaException ex = assertThrows(
                PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> autorizacaoService.autorizarDeposito(BigDecimal.TEN)
        );

        assertTrue(ex.getMessage().contains("Erro ao chamar autorizador"));
    }

}
