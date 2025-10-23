package com.nimble.gatewaypagamento.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusPagamento {
    PENDENTE,
    CONCLUIDO,
    CANCELADO;

    @JsonCreator
    public static StatusPagamento from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        try {
            return StatusPagamento.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para StatusPagamento: " + value, e);
        }
    }
}