package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.service.CobrancaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cobrancas")
@RequiredArgsConstructor
public class CobrancaController {

    private final CobrancaService cobrancaService;

    @PostMapping
    public ResponseEntity<RespostaCobrancaDTO> criarCobranca(@RequestBody @Valid CadastroCobrancaDTO dto,
                                                             @AuthenticationPrincipal String cpfOriginador) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobrancaService.cadastrar(cpfOriginador, dto));
    }

    @GetMapping("/enviadas")
    public ResponseEntity<List<RespostaCobrancaDTO>> listarEnviadas(@RequestParam StatusCobranca status,
                                                                    @AuthenticationPrincipal String cpfOriginador) {
        return ResponseEntity.ok(cobrancaService.listarEnviadas(cpfOriginador, status));
    }

    @GetMapping("/recebidas")
    public ResponseEntity<List<RespostaCobrancaDTO>> listarRecebidas(@AuthenticationPrincipal String cpfDestinatario, @RequestParam StatusCobranca status) {
        return ResponseEntity.ok(cobrancaService.listarRecebidas(cpfDestinatario, status));
    }
}