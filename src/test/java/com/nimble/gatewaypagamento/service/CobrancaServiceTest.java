package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Pagamento;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.entity.enums.StatusPagamento;
import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import com.nimble.gatewaypagamento.exception.cobranca.CobrancaJaPagaException;
import com.nimble.gatewaypagamento.exception.cobranca.CobrancaNaoEncontradaException;
import com.nimble.gatewaypagamento.exception.cobranca.CpfOriginadorDestinatarioIguaisException;
import com.nimble.gatewaypagamento.exception.cobranca.OriginadorInvalidoException;
import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import com.nimble.gatewaypagamento.mapper.CobrancaMapper;
import com.nimble.gatewaypagamento.repository.CobrancaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaServiceTest {

    @Mock
    private CobrancaRepository cobrancaRepository;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private CobrancaMapper cobrancaMapper;

    @Mock
    private PagamentoService pagamentoService;

    @Mock
    private AutorizacaoService autorizacaoService;

    @InjectMocks
    private CobrancaService cobrancaService;

    private CadastroCobrancaDTO dto;
    private Cobranca cobranca;
    private RespostaCobrancaDTO response;
    private Usuario originador;
    private Usuario destinatario;
    private Pagamento pagamento;

    @BeforeEach
    void setUp() {
        // DTO de teste
        dto = new CadastroCobrancaDTO("12345678900", new BigDecimal("100.00"), "teste");

        // Usuários mock
        originador = new Usuario();
        originador.setCpf("11111111111");
        originador.setNome("Originador");
        originador.setSaldo(new BigDecimal("200"));

        destinatario = new Usuario();
        destinatario.setCpf("12345678900");
        destinatario.setNome("Destinatario");
        destinatario.setSaldo(new BigDecimal("50"));

        // Entidade Cobranca
        cobranca = Cobranca.builder()
                .id(1L)
                .originador(originador)
                .destinatario(destinatario)
                .valor(new BigDecimal("100.00"))
                .descricao("teste")
                .status(StatusCobranca.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();

        // DTO de resposta
        response = new RespostaCobrancaDTO(
                1L,
                originador.getCpf(),
                destinatario.getCpf(),
                cobranca.getValor(),
                cobranca.getDescricao(),
                cobranca.getStatus(),
                cobranca.getDataCriacao()
        );

        pagamento = Pagamento.builder()
                .id(1L)
                .cobranca(cobranca)
                .valor(new BigDecimal("100.00"))
                .status(StatusPagamento.CONCLUIDO)
                .tipoPagamento(TipoPagamento.SALDO) // inicialização default
                .build();
    }

    @Test
    void deveCadastrarComSucesso() {
        when(usuarioService.buscarPorCpf(originador.getCpf())).thenReturn(originador);
        when(usuarioService.buscarPorCpf(destinatario.getCpf())).thenReturn(destinatario);
        when(cobrancaMapper.toEntity(dto)).thenReturn(cobranca);
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobranca);
        when(cobrancaMapper.toDTO(any(Cobranca.class))).thenReturn(response);

        RespostaCobrancaDTO resultado = cobrancaService.salvar(originador.getCpf(), dto);

        // Usando comparação recursiva ignorando dataCriacao
        assertThat(resultado)
                .usingRecursiveComparison()
                .ignoringFields("dataCriacao")
                .isEqualTo(response);

        verify(cobrancaRepository).save(any(Cobranca.class));
        verify(usuarioService).buscarPorCpf(originador.getCpf());
        verify(usuarioService).buscarPorCpf(destinatario.getCpf());
    }

    @Test
    void deveLancarExcecaoQuandoCpfsForemIguais() {
        CadastroCobrancaDTO dtoMesmoCpf = new CadastroCobrancaDTO("11111111111", new BigDecimal("100.00"), "teste");

        when(usuarioService.buscarPorCpf("11111111111")).thenReturn(originador);
        when(usuarioService.buscarPorCpf(dtoMesmoCpf.cpfDestinatario())).thenReturn(originador);

        assertThrows(CpfOriginadorDestinatarioIguaisException.class,
                () -> cobrancaService.salvar("11111111111", dtoMesmoCpf));

        verifyNoInteractions(cobrancaRepository);
    }

    @Test
    void deveListarCobrancasEnviadas() {
        when(usuarioService.buscarPorCpf(originador.getCpf())).thenReturn(originador);
        when(cobrancaRepository.findByOriginadorAndStatus(originador, StatusCobranca.PENDENTE))
                .thenReturn(List.of(cobranca));
        when(cobrancaMapper.toDTO(any(Cobranca.class))).thenReturn(response);

        List<RespostaCobrancaDTO> resultado = cobrancaService.listarEnviadas(originador.getCpf(), StatusCobranca.PENDENTE);

        assertEquals(1, resultado.size());
        assertThat(resultado.get(0))
                .usingRecursiveComparison()
                .ignoringFields("dataCriacao")
                .isEqualTo(response);
    }

    @Test
    void deveListarCobrancasRecebidas() {
        when(usuarioService.buscarPorCpf(destinatario.getCpf())).thenReturn(destinatario);
        when(cobrancaRepository.findByDestinatarioAndStatus(destinatario, StatusCobranca.PENDENTE))
                .thenReturn(List.of(cobranca));
        when(cobrancaMapper.toDTO(any(Cobranca.class))).thenReturn(response);

        List<RespostaCobrancaDTO> resultado = cobrancaService.listarRecebidas(destinatario.getCpf(), StatusCobranca.PENDENTE);

        assertEquals(1, resultado.size());
        assertThat(resultado.get(0))
                .usingRecursiveComparison()
                .ignoringFields("dataCriacao")
                .isEqualTo(response);
    }

    @Test
    void deveCadastrarCobrancaDiretamente() {
        cobrancaService.salvar(cobranca);

        verify(cobrancaRepository).save(cobranca);
    }

    @Test
    void deveRetornarCobrancaQuandoExistir() {
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));

        Cobranca resultado = cobrancaService.buscarCobrancaPorId(1L);

        assertEquals(cobranca, resultado);
        verify(cobrancaRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoCobrancaNaoExistir() {
        when(cobrancaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(CobrancaNaoEncontradaException.class,
                () -> cobrancaService.buscarCobrancaPorId(2L));

        verify(cobrancaRepository).findById(2L);
    }

    @Test
    void deveCancelarCobrancaComSucesso() {
        cobranca.setStatus(StatusCobranca.PENDENTE);
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));

        // Mapeamento do DTO refletindo alterações reais da cobrança
        when(cobrancaMapper.toDTO(any(Cobranca.class)))
                .thenAnswer(invocation -> {
                    Cobranca c = invocation.getArgument(0);
                    return new RespostaCobrancaDTO(
                            c.getId(),
                            c.getOriginador().getCpf(),
                            c.getDestinatario().getCpf(),
                            c.getValor(),
                            c.getDescricao(),
                            c.getStatus(),
                            c.getDataCriacao()
                    );
                });

        RespostaCobrancaDTO resultado = cobrancaService.cancelarCobranca(1L, originador.getCpf());

        assertThat(resultado.status()).isEqualTo(StatusCobranca.CANCELADA);
        verify(cobrancaRepository).save(cobranca);
    }

    @Test
    void deveLancarExcecaoQuandoOriginadorInvalido() {
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));

        assertThrows(OriginadorInvalidoException.class,
                () -> cobrancaService.cancelarCobranca(1L, "99999999999"));

        verify(cobrancaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoCobrancaJaCancelada() {
        cobranca.setStatus(StatusCobranca.CANCELADA);
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));

        assertThrows(CobrancaJaPagaException.class,
                () -> cobrancaService.cancelarCobranca(1L, originador.getCpf()));

        verify(cobrancaRepository, never()).save(any());
    }

    @Test
    void deveEstornarSaldoQuandoCobrancaPaga() {
        Cobranca cobranca = new Cobranca();
        cobranca.setStatus(StatusCobranca.PAGA);
        cobranca.setOriginador(originador);
        cobranca.setDestinatario(destinatario);

        Pagamento pagamento = new Pagamento();
        pagamento.setTipoPagamento(TipoPagamento.SALDO);
        pagamento.setValor(new BigDecimal("100"));
        pagamento.setCobranca(cobranca);

        // mock do pagamentoService
        when(pagamentoService.findByCobranca(cobranca)).thenReturn(pagamento);
        when(cobrancaRepository.findById(cobranca.getId())).thenReturn(Optional.of(cobranca));

        cobrancaService.cancelarCobranca(cobranca.getId(), cobranca.getOriginador().getCpf());

        assertEquals(StatusCobranca.CANCELADA, cobranca.getStatus());
    }

    @Test
    void deveCancelarPagamentoCartaoQuandoAutorizado() {
        // Define a cobrança como PAGA
        cobranca.setStatus(StatusCobranca.PAGA);

        // Inicializa o pagamento como CARTAO
        pagamento = Pagamento.builder()
                .id(1L)
                .cobranca(cobranca)
                .valor(cobranca.getValor())
                .status(StatusPagamento.CONCLUIDO)
                .tipoPagamento(TipoPagamento.CARTAO)
                .build();

        // Mock do repository e services
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));
        when(pagamentoService.findByCobranca(cobranca)).thenReturn(pagamento);
        when(autorizacaoService.autorizarDeposito(pagamento.getValor())).thenReturn(true);

        // Mapeamento real para refletir alterações de status
        when(cobrancaMapper.toDTO(any(Cobranca.class)))
                .thenAnswer(invocation -> {
                    Cobranca c = invocation.getArgument(0);
                    return new RespostaCobrancaDTO(
                            c.getId(),
                            c.getOriginador().getCpf(),
                            c.getDestinatario().getCpf(),
                            c.getValor(),
                            c.getDescricao(),
                            c.getStatus(),
                            c.getDataCriacao()
                    );
                });

        // Executa o cancelamento
        RespostaCobrancaDTO resultado = cobrancaService.cancelarCobranca(1L, originador.getCpf());

        // Verificações
        assertThat(resultado.status()).isEqualTo(StatusCobranca.CANCELADA);
        assertThat(pagamento.getStatus()).isEqualTo(StatusPagamento.CANCELADO);
        verify(pagamentoService).salvar(pagamento);
        verify(cobrancaRepository).save(cobranca);
    }

    @Test
    void deveLancarExcecaoQuandoAutorizacaoDepositoNegada() {
        cobranca.setStatus(StatusCobranca.PAGA);
        pagamento.setTipoPagamento(TipoPagamento.CARTAO);
        when(cobrancaRepository.findById(1L)).thenReturn(Optional.of(cobranca));
        when(pagamentoService.findByCobranca(cobranca)).thenReturn(pagamento);
        when(autorizacaoService.autorizarDeposito(pagamento.getValor())).thenReturn(false);

        assertThrows(PagamentoDeCobrancaNaoAutorizadaException.class,
                () -> cobrancaService.cancelarCobranca(1L, originador.getCpf()));

        verify(pagamentoService, never()).salvar(any());
        verify(cobrancaRepository, never()).save(any());
    }

}
