package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.service.CobrancaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static com.nimble.gatewaypagamento.mocks.CobrancaMocks.criarCadastroCobrancaDTO;
import static com.nimble.gatewaypagamento.mocks.CobrancaMocks.criarCobrancaResponseDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaControllerTest {

    @Mock
    private CobrancaService cobrancaService;

    @InjectMocks
    private CobrancaController cobrancaController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("12345678900", null)
        );
    }

    @Test
    void deveCriarCobrancaComSucesso() {
        CadastroCobrancaDTO dto = criarCadastroCobrancaDTO();
        CobrancaResponseDTO response = criarCobrancaResponseDTO();

        when(cobrancaService.criarCobranca(anyString(), any(CadastroCobrancaDTO.class)))
                .thenReturn(response);

        ResponseEntity<CobrancaResponseDTO> result = cobrancaController.criarCobranca(dto);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(cobrancaService).criarCobranca(eq("12345678900"), eq(dto));
    }

    @Test
    void deveListarEnviadasComSucesso() {
        List<CobrancaResponseDTO> lista = List.of(criarCobrancaResponseDTO());
        when(cobrancaService.listarEnviadas(anyString(), any())).thenReturn(lista);

        ResponseEntity<List<CobrancaResponseDTO>> result =
                cobrancaController.listarEnviadas(StatusCobranca.PENDENTE);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(lista, result.getBody());
        verify(cobrancaService).listarEnviadas(eq("12345678900"), eq(StatusCobranca.PENDENTE));
    }

    @Test
    void deveListarRecebidasComSucesso() {
        List<CobrancaResponseDTO> lista = List.of(criarCobrancaResponseDTO());
        when(cobrancaService.listarRecebidas(anyString(), any())).thenReturn(lista);

        ResponseEntity<List<CobrancaResponseDTO>> result =
                cobrancaController.listarRecebidas("98765432100", StatusCobranca.PAGA);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(lista, result.getBody());
        verify(cobrancaService).listarRecebidas(eq("98765432100"), eq(StatusCobranca.PAGA));
    }

}
