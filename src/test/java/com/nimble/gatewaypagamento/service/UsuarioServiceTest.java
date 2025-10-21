package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.CadastroUsuarioDTO;
import com.nimble.gatewaypagamento.dto.UsuarioResponseDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.Funcao;
import com.nimble.gatewaypagamento.exception.SenhaIncorretaException;
import com.nimble.gatewaypagamento.exception.UsuarioJaCadastradoException;
import com.nimble.gatewaypagamento.exception.UsuarioNaoEncontradoException;
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
    void deveCadastrarUsuarioComSucesso() {
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
        when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(new UsuarioResponseDTO(null, "Jo?o", "12345678900", "joao@email.com", Funcao.USUARIO));

        UsuarioResponseDTO response = usuarioService.cadastrar(dto);

        assertEquals("Jo?o", response.nome());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoSeCpfJaCadastrado() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Jo?o", "123", "email@test.com", "senha");
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.of(new Usuario()));

        assertThrows(UsuarioJaCadastradoException.class, () -> usuarioService.cadastrar(dto));
    }

    @Test
    void deveLancarExcecaoSeEmailJaCadastrado() {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Jo?o", "123", "email@test.com", "senha");
        when(usuarioRepository.findByCpf("123")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("email@test.com")).thenReturn(Optional.of(new Usuario()));

        assertThrows(UsuarioJaCadastradoException.class, () -> usuarioService.cadastrar(dto));
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
}