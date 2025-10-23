package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.nimble.gatewaypagamento.mocks.CobrancaMocks.criarCadastroCobrancaDTO;
import static org.junit.jupiter.api.Assertions.*;

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
        RespostaCobrancaDTO dto = mapper.toDTO(entity);
        assertNotNull(dto);
    }

    @Test
    void toEntityDeveRetornarNullQuandoDtoForNull() {
        Cobranca cobranca = mapper.toEntity(null);
        assertNull(cobranca);
    }

    @Test
    void toDTODeveRetornarNullQuandoEntityForNull() {
        RespostaCobrancaDTO dto = mapper.toDTO(null);
        assertNull(dto);
    }

    @Test
    void deveCobrirCamposAninhadosNoToDTO() {
        Usuario originador = new Usuario();
        originador.setCpf("11111111111");

        Usuario destinatario = new Usuario();
        destinatario.setCpf("22222222222");

        Cobranca entity = Cobranca.builder()
                .originador(originador)
                .destinatario(destinatario)
                .valor(BigDecimal.TEN)
                .descricao("teste")
                .status(StatusCobranca.PENDENTE)
                .dataCriacao(LocalDateTime.now())
                .build();

        RespostaCobrancaDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertEquals("11111111111", dto.cpfOriginador());
        assertEquals("22222222222", dto.cpfDestinatario());
        assertEquals("teste", dto.descricao());
    }


}
