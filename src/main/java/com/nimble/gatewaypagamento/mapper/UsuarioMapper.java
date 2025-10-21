package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.UsuarioResponseDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    UsuarioResponseDTO toDTO(Usuario usuario);
}