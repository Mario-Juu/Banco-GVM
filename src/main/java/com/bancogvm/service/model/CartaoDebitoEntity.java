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
@DiscriminatorValue("DEBITO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CartaoDebitoEntity extends CartaoEntity {

    private BigDecimal limiteSaque;
    private BigDecimal limiteTransferencia;

    public boolean realizarTransacaoDebito(BigDecimal valor) {
        return valor.compareTo(limiteSaque) <= 0;
    }

    @Override
    public String getTipoCartao() {
        return "DEBITO";
    }
}
