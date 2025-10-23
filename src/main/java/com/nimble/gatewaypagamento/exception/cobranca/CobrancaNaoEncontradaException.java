package com.nimble.gatewaypagamento.exception.cobranca;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class CobrancaNaoEncontradaException extends GatewayPagamentoException {
    public CobrancaNaoEncontradaException(String mensagem) {
        super(mensagem, HttpStatus.NOT_FOUND);
    }
}
