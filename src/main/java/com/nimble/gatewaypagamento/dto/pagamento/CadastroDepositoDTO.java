package com.nimble.gatewaypagamento.dto.pagamento;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CadastroDepositoDTO(

        @NotNull(message = "O valor do depósito é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor mínimo do depósito é 0.01")
        @Digits(integer = 12, fraction = 2, message = "Valor do depósito inválido inválido")
        BigDecimal valor

) {}
