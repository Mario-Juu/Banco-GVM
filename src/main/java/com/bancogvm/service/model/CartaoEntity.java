package com.bancogvm.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "cartao")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_cartao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class CartaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroCartao;
    private String nomeTitular;
    private Instant dataEmissao;
    private Instant dataValidade;
    private String cvvHash;
    private String statusCartao;
    @ManyToOne(optional = false)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaCorrenteEntity conta;

    public void ativar() {
        setStatusCartao("ATIVO");
    }

    public void bloquear() {
        setStatusCartao("BLOQUEADO");
    }

    public void desbloquear() {
        setStatusCartao("ATIVO");
    }

    public abstract String getTipoCartao();
}