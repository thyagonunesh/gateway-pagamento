package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.exception.CpfOriginadorDestinatarioIguaisException;
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

    @InjectMocks
    private CobrancaService cobrancaService;

    private CadastroCobrancaDTO dto;
    private Cobranca cobranca;
    private CobrancaResponseDTO response;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        dto = new CadastroCobrancaDTO("12345678900", new BigDecimal("100.00"), "teste");
        cobranca = Cobranca.builder()
                .id(1L)
                .cpfOriginador("11111111111")
                .cpfDestinatario("12345678900")
                .valor(new BigDecimal("100.00"))
                .descricao("teste")
                .status(StatusCobranca.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();

        response = new CobrancaResponseDTO(1L, "11111111111", "12345678900",
                new BigDecimal("100.00"), "teste", StatusCobranca.PENDENTE, LocalDateTime.now());

        usuario = new Usuario();
        usuario.setCpf("12345678900");
    }

    @Test
    void deveCriarCobrancaComSucesso() {
        when(usuarioService.buscarPorCpf(dto.cpfDestinatario())).thenReturn(usuario);
        when(cobrancaMapper.toEntity(dto)).thenReturn(cobranca);
        when(cobrancaRepository.save(any(Cobranca.class))).thenReturn(cobranca);
        when(cobrancaMapper.toDTO(cobranca)).thenReturn(response);

        CobrancaResponseDTO resultado = cobrancaService.criarCobranca("11111111111", dto);

        assertEquals(response, resultado);
        verify(cobrancaRepository).save(any(Cobranca.class));
        verify(usuarioService).buscarPorCpf(dto.cpfDestinatario());
    }

    @Test
    void deveLancarExcecaoQuandoCpfsForemIguais() {
        CadastroCobrancaDTO dtoMesmoCpf = new CadastroCobrancaDTO("11111111111", new BigDecimal("100.00"), "teste");
        assertThrows(CpfOriginadorDestinatarioIguaisException.class,
                () -> cobrancaService.criarCobranca("11111111111", dtoMesmoCpf));
        verifyNoInteractions(cobrancaRepository);
    }

    @Test
    void deveListarCobrancasEnviadas() {
        when(cobrancaRepository.findByCpfOriginadorAndStatus("11111111111", StatusCobranca.PENDENTE))
                .thenReturn(List.of(cobranca));
        when(cobrancaMapper.toDTO(cobranca)).thenReturn(response);

        List<CobrancaResponseDTO> resultado = cobrancaService.listarEnviadas("11111111111", StatusCobranca.PENDENTE);

        assertEquals(1, resultado.size());
        assertEquals(response, resultado.get(0));
    }

    @Test
    void deveListarCobrancasRecebidas() {
        when(cobrancaRepository.findByCpfDestinatarioAndStatus("12345678900", StatusCobranca.PENDENTE))
                .thenReturn(List.of(cobranca));
        when(cobrancaMapper.toDTO(cobranca)).thenReturn(response);

        List<CobrancaResponseDTO> resultado = cobrancaService.listarRecebidas("12345678900", StatusCobranca.PENDENTE);

        assertEquals(1, resultado.size());
        assertEquals(response, resultado.get(0));
    }
}
