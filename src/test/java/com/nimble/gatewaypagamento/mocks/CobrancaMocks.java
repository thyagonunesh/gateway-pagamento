package com.nimble.gatewaypagamento.mocks;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CobrancaMocks {

    public static RespostaCobrancaDTO criarCobrancaResponseDTO() {
        return new RespostaCobrancaDTO(
                1L,
                "11111111111",
                "22222222222",
                new BigDecimal("100.00"),
                "Descrição teste",
                StatusCobranca.PENDENTE,
                LocalDateTime.now()
        );
    }

    public static CadastroCobrancaDTO criarCadastroCobrancaDTO() {
        return new CadastroCobrancaDTO(
                "12345678900",
                new BigDecimal("100.0"),
                "Teste Descrição"
        );
    }

}
