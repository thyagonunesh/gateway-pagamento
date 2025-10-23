package com.nimble.gatewaypagamento.exception.usuario;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class UsuarioNaoEncontradoException extends GatewayPagamentoException {
    public UsuarioNaoEncontradoException(String mensagem) {
        super(mensagem, HttpStatus.NOT_FOUND);
    }
}
