package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
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
        RespostaCobrancaDTO response = criarCobrancaResponseDTO();

        when(cobrancaService.salvar(anyString(), any(CadastroCobrancaDTO.class)))
                .thenReturn(response);

        ResponseEntity<RespostaCobrancaDTO> result = cobrancaController.criarCobranca(dto, "12345678900");

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
        verify(cobrancaService).salvar(eq("12345678900"), eq(dto));
    }

    @Test
    void deveListarEnviadasComSucesso() {
        List<RespostaCobrancaDTO> lista = List.of(criarCobrancaResponseDTO());
        when(cobrancaService.listarEnviadas(anyString(), any())).thenReturn(lista);

        ResponseEntity<List<RespostaCobrancaDTO>> result =
                cobrancaController.listarEnviadas(StatusCobranca.PENDENTE, "12345678900");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(lista, result.getBody());
        verify(cobrancaService).listarEnviadas(eq("12345678900"), eq(StatusCobranca.PENDENTE));
    }

    @Test
    void deveListarRecebidasComSucesso() {
        List<RespostaCobrancaDTO> lista = List.of(criarCobrancaResponseDTO());
        when(cobrancaService.listarRecebidas(anyString(), any())).thenReturn(lista);

        ResponseEntity<List<RespostaCobrancaDTO>> result =
                cobrancaController.listarRecebidas("98765432100", StatusCobranca.PAGA);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(lista, result.getBody());
        verify(cobrancaService).listarRecebidas(eq("98765432100"), eq(StatusCobranca.PAGA));
    }

    @Test
    void deveCancelarCobrancaComSucesso() {
        Long idCobranca = 1L;
        String cpfCancelador = "12345678900";
        RespostaCobrancaDTO resposta = criarCobrancaResponseDTO();

        // Mock do serviço
        when(cobrancaService.cancelarCobranca(idCobranca, cpfCancelador)).thenReturn(resposta);

        // Chamada do controller
        ResponseEntity<RespostaCobrancaDTO> result = cobrancaController.cancelar(idCobranca, cpfCancelador);

        // Verificações
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resposta, result.getBody());
        verify(cobrancaService).cancelarCobranca(eq(idCobranca), eq(cpfCancelador));
    }

}
