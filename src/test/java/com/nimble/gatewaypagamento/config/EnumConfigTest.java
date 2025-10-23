package com.nimble.gatewaypagamento.config;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.format.support.FormattingConversionService;

import static org.junit.jupiter.api.Assertions.*;

class EnumConfigTest {

    private FormattingConversionService conversionService;

    @BeforeEach
    void setUp() {
        conversionService = new FormattingConversionService();
        new EnumConfig().addFormatters(conversionService);
    }

    @Test
    void deveRetornarNullSeSourceForNull() {
        assertNull(conversionService.convert(null, StatusCobranca.class));
    }

    @Test
    void deveRetornarNullSeSourceForBlank() {
        assertNull(conversionService.convert("   ", StatusCobranca.class));
    }

    @Test
    void deveConverterStringValida() {
        assertEquals(StatusCobranca.PENDENTE, conversionService.convert("pendente", StatusCobranca.class));
        assertEquals(StatusCobranca.PAGA, conversionService.convert("PAGA", StatusCobranca.class));
        assertEquals(StatusCobranca.CANCELADA, conversionService.convert("cAnCelada", StatusCobranca.class));
    }

    @Test
    void deveLancarExceptionParaValorInvalido() {
        assertThrows(ConversionFailedException.class,
                () -> conversionService.convert("invalid", StatusCobranca.class));
    }
}