package com.nimble.gatewaypagamento.exception.usuario;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class UsuarioJaCadastradoException extends GatewayPagamentoException {
    public UsuarioJaCadastradoException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST);
    }
}
