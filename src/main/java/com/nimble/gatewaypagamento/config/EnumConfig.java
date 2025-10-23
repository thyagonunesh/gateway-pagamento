package com.nimble.gatewaypagamento.config;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new Converter<String, StatusCobranca>() {
            @Override
            public StatusCobranca convert(String source) {
                if (source == null) {
                    return null;
                }
                if (source.isBlank()) {
                    return null;
                }
                return StatusCobranca.valueOf(source.toUpperCase());
            }
        });
    }
}