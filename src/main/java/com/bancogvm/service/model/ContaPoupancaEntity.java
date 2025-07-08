package com.bancogvm.service.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("POUPANCA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContaPoupancaEntity extends ContaEntity {

    private BigDecimal taxaRendimentoAnual;
    private LocalDate dataAniversario;

    public void calcularRendimentoMensal() {
        BigDecimal mensal = getSaldo()
                .multiply(taxaRendimentoAnual)
                .divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP);
        setSaldo(getSaldo().add(mensal));
    }

}