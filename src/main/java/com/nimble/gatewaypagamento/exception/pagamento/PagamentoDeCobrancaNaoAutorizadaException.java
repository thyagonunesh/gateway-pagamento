package com.nimble.gatewaypagamento.exception.pagamento;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class PagamentoDeCobrancaNaoAutorizadaException extends GatewayPagamentoException {
    public PagamentoDeCobrancaNaoAutorizadaException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST);
    }
}
