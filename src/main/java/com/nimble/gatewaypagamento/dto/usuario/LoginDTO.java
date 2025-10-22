package com.nimble.gatewaypagamento.dto.usuario;

import jakarta.validation.constraints.NotBlank;

public record LoginDTO(

        @NotBlank(message = "Informe o CPF ou e-mail")
        String cpfOuEmail,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}