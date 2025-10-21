package com.nimble.gatewaypagamento.controller;

import com.nimble.gatewaypagamento.dto.CadastroUsuarioDTO;
import com.nimble.gatewaypagamento.dto.LoginDTO;
import com.nimble.gatewaypagamento.dto.UsuarioResponseDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.service.TokenService;
import com.nimble.gatewaypagamento.service.UsuarioService;
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
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid CadastroUsuarioDTO dto) {
        UsuarioResponseDTO usuarioDTO = usuarioService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticar(@RequestBody @Valid LoginDTO dto) {
        Usuario usuario = usuarioService.autenticar(dto.cpfOuEmail(),  dto.senha());
        String token = tokenService.gerarToken(usuario);

        return ResponseEntity.ok(token);
    }

}
