package com.nimble.gatewaypagamento.config;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.support.FormattingConversionService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnumConfigTest {

    private FormattingConversionService conversionService;

    @BeforeEach
    void setUp() {
        EnumConfig enumConfig = new EnumConfig();
        conversionService = new FormattingConversionService();
        enumConfig.addFormatters(conversionService);
    }

    @Test
    void deveConverterStringParaEnumIgnorandoCase() {
        StatusCobranca status = conversionService.convert("pendente", StatusCobranca.class);
        assertEquals(StatusCobranca.PENDENTE, status);
    }

    @Test
    void deveConverterStringEmMaiusculo() {
        StatusCobranca status = conversionService.convert("PAGA", StatusCobranca.class);
        assertEquals(StatusCobranca.PAGA, status);
    }
}