package com.nimble.gatewaypagamento.dto;

import com.nimble.gatewaypagamento.entity.enums.Funcao;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        Funcao funcao
) {
}
