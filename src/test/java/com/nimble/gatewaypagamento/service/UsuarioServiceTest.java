package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.usuario.CadastroUsuarioDTO;
import com.nimble.gatewaypagamento.dto.usuario.RespostaUsuarioDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.Funcao;
import com.nimble.gatewaypagamento.exception.usuario.SenhaIncorretaException;
import com.nimble.gatewaypagamento.exception.usuario.UsuarioJaCadastradoException;
import com.nimble.gatewaypagamento.exception.usuario.UsuarioNaoEncontradoException;
import com.nimble.gatewaypagamento.mapper.UsuarioMapper;
import com.nimble.gatewaypagamento.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveSalvarUsuarioComSucesso() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Jo?o", "12345678900", "joao@email.com", "senha123");
        Usuario usuario = Usuario.builder()
                .nome("Jo?o")
                .cpf("12345678900")
                .email("joao@email.com")
                .senhaHash("encodedSenha")
                .funcao(Funcao.USUARIO)
                .build();

        when(usuarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedSenha");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(new RespostaUsuarioDTO(null, "Jo?o", "12345678900", "joao@email.com", Funcao.USUARIO));

        RespostaUsuarioDTO response = usuarioService.salvar(dto);

        assertEquals("Jo?o", response.nome());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoSeCpfJaCadastrado() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Jo?o", "123", "email@test.com", "senha");
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.of(new Usuario()));

        assertThrows(UsuarioJaCadastradoException.class, () -> usuarioService.salvar(dto));
    }

    @Test
    void deveLancarExcecaoSeEmailJaCadastrado() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Jo?o", "123", "email@test.com", "senha");
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("email@test.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(UsuarioJaCadastradoException.class, () -> usuarioService.salvar(dto));
    }

    @Test
    void deveAutenticarUsuarioComSucesso() {
        Usuario usuario = Usuario.builder().cpf("123").email("a@a.com").senhaHash("encoded").build();

        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha", "encoded")).thenReturn(true);

        Usuario result = usuarioService.autenticar("123", "senha");

        assertEquals(usuario, result);
    }

    @Test
    void deveLancarExcecaoSeUsuarioNaoEncontrado() {
        when(usuarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.autenticar("123", "senha"));
    }

    @Test
    void deveLancarExcecaoSeSenhaIncorreta() {
        Usuario usuario = Usuario.builder().cpf("123").senhaHash("encoded").build();
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha", "encoded")).thenReturn(false);

        assertThrows(SenhaIncorretaException.class, () -> usuarioService.autenticar("123", "senha"));
    }

    @Test
    void deveBuscarUsuarioPorCpfComSucesso() {
        Usuario usuario = Usuario.builder().cpf("123").nome("João").build();
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.of(usuario));

        Usuario result = usuarioService.buscarPorCpf("123");

        assertEquals(usuario, result);
        verify(usuarioRepository).findByCpf("123");
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontradoPorCpf() {
        when(usuarioRepository.findByCpf("999")).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class, () -> usuarioService.buscarPorCpf("999"));
        verify(usuarioRepository).findByCpf("999");
    }

    @Test
    void deveSalvarUsuarioDiretamente() {
        Usuario usuario = Usuario.builder()
                .nome("João")
                .cpf("12345678900")
                .email("joao@email.com")
                .senhaHash("senha")
                .funcao(Funcao.USUARIO)
                .build();

        usuarioService.salvar(usuario);

        verify(usuarioRepository).save(usuario);
    }

}