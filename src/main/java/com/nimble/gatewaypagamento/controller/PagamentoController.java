package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.pagamento.CadastroDepositoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.CadastroPagamentoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.RespostaPagamentoDTO;
import com.nimble.gatewaypagamento.service.PagamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pagamentos")
@RequiredArgsConstructor
@Tag(name = "Pagamentos", description = "Endpoints de pagamento e depósito")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @Operation(summary = "Pagar uma cobrança")
    @PostMapping("/pagar")
    public ResponseEntity<RespostaPagamentoDTO> pagarCobranca(@RequestBody @Valid CadastroPagamentoDTO dto,
                                                              @AuthenticationPrincipal String cpfPagador) {
        return ResponseEntity.ok(pagamentoService.pagarCobranca(dto, cpfPagador));
    }

    @Operation(summary = "Depositar saldo na conta do usuário")
    @PostMapping("/deposito")
    public ResponseEntity<String> depositarSaldo(@RequestBody @Valid CadastroDepositoDTO dto,
                                                 @AuthenticationPrincipal String cpf) {
        pagamentoService.depositarSaldo(dto.valor(), cpf);
        return ResponseEntity.ok("Depósito realizado com sucesso");
    }

}