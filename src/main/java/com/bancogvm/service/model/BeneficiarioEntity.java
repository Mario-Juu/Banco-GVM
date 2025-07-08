package com.bancogvm.service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "beneficiario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BeneficiarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeFavorecido;
    private String cpfCnpjFavorecido;
    private String bancoFavorecido;
    private String agenciaFavorecida;
    private String contaFavorecida;
    private String tipoContaFavorecida;
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private ClienteEntity cliente;
}