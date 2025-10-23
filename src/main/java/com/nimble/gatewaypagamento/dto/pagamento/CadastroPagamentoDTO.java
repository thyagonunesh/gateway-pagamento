package com.nimble.gatewaypagamento.dto.pagamento;

import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CadastroPagamentoDTO(
        @NotNull(message = "Id da cobrança é obrigatório")
        Long idCobranca,

        @NotNull(message = "Tipo de pagamento é obrigatório")
        TipoPagamento tipoPagamento,

        // Campos do cartão: validados apenas no formato, podem ser nulos se não for pagamento por cartão
        @Pattern(regexp = "\\d{13,20}", message = "Número do cartão inválido")
        String numeroCartao,

        @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Validade do cartão inválida")
        String validadeCartao,

        @Pattern(regexp = "\\d{3}", message = "CVV inválido")
        String cvv
) {}
