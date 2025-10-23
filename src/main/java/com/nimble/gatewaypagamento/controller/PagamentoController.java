package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.pagamento.CadastroDepositoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.CadastroPagamentoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.RespostaPagamentoDTO;
import com.nimble.gatewaypagamento.service.PagamentoService;
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
public class PagamentoController {

    private final PagamentoService pagamentoService;

    @PostMapping("/pagar")
    public ResponseEntity<RespostaPagamentoDTO> pagarCobranca(@RequestBody @Valid CadastroPagamentoDTO dto,
                                                              @AuthenticationPrincipal String cpfPagador) {
        RespostaPagamentoDTO respostaPagamentoDTO = pagamentoService.pagarCobranca(dto, cpfPagador);
        return ResponseEntity.ok(respostaPagamentoDTO);
    }

    @PostMapping("/deposito")
    public ResponseEntity<String> depositarSaldo(@RequestBody @Valid CadastroDepositoDTO dto,
                                                 @AuthenticationPrincipal String cpf) {
        pagamentoService.depositarSaldo(dto.valor(), cpf);
        return ResponseEntity.ok("Depósito realizado com sucesso");
    }

}