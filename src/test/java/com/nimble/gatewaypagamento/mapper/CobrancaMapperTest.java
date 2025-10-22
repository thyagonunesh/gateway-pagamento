package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.nimble.gatewaypagamento.mocks.CobrancaMocks.criarCadastroCobrancaDTO;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CobrancaMapperTest {

    private final CobrancaMapper mapper = Mappers.getMapper(CobrancaMapper.class);

    @Test
    void deveConverterDtoParaEntity() {
        CadastroCobrancaDTO dto = criarCadastroCobrancaDTO();
        Cobranca entity = mapper.toEntity(dto);
        assertNotNull(entity);
    }

    @Test
    void deveConverterEntityParaDto() {
        Cobranca entity = new Cobranca();
        CobrancaResponseDTO dto = mapper.toDTO(entity);
        assertNotNull(dto);
    }

    @Test
    void toEntityDeveRetornarNullQuandoDtoForNull() {
        Cobranca cobranca = mapper.toEntity(null);
        assertNull(cobranca);
    }

    @Test
    void toDTODeveRetornarNullQuandoEntityForNull() {
        CobrancaResponseDTO dto = mapper.toDTO(null);
        assertNull(dto);
    }

}
