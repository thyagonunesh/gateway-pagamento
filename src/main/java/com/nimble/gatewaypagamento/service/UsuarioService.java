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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    public UsuarioResponseDTO cadastrar(CadastroUsuarioDTO dto) {
        usuarioRepository.findByCpf(dto.cpf())
                .ifPresent(u -> { throw new UsuarioJaCadastradoException("CPF já cadastrado"); });

        usuarioRepository.findByEmail(dto.email())
                .ifPresent(u -> { throw new UsuarioJaCadastradoException("E-mail já cadastrado"); });

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .funcao(Funcao.USUARIO)
                .build();

        Usuario salvo = usuarioRepository.save(usuario);

        return usuarioMapper.toDTO(salvo);
    }

    public Usuario autenticar(String cpfOuEmail, String senha) {
        Usuario usuario = usuarioRepository.findByCpf(cpfOuEmail)
                .or(() -> usuarioRepository.findByEmail(cpfOuEmail))
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenhaHash())) {
            throw new SenhaIncorretaException("Senha incorreta");
        }

        return usuario;
    }
}
