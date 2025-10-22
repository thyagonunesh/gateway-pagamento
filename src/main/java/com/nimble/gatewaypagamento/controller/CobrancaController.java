package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.service.CobrancaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cobrancas")
@RequiredArgsConstructor
public class CobrancaController {

    private final CobrancaService cobrancaService;

    @PostMapping
    public ResponseEntity<CobrancaResponseDTO> criarCobranca(@RequestBody @Valid CadastroCobrancaDTO dto) {
        String cpfOriginador = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobrancaService.criarCobranca(cpfOriginador, dto));
    }

    @GetMapping("/enviadas")
    public ResponseEntity<List<CobrancaResponseDTO>> listarEnviadas(@RequestParam StatusCobranca status) {
        String cpfOriginador = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(cobrancaService.listarEnviadas(cpfOriginador, status));
    }

    @GetMapping("/recebidas")
    public ResponseEntity<List<CobrancaResponseDTO>> listarRecebidas(@RequestHeader("cpfDestinatario") String cpfDestinatario, @RequestParam StatusCobranca status) {
        return ResponseEntity.ok(cobrancaService.listarRecebidas(cpfDestinatario, status));
    }
}