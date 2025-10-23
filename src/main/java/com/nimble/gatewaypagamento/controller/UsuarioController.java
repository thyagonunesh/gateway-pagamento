package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.usuario.CadastroUsuarioDTO;
import com.nimble.gatewaypagamento.dto.usuario.LoginDTO;
import com.nimble.gatewaypagamento.dto.usuario.RespostaUsuarioDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.service.TokenService;
import com.nimble.gatewaypagamento.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Endpoints para cadastro e autenticação de usuários")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    @Operation(summary = "Cadastrar um novo usuário")
    @PostMapping
    public ResponseEntity<RespostaUsuarioDTO> cadastrar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        RespostaUsuarioDTO usuarioDTO = usuarioService.salvar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    @Operation(summary = "Autenticar usuário e gerar token JWT")
    @PostMapping("/login")
    public ResponseEntity<String> autenticar(@RequestBody @Valid LoginDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto.cpfOuEmail(), dto.senha());
        String token = tokenService.gerarToken(usuario);
        return ResponseEntity.ok(token);
    }

}