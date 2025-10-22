package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.CobrancaResponseDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.exception.CpfOriginadorDestinatarioIguaisException;
import com.nimble.gatewaypagamento.mapper.CobrancaMapper;
import com.nimble.gatewaypagamento.repository.CobrancaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioService usuarioService;
    private final CobrancaMapper cobrancaMapper;

    public CobrancaResponseDTO criarCobranca(String cpfOriginador, CadastroCobrancaDTO dto) {
        validarCpfDiferente(cpfOriginador, dto.cpfDestinatario());

        Usuario destinatario = usuarioService.buscarPorCpf(dto.cpfDestinatario());

        Cobranca cobranca = cobrancaMapper.toEntity(dto);
        cobranca.setCpfOriginador(cpfOriginador);
        cobranca.setCpfDestinatario(destinatario.getCpf());
        cobranca.setStatus(StatusCobranca.PENDENTE);
        cobranca.setDataCriacao(LocalDateTime.now());

        Cobranca cobrancaSalva = cobrancaRepository.save(cobranca);
        return cobrancaMapper.toDTO(cobrancaSalva);
    }

    private void validarCpfDiferente(String cpfOriginador, String cpfDestinatario) {
        if (cpfOriginador.equals(cpfDestinatario)) {
            throw new CpfOriginadorDestinatarioIguaisException(
                    "Originador e destinatário não podem ter o mesmo CPF.");
        }
    }

    public List<CobrancaResponseDTO> listarEnviadas(String cpfOriginador, StatusCobranca status) {
        return cobrancaRepository.findByCpfOriginadorAndStatus(cpfOriginador, status)
                .stream()
                .map(cobrancaMapper::toDTO)
                .toList();
    }

    public List<CobrancaResponseDTO> listarRecebidas(String cpfDestinatario, StatusCobranca status) {
        return cobrancaRepository.findByCpfDestinatarioAndStatus(cpfDestinatario, status)
                .stream()
                .map(cobrancaMapper::toDTO)
                .toList();
    }
}

