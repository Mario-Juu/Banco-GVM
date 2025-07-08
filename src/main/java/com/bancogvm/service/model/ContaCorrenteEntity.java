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
@DiscriminatorValue("CORRENTE")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContaCorrenteEntity extends ContaEntity {

    private BigDecimal limiteChequeEspecial;

    public boolean sacar(BigDecimal valor) {
        BigDecimal disponivel = getSaldo().add(limiteChequeEspecial);
        if (disponivel.compareTo(valor) >= 0) {
            setSaldo(getSaldo().subtract(valor));
            return true;
        }
        return false;
    }

}
