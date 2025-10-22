package com.nimble.gatewaypagamento.config;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class EnumConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, StatusCobranca.class,
                source -> StatusCobranca.valueOf(source.toUpperCase()));
    }
}