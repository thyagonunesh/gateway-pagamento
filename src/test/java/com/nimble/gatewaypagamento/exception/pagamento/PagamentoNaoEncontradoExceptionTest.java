package com.nimble.gatewaypagamento.exception.pagamento;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoNaoEncontradoExceptionTest {

    @Test
    void deveCriarExceptionComMensagemEStatus() {
        String mensagem = "Pagamento não encontrado";
        PagamentoNaoEncontradoException ex = new PagamentoNaoEncontradoException(mensagem);

        assertEquals(mensagem, ex.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }
}