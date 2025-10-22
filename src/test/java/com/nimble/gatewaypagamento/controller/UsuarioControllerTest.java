package com.nimble.gatewaypagamento.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimble.gatewaypagamento.dto.usuario.CadastroUsuarioDTO;
import com.nimble.gatewaypagamento.dto.usuario.LoginDTO;
import com.nimble.gatewaypagamento.dto.usuario.UsuarioResponseDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.Funcao;
import com.nimble.gatewaypagamento.service.TokenService;
import com.nimble.gatewaypagamento.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false) // desativa segurança para o teste
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TokenService tokenService;

    @Test
    void cadastrar_deveRetornar201() throws Exception {
        CadastroUsuarioDTO dto = new CadastroUsuarioDTO("Nome", "12345678909", "teste@teste.com", "senha123");
        UsuarioResponseDTO responseDTO = new UsuarioResponseDTO(null, "Nome", "email@teste.com", "12345678900", Funcao.USUARIO);

        Mockito.when(usuarioService.cadastrar(any(CadastroUsuarioDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(responseDTO)));
    }

    @Test
    void autenticar_deveRetornar200ComToken() throws Exception {
        LoginDTO dto = new LoginDTO("12345678900", "senha123");
        Usuario usuario = new Usuario();
        usuario.setCpf("12345678900");

        Mockito.when(usuarioService.autenticar(dto.cpfOuEmail(), dto.senha())).thenReturn(usuario);
        Mockito.when(tokenService.gerarToken(usuario)).thenReturn("token123");

        mockMvc.perform(post("/usuarios/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("token123"));
    }
}
