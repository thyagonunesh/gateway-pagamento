package com.nimble.gatewaypagamento.entity;

import com.nimble.gatewaypagamento.entity.enums.StatusCobranca;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cobrancas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cobranca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 11)
    private String cpfOriginador;

    @Column(nullable = false, length = 11)
    private String cpfDestinatario;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column
    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusCobranca status = StatusCobranca.PENDENTE;

    private LocalDateTime dataCriacao;

}