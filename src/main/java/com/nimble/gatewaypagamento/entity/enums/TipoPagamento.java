package com.nimble.gatewaypagamento.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoPagamento {
    SALDO,
    CARTAO;

    @JsonCreator
    public static TipoPagamento from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        try {
            return TipoPagamento.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para TipoPagamento: " + value, e);
        }
    }
}