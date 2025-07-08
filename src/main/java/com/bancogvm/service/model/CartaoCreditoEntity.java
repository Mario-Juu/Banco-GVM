package com.bancogvm.service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CREDITO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartaoCreditoEntity extends CartaoEntity {

    private BigDecimal limiteCredito;
    private int diaFechamento;
    private int diaVencimento;

    public boolean realizarCompra(BigDecimal valor) {
        // lógica simples: permitir se houver limite
        BigDecimal disponivel = limiteCredito; // ou calcule com fatura
        return disponivel.compareTo(valor) >= 0;
    }

    public void pagarFatura(BigDecimal valor) {
        // aqui reduz valor da fatura pendente (não modelada)
    }

    public BigDecimal consultarLimiteDisponivel() {
        return limiteCredito;
    }

    @Override
    public String getTipoCartao() {
        return "CREDITO";
    }
}
