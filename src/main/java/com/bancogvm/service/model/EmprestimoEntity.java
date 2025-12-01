package com.bancogvm.service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "emprestimo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EmprestimoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valorSolicitado;
    private BigDecimal valorAprovado;
    private BigDecimal taxaJurosMensal;
    private Integer numeroParcelas;
    private BigDecimal valorTotal;
    private BigDecimal valorParcela;

    private Instant dataSolicitacao;
    private Instant dataAprovacao;

    private String statusEmprestimo;   // ex: "PENDENTE", "APROVADO", "REJEITADO"
    private String motivoRejeicao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_credito_id")
    private ContaCorrenteEntity contaCredito;
}