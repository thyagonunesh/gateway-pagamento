package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CobrancaMapper {
    Cobranca toEntity(CadastroCobrancaDTO dto);

    @Mapping(target = "cpfOriginador", source = "originador.cpf")
    @Mapping(target = "cpfDestinatario", source = "destinatario.cpf")
    RespostaCobrancaDTO toDTO(Cobranca entity);
}