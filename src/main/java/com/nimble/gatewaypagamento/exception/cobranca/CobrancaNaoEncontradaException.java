package com.nimble.gatewaypagamento.exception.cobranca;

public class CobrancaNaoEncontradaException extends RuntimeException {
    public CobrancaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
