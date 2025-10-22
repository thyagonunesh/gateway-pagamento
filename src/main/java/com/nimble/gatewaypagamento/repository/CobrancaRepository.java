package com.nimble.gatewaypagamento.repository;

import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {
    List<Cobranca> findByCpfOriginadorAndStatus(String cpfOriginador, StatusCobranca status);
    List<Cobranca> findByCpfDestinatarioAndStatus(String cpfDestinatario, StatusCobranca status);
}
