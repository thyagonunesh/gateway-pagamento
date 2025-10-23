package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.pagamento.CadastroPagamentoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.RespostaPagamentoDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Pagamento;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.entity.enums.StatusPagamento;
import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import com.nimble.gatewaypagamento.exception.pagamento.SaldoInsuficienteException;
import com.nimble.gatewaypagamento.repository.PagamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PagamentoServiceTest {

    private PagamentoService pagamentoService;
    private CobrancaService cobrancaService;
    private UsuarioService usuarioService;
    private PagamentoRepository pagamentoRepository;
    private AutorizacaoService autorizacaoService;

    private Usuario originador;
    private Usuario destinatario;
    private Cobranca cobranca;

    @BeforeEach
    void setUp() {
        cobrancaService = mock(CobrancaService.class);
        usuarioService = mock(UsuarioService.class);
        pagamentoRepository = mock(PagamentoRepository.class);
        autorizacaoService = mock(AutorizacaoService.class);

        pagamentoService = new PagamentoService(cobrancaService, usuarioService, pagamentoRepository, autorizacaoService);

        originador = new Usuario();
        originador.setCpf("11111111111");
        originador.setNome("Originador");
        originador.setSaldo(BigDecimal.valueOf(100));

        destinatario = new Usuario();
        destinatario.setCpf("22222222222");
        destinatario.setNome("Destinatario");
        destinatario.setSaldo(BigDecimal.valueOf(200));

        cobranca = Cobranca.builder()
                .id(1L)
                .originador(originador)
                .destinatario(destinatario)
                .valor(BigDecimal.valueOf(150))
                .status(StatusCobranca.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();
    }

    // ---------------- Pagar por saldo ----------------

    @Test
    void devePagarCobrancaPorSaldoComSucesso() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.SALDO, null, null, null);
        RespostaPagamentoDTO resposta = pagamentoService.pagarCobranca(dto, destinatario.getCpf());

        assertEquals(StatusPagamento.CONCLUIDO.name(), resposta.status());
        assertEquals(cobranca.getId(), resposta.idCobranca());
        assertEquals(BigDecimal.valueOf(50), destinatario.getSaldo());
        assertEquals(BigDecimal.valueOf(250), originador.getSaldo());

        verify(pagamentoRepository).save(any(Pagamento.class));
        verify(cobrancaService).cadastrar(cobranca);
    }

    @Test
    void deveLancarExcecaoSeSaldoInsuficiente() {
        cobranca.setValor(BigDecimal.valueOf(300));
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.SALDO, null, null, null);

        assertThrows(SaldoInsuficienteException.class,
                () -> pagamentoService.pagarCobranca(dto, destinatario.getCpf()));
    }

    // ---------------- Pagar por cartão ----------------

    @Test
    void devePagarCobrancaPorCartaoComAutorizacao() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);
        when(autorizacaoService.autorizarPagamentoCartao(anyString(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(true);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", "12/25", "123");
        RespostaPagamentoDTO resposta = pagamentoService.pagarCobranca(dto, destinatario.getCpf());

        assertEquals(StatusPagamento.CONCLUIDO.name(), resposta.status());
        verify(pagamentoRepository).save(any(Pagamento.class));
        verify(cobrancaService).cadastrar(cobranca);
    }

    // ---------------- Validações do cartão (branches do OR) ----------------

    @Test
    void deveLancarExcecaoSeNumeroCartaoNuloOuVazio() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        // null
        CadastroPagamentoDTO dtoNulo = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, null, "12/25", "123");
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoNulo, destinatario.getCpf()));

        // vazio
        CadastroPagamentoDTO dtoVazio = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "", "12/25", "123");
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoVazio, destinatario.getCpf()));
    }

    @Test
    void deveLancarExcecaoSeValidadeCartaoNulaOuVazia() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        // null
        CadastroPagamentoDTO dtoNulo = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", null, "123");
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoNulo, destinatario.getCpf()));

        // vazio
        CadastroPagamentoDTO dtoVazio = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", "", "123");
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoVazio, destinatario.getCpf()));
    }

    @Test
    void deveLancarExcecaoSeCvvCartaoNuloOuVazio() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        // null
        CadastroPagamentoDTO dtoNulo = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", "12/25", null);
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoNulo, destinatario.getCpf()));

        // vazio
        CadastroPagamentoDTO dtoVazio = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", "12/25", "");
        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dtoVazio, destinatario.getCpf()));
    }

    @Test
    void deveLancarExcecaoSePagamentoCartaoNaoAutorizado() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);
        when(autorizacaoService.autorizarPagamentoCartao(anyString(), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(false);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.CARTAO, "1234", "12/25", "123");

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dto, destinatario.getCpf()));
    }

    // ---------------- Exceções gerais ----------------

    @Test
    void deveLancarExcecaoSeCobrancaJaPaga() {
        cobranca.setStatus(StatusCobranca.PAGA);
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.SALDO, null, null, null);

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dto, destinatario.getCpf()));
    }

    @Test
    void deveLancarExcecaoSeUsuarioNaoForDestinatario() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        CadastroPagamentoDTO dto = new CadastroPagamentoDTO(1L, TipoPagamento.SALDO, null, null, null);

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dto, "00000000000"));
    }

    @Test
    void deveLancarExcecaoSeTipoPagamentoInvalido() {
        when(cobrancaService.buscarCobrancaPorId(1L)).thenReturn(cobranca);

        CadastroPagamentoDTO dto = mock(CadastroPagamentoDTO.class);
        when(dto.idCobranca()).thenReturn(1L);
        when(dto.tipoPagamento()).thenReturn(null);

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.pagarCobranca(dto, destinatario.getCpf()));
    }

    // ---------------- Depositar saldo ----------------

    @Test
    void deveDepositarSaldoComSucesso() {
        when(usuarioService.buscarPorCpf(destinatario.getCpf())).thenReturn(destinatario);
        when(autorizacaoService.autorizarDeposito(BigDecimal.valueOf(200))).thenReturn(true);

        pagamentoService.depositarSaldo(BigDecimal.valueOf(200), destinatario.getCpf());

        assertEquals(BigDecimal.valueOf(400), destinatario.getSaldo());
        verify(usuarioService).salvar(destinatario);
    }

    @Test
    void deveLancarExcecaoSeDepositoNaoAutorizado() {
        BigDecimal valor = BigDecimal.valueOf(100);
        when(usuarioService.buscarPorCpf(destinatario.getCpf())).thenReturn(destinatario);
        when(autorizacaoService.autorizarDeposito(valor)).thenReturn(false);

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> pagamentoService.depositarSaldo(valor, destinatario.getCpf()));
    }
}