package com.nimble.gatewaypagamento.exception.cobranca;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class OriginadorInvalidoException extends GatewayPagamentoException {
    public OriginadorInvalidoException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
