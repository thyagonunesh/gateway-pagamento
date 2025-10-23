package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.exception.cobranca.CobrancaNaoEncontradaException;
import com.nimble.gatewaypagamento.exception.cobranca.CpfOriginadorDestinatarioIguaisException;
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

    public RespostaCobrancaDTO cadastrar(String cpfOriginador, CadastroCobrancaDTO dto) {
        Usuario originador = usuarioService.buscarPorCpf(cpfOriginador);
        Usuario destinatario = usuarioService.buscarPorCpf(dto.cpfDestinatario());

        validarCpfDiferente(originador.getCpf(), destinatario.getCpf());

        Cobranca cobranca = cobrancaMapper.toEntity(dto);
        cobranca.setOriginador(originador);
        cobranca.setDestinatario(destinatario);
        cobranca.setStatus(StatusCobranca.PENDENTE);
        cobranca.setDataCriacao(LocalDateTime.now());

        Cobranca cobrancaSalva = cobrancaRepository.save(cobranca);
        return cobrancaMapper.toDTO(cobrancaSalva);
    }

    // Atualiza/Salva cobrança
    public void cadastrar(Cobranca cobranca) {
        cobrancaRepository.save(cobranca);
    }

    private void validarCpfDiferente(String cpfOriginador, String cpfDestinatario) {
        if (cpfOriginador.equals(cpfDestinatario)) {
            throw new CpfOriginadorDestinatarioIguaisException(
                    "Originador e destinatário não podem ter o mesmo CPF.");
        }
    }

    // Listar cobranças enviadas pelo originador
    public List<RespostaCobrancaDTO> listarEnviadas(String cpfOriginador, StatusCobranca status) {
        Usuario originador = usuarioService.buscarPorCpf(cpfOriginador);
        return cobrancaRepository.findByOriginadorAndStatus(originador, status)
                .stream()
                .map(cobrancaMapper::toDTO)
                .toList();
    }

    // Listar cobranças recebidas pelo destinatário
    public List<RespostaCobrancaDTO> listarRecebidas(String cpfDestinatario, StatusCobranca status) {
        Usuario destinatario = usuarioService.buscarPorCpf(cpfDestinatario);
        return cobrancaRepository.findByDestinatarioAndStatus(destinatario, status)
                .stream()
                .map(cobrancaMapper::toDTO)
                .toList();
    }

    // Buscar cobrança pelo ID
    public Cobranca buscarCobrancaPorId(Long id) {
        return cobrancaRepository
                .findById(id)
                .orElseThrow(() -> new CobrancaNaoEncontradaException("Cobrança não encontrada"));
    }
}


