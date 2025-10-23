package com.nimble.gatewaypagamento.exception.pagamento;

import com.nimble.gatewaypagamento.exception.GatewayPagamentoException;
import org.springframework.http.HttpStatus;

public class SaldoInsuficienteException extends GatewayPagamentoException {
    public SaldoInsuficienteException(String mensagem) {
        super(mensagem, HttpStatus.BAD_REQUEST);
    }
}
