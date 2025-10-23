package com.nimble.gatewaypagamento.exception.cobranca;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class CobrancaJaPagaException extends GatewayPagamentoException {
    public CobrancaJaPagaException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST);
    }
}
