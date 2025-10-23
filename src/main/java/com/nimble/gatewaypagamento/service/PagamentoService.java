package com.nimble.gatewaypagamento.service;

import com.nimble.gatewaypagamento.dto.pagamento.CadastroPagamentoDTO;
import com.nimble.gatewaypagamento.dto.pagamento.RespostaPagamentoDTO;
import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Pagamento;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import com.nimble.gatewaypagamento.entity.enums.StatusPagamento;
import com.nimble.gatewaypagamento.entity.enums.TipoPagamento;
import com.nimble.gatewaypagamento.exception.pagamento.PagamentoDeCobrancaNaoAutorizadaException;
import com.nimble.gatewaypagamento.exception.pagamento.PagamentoNaoEncontradoException;
import com.nimble.gatewaypagamento.exception.pagamento.SaldoInsuficienteException;
import com.nimble.gatewaypagamento.repository.PagamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PagamentoService {

    private final UsuarioService usuarioService;
    private final PagamentoRepository pagamentoRepository;
    private final AutorizacaoService autorizacaoService;

    private CobrancaService cobrancaService;

    public PagamentoService(UsuarioService usuarioService,
                            PagamentoRepository pagamentoRepository,
                            AutorizacaoService autorizacaoService) {
        this.usuarioService = usuarioService;
        this.pagamentoRepository = pagamentoRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Transactional
    public RespostaPagamentoDTO pagarCobranca(CadastroPagamentoDTO dto, String cpfPagador) {
        Cobranca cobranca = cobrancaService.buscarCobrancaPorId(dto.idCobranca());

        if (cobranca.getStatus() == StatusCobranca.PAGA) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Esta cobrança já está paga");
        }

        if (!cobranca.getDestinatario().getCpf().equals(cpfPagador)) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("O usuário não é destinatário da cobrança");
        }

        TipoPagamento tipoPagamento = dto.tipoPagamento();
        if (tipoPagamento == null) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Tipo de pagamento inválido");
        }

        switch (tipoPagamento) {
            case SALDO -> pagarCobrancaPorSaldo(cobranca);
            case CARTAO -> pagarCobrancaPorCartao(dto, cobranca);
            default -> throw new PagamentoDeCobrancaNaoAutorizadaException("Tipo de pagamento inválido");
        }

        cobranca.setStatus(StatusCobranca.PAGA);
        cobrancaService.salvar(cobranca);

        Pagamento pagamento = Pagamento.builder()
                .cobranca(cobranca)
                .valor(cobranca.getValor())
                .tipoPagamento(dto.tipoPagamento())
                .status(StatusPagamento.CONCLUIDO)
                .dataCriacao(LocalDateTime.now())
                .build();

        pagamentoRepository.save(pagamento);

        return new RespostaPagamentoDTO(
                pagamento.getId(),
                cobranca.getId(),
                pagamento.getStatus().name(),
                pagamento.getValor(),
                cobranca.getOriginador().getNome(),
                cobranca.getDestinatario().getNome()
        );
    }

    @Transactional
    public void depositarSaldo(BigDecimal valor, String cpfUsuario) {
        Usuario usuario = usuarioService.buscarPorCpf(cpfUsuario);

        boolean autorizado = autorizacaoService.autorizarDeposito(valor);
        if (!autorizado) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Depósito não autorizado pelo autorizador externo");
        }

        usuario.setSaldo(usuario.getSaldo().add(valor));
        usuarioService.salvar(usuario);
    }

    private void pagarCobrancaPorCartao(CadastroPagamentoDTO dto, Cobranca cobranca) {
        validarPagamentoCartao(dto);

        boolean autorizado = autorizacaoService.autorizarPagamentoCartao(
                dto.numeroCartao(),
                dto.validadeCartao(),
                dto.cvv(),
                cobranca.getValor()
        );

        if (!autorizado) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Pagamento não autorizado pelo autorizador externo");
        }
    }

    private void validarPagamentoCartao(CadastroPagamentoDTO dto) {
        if (dto.numeroCartao() == null || dto.numeroCartao().isBlank()) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Número do cartão é obrigatório para pagamento com cartão");
        }
        if (dto.validadeCartao() == null || dto.validadeCartao().isBlank()) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("Validade do cartão é obrigatória para pagamento com cartão");
        }
        if (dto.cvv() == null || dto.cvv().isBlank()) {
            throw new PagamentoDeCobrancaNaoAutorizadaException("CVV é obrigatório para pagamento com cartão");
        }
    }

    private static void pagarCobrancaPorSaldo(Cobranca cobranca) {
        Usuario pagador = cobranca.getDestinatario();
        Usuario recebedor = cobranca.getOriginador();

        if (pagador.getSaldo().compareTo(cobranca.getValor()) < 0) {
            throw new SaldoInsuficienteException("Saldo insuficiente");
        }

        pagador.setSaldo(pagador.getSaldo().subtract(cobranca.getValor()));
        recebedor.setSaldo(recebedor.getSaldo().add(cobranca.getValor()));
    }

    public Pagamento findByCobranca(Cobranca cobranca) {
        return pagamentoRepository
                .findByCobranca(cobranca)
                .orElseThrow(() -> new PagamentoNaoEncontradoException("Pagamento não encontrado para esta cobrança"));
    }

    public void salvar(Pagamento pagamento) {
        pagamentoRepository.save(pagamento);
    }

    @Autowired
    public void setCobrancaService(@Lazy CobrancaService cobrancaService) {
        this.cobrancaService = cobrancaService;
    }
}