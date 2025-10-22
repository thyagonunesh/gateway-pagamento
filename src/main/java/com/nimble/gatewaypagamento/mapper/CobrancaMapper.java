package com.nimble.gatewaypagamento.mapper;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CobrancaMapper {
    Cobranca toEntity(CadastroCobrancaDTO dto);
    CobrancaResponseDTO toDTO(Cobranca entity);
}