package com.nimble.gatewaypagamento.exception.pagamento;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class PagamentoNaoEncontradoException extends GatewayPagamentoException {
    public PagamentoNaoEncontradoException(String mensagem) {
        super(mensagem, HttpStatus.NOT_FOUND);
    }
}
