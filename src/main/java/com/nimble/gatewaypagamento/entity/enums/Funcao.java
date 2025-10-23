package com.nimble.gatewaypagamento.entity.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Funcao {
    USUARIO,
    ADMIN;

    @JsonCreator
    public static Funcao from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Valor não pode ser nulo");
        }
        try {
            return Funcao.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Valor inválido para Funcao: " + value, e);
        }
    }
}