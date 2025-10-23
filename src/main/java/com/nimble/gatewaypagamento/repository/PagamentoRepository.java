package com.nimble.gatewaypagamento.repository;

import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    Optional<Pagamento> findByCobranca(Cobranca cobranca);
}
