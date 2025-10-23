package com.nimble.gatewaypagamento.exception.cobranca;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OriginadorInvalidoExceptionTest {

    @Test
    void deveCriarExcecaoComMensagem() {
        String mensagem = "Originador inválido";
        OriginadorInvalidoException exception = new OriginadorInvalidoException(mensagem);

        assertEquals(mensagem, exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
