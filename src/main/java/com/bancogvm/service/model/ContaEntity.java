package com.bancogvm.service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conta")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_conta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public abstract class ContaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroConta;
    private String agencia;
    private BigDecimal saldo;
    private Instant dataAbertura;
    private String statusConta;
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<CartaoEntity> cartoes = new ArrayList<>();
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JsonManagedReference("conta-titular")
    private List<TitularContaEntity> titulares = new ArrayList<>();
}