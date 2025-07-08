package com.bancogvm.controller.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TransacaoRequest {
    private Long contaOrigemId;
    private Long contaDestinoId;
    private BigDecimal valor;
    private String tipoTransacao;
    private String descricao;
}
