package com.bancogvm.service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

import java.time.Instant;

@Entity
@Table(name = "titular_conta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TitularContaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoTitularidade;
    private Instant dataAssociacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    @JsonBackReference("cliente-titular")
    private ClienteEntity cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    @JsonBackReference("conta-titular")
    private ContaEntity conta;
}
