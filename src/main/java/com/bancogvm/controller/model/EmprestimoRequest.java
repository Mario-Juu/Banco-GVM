package com.bancogvm.controller.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EmprestimoRequest {
    private BigDecimal valorSolicitado;
    private BigDecimal taxaJurosMensal;
    private Integer numeroParcelas;
    private Long clienteId;
    private Long contaCreditoId;
}
