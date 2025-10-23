package com.nimble.gatewaypagamento.dto.cobranca;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RespostaCobrancaDTO(

        Long id,
        String cpfOriginador,
        String cpfDestinatario,
        BigDecimal valor,
        String descricao,
        StatusCobranca status,
        LocalDateTime dataCriacao

) {
}