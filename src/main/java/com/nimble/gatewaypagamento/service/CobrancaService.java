package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.cobranca.CadastroCobrancaDTO;
import com.nimble.gatewaypagamento.dto.cobranca.RespostaCobrancaDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Pagamento;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.entity.enums.StatusPagamento;
import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import com.nimble.gatewaypagamento.exception.cobranca.CobrancaJaPagaException;
import com.nimble.gatewaypagamento.exception.cobranca.CobrancaNaoEncontradaException;
import com.nimble.gatewaypagamento.exception.cobranca.CpfOriginadorDestinatarioIguaisException;
import com.nimble.gatewaypagamento.exception.cobranca.OriginadorInvalidoException;
import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import com.nimble.gatewaypagamento.exception.pagamento.SaldoInsuficienteException;
import com.nimble.gatewaypagamento.mapper.CobrancaMapper;
import com.nimble.gatewaypagamento.repository.CobrancaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CobrancaService {

    private final CobrancaRepository cobrancaRepository;
    private final UsuarioService usuarioService;
    private final CobrancaMapper cobrancaMapper;
    private final PagamentoService pagamentoService;
    private final AutorizacaoService autorizacaoService;

    public RespostaCobrancaDTO salvar(String cpfOriginador, CadastroCobrancaDTO dto) {
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
    public void salvar(Cobranca cobranca) {
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

    @Transactional
    public RespostaCobrancaDTO cancelarCobranca(Long id, String cpfCancelador) {
        Cobranca cobranca = buscarCobrancaPorId(id);

        if (!cpfCancelador.equals(cobranca.getOriginador().getCpf())) {
            throw new OriginadorInvalidoException("Apenas o originador pode cancelar a Cobrança");
        }

        if (cobranca.getStatus() == StatusCobranca.CANCELADA) {
            throw new CobrancaJaPagaException("Cobrança já está cancelada");
        }

        if (cobranca.getStatus() == StatusCobranca.PENDENTE) {
            cobranca.setStatus(StatusCobranca.CANCELADA);
        } else if (cobranca.getStatus() == StatusCobranca.PAGA) {
            cancelarCobrancaPaga(cobranca);
        }

        salvar(cobranca);

        return cobrancaMapper.toDTO(cobranca);
    }

    private void cancelarCobrancaPaga(Cobranca cobranca) {
        Pagamento pagamento = pagamentoService.findByCobranca(cobranca);

        if (pagamento.getTipoPagamento() == TipoPagamento.SALDO) {
            estornarSaldo(pagamento);
            cobranca.setStatus(StatusCobranca.CANCELADA);
        } else if (pagamento.getTipoPagamento() == TipoPagamento.CARTAO) {
            boolean autorizado = autorizacaoService.autorizarDeposito(pagamento.getValor());
            if (!autorizado) {
                throw new PagamentoDeCobrancaNaoAutorizadaException("Autorizador negou o cancelamento do pagamento");
            }
            cobranca.setStatus(StatusCobranca.CANCELADA);
            pagamento.setStatus(StatusPagamento.CANCELADO);

            pagamentoService.salvar(pagamento);
        }
    }

    private void estornarSaldo(Pagamento pagamento) {
        Usuario originador = pagamento.getCobranca().getOriginador();
        Usuario destinatario = pagamento.getCobranca().getDestinatario();
        BigDecimal valor = pagamento.getValor();

        if (originador.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException("Originador não possui saldo suficiente para estornar o pagamento.");
        }

        // Originador devolve o valor
        originador.setSaldo(originador.getSaldo().subtract(valor));

        // Destinatário recebe o valor de volta
        destinatario.setSaldo(destinatario.getSaldo().add(valor));

        usuarioService.salvar(originador);
        usuarioService.salvar(destinatario);
    }

}