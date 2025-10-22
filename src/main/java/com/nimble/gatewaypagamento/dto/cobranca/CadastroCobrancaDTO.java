package com.nimble.gatewaypagamento.dto.cobranca;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

public record CadastroCobrancaDTO(

        @NotBlank(message = "CPF do destinatário é obrigatório")
        @CPF(message = "CPF inválido")
        String cpfDestinatario,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor mínimo é 0.01")
        @Digits(integer = 12, fraction = 2, message = "Valor inválido")
        BigDecimal valor,

        @Size(max = 255, message = "Descrição deve ter no máximo 255 caracteres")
        String descricao

) {
}
