package com.nimble.gatewaypagamento.dto.pagamento;

import java.math.BigDecimal;

public record RespostaPagamentoDTO(
        Long idPagamento,
        Long idCobranca,
        String status,
        BigDecimal valor,
        String pagador,
        String destinatario
) {}