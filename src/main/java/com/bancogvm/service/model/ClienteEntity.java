package com.bancogvm.service.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cliente")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cpf;

    private LocalDate dataNascimento;
    private String endereco;
    private String telefone;
    private String email;

    private String loginUsuario;
    private String senhaHash;

    private Instant dataCadastro;
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BeneficiarioEntity> beneficiarios = new ArrayList<>();
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("cliente-titular")
    private List<TitularContaEntity> titularidades = new ArrayList<>();
}