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
@Table(name = "transacao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransacaoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor;
    private Instant dataHora;
    private String tipoTransacao;      // ex: "DEPOSITO", "SAQUE", "TRANSFERENCIA"
    private String statusTransacao;    // ex: "PENDENTE", "CONCLUIDA", "FALHOU"
    private String descricao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_origem_id")
    private ContaCorrenteEntity contaOrigem;

    @ManyToOne
    @JoinColumn(name = "conta_destino_id")
    private ContaCorrenteEntity contaDestino;
}