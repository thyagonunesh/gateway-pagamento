package com.nimble.gatewaypagamento.dto.usuario;

import com.nimble.gatewaypagamento.entity.enums.Funcao;

public record RespostaUsuarioDTO(
        Long id,
        String nome,
        String cpf,
        String email,
        Funcao funcao
) {
}
