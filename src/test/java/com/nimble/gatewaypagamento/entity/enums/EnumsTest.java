package com.nimble.gatewaypagamento.entity.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnumsTest {

    @Test
    void deveConverterStringsValidas() {
        assertEquals(TipoPagamento.SALDO, TipoPagamento.from("saldo"));
        assertEquals(Funcao.ADMIN, Funcao.from("admin"));
        assertEquals(StatusPagamento.CONCLUIDO, StatusPagamento.from("concluido"));
        assertEquals(StatusCobranca.PENDENTE, StatusCobranca.from("pendente"));
        assertEquals(StatusCobranca.PAGA, StatusCobranca.from("PAGA"));
        assertEquals(StatusCobranca.CANCELADA, StatusCobranca.from("Cancelada"));
    }

    @Test
    void deveLancarExceptionParaValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> TipoPagamento.from("invalid"));
        assertThrows(IllegalArgumentException.class, () -> Funcao.from("invalid"));
        assertThrows(IllegalArgumentException.class, () -> StatusPagamento.from("invalid"));
        assertThrows(IllegalArgumentException.class, () -> StatusCobranca.from("invalid"));
    }

    @Test
    void deveLancarExceptionParaNull() {
        assertThrows(IllegalArgumentException.class, () -> TipoPagamento.from(null));
        assertThrows(IllegalArgumentException.class, () -> Funcao.from(null));
        assertThrows(IllegalArgumentException.class, () -> StatusPagamento.from(null));
        assertThrows(IllegalArgumentException.class, () -> StatusCobranca.from(null));
    }
}