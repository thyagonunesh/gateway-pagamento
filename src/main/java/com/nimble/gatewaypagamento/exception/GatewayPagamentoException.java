package com.nimble.gatewaypagamento.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public class GatewayPagamentoException extends RuntimeException {

    @Getter
    private final HttpStatus status;

    protected GatewayPagamentoException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}