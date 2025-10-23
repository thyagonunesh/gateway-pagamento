package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.usuario.RespostaUsuarioDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    RespostaUsuarioDTO toDTO(Usuario usuario);
}