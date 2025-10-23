package com.nimble.gatewaypagamento.exception.pagamento;

public class PagamentoDeCobrancaNaoAutorizadaException extends RuntimeException {
    public PagamentoDeCobrancaNaoAutorizadaException(String mensagem) {
        super(mensagem);
    }
}
