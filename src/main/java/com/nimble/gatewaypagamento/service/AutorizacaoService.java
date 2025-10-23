package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class AutorizacaoService {

    private final WebClient webClient;

    public AutorizacaoService(WebClient webClient) {
        this.webClient = webClient;
    }

    public boolean autorizarPagamentoCartao(String numero, String validade, String cvv, BigDecimal valor) {
        Map<String, Object> request = Map.of(
                "numero", numero,
                "validade", validade,
                "cvv", cvv,
                "valor", valor
        );

        return chamarAutorizador(request);
    }

    public boolean autorizarDeposito(BigDecimal valor) {
        Map<String, Object> request = Map.of("valor", valor);
        return chamarAutorizador(request);
    }

    private boolean chamarAutorizador(Map<String, Object> request) {
        try {
            UriComponentsBuilder uriBuilder = UriComponentsBuilder
                    .fromUriString("https://zsy6tx7aql.execute-api.sa-east-1.amazonaws.com/authorizer");
            request.forEach(uriBuilder::queryParam);

            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder.build().toUri())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block();

            Map<String, Object> data = (Map<String, Object>) response.get("data");
            return Boolean.TRUE.equals(data.get("authorized"));
        } catch (WebClientResponseException e) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Erro ao chamar autorizador: " + e.getMessage());
        } catch (Exception e) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Falha inesperada ao processar autorização");
        }
    }

}
