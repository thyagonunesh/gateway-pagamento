package com.nimble.gatewaypagamento.repository;

import com.nimble.gatewaypagamento.entity.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}
