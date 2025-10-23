package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.pagamento.CadastroDepositoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.CadastroPagamentoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.RespostaPagamentoDTO;
import com.nimble.gatewaypagamento.entity.enums.StatusPagamento;
import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import com.nimble.gatewaypagamento.service.PagamentoService;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    @BeforeEach
    void setup() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("12345678900", null)
        );
    }

    @Test
    void devePagarCobrancaComSucesso() {
        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(
                1L,
                TipoPagamento.SALDO,
                null,
                null,
                null
        );

        RespostaPagamentoDTO resposta = new RespostaPagamentoDTO(
                1L,
                1L,
                StatusPagamento.CONCLUIDO.name(),
                new BigDecimal("100.00"),
                "Originador",
                "Destinatario"
        );

        when(pagamentoService.pagarCobranca(any(CadastroPagamentoDTO.class), anyString()))
                .thenReturn(resposta);

        ResponseEntity<RespostaPagamentoDTO> result =
                pagamentoController.pagarCobranca(dto, "12345678900");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(resposta, result.getBody());
        verify(pagamentoService).pagarCobranca(eq(dto), eq("12345678900"));
    }

    @Test
    void deveDepositarSaldoComSucesso() {
        CadastroDepositoDTO dto = new CadastroDepositoDTO(new BigDecimal("150.00"));

        doNothing().when(pagamentoService).depositarSaldo(any(BigDecimal.class), anyString());

        ResponseEntity<String> result =
                pagamentoController.depositarSaldo(dto, "12345678900");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Depósito realizado com sucesso", result.getBody());
        verify(pagamentoService).depositarSaldo(eq(dto.valor()), eq("12345678900"));
    }
}
