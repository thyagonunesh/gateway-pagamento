package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.service.CobrancaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cobranças", description = "Endpoints para criar, listar e cancelar cobranças")
public class CobrancaController {

    private final CobrancaService cobrancaService;

    @Operation(summary = "Criar uma nova cobrança")
    @PostMapping
    public ResponseEntity<RespostaCobrancaDTO> criarCobranca(@RequestBody @Valid CadastroCobrancaDTO dto,
                                                             @AuthenticationPrincipal String cpfOriginador) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cobrancaService.salvar(cpfOriginador, dto));
    }

    @Operation(summary = "Listar cobranças enviadas")
    @GetMapping("/enviadas")
    public ResponseEntity<List<RespostaCobrancaDTO>> listarEnviadas(@RequestParam StatusCobranca status,
                                                                    @AuthenticationPrincipal String cpfOriginador) {
        return ResponseEntity.ok(cobrancaService.listarEnviadas(cpfOriginador, status));
    }

    @Operation(summary = "Listar cobranças recebidas")
    @GetMapping("/recebidas")
    public ResponseEntity<List<RespostaCobrancaDTO>> listarRecebidas(@AuthenticationPrincipal String cpfDestinatario,
                                                                     @RequestParam StatusCobranca status) {
        return ResponseEntity.ok(cobrancaService.listarRecebidas(cpfDestinatario, status));
    }

    @Operation(summary = "Cancelar uma cobrança")
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<RespostaCobrancaDTO> cancelar(@PathVariable Long id,
                                                        @AuthenticationPrincipal String cpfCancelador) {
        return ResponseEntity.ok(cobrancaService.cancelarCobranca(id, cpfCancelador));
    }

}