package com.nimble.gatewaypagamento.exception.usuario;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class SenhaIncorretaException extends GatewayPagamentoException {
    public SenhaIncorretaException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST);
    }
}
