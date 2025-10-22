package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.usuario.UsuarioResponseDTO;
import com.nimble.gatewaypagamento.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UsuarioMapperTest {

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    void deveConverterUsuarioParaDto() {
        Usuario usuario = new Usuario();
        UsuarioResponseDTO dto = mapper.toDTO(usuario);
        assertNotNull(dto);
    }

    @Test
    void toDTODeveRetornarNullQuandoEntityForNull() {
        UsuarioResponseDTO dto = mapper.toDTO(null);
        assertNull(dto);
    }

}
