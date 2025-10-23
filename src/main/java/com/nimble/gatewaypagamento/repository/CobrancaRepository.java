package com.nimble.gatewaypagamento.repository;

import com.nimble.gatewaypagamento.entity.Cobranca;
import com.nimble.gatewaypagamento.entity.Usuario;
import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CobrancaRepository extends JpaRepository<Cobranca, Long> {

    List<Cobranca> findByOriginadorAndStatus(Usuario originador, StatusCobranca status);

    List<Cobranca> findByDestinatarioAndStatus(Usuario destinatario, StatusCobranca status);
}
