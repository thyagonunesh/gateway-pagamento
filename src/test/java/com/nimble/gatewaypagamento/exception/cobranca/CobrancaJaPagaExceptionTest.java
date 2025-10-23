package com.nimble.gatewaypagamento.exception.cobranca;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CobrancaJaPagaExceptionTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Cobrança já está paga";
        CobrancaJaPagaException exception = new CobrancaJaPagaException(mensagem);

        assertEquals(mensagem, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
