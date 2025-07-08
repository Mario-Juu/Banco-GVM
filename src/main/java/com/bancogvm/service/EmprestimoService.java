package com.bancogvm.service;

import com.bancogvm.service.model.EmprestimoEntity;

import java.math.BigDecimal;
import java.util.List;

public interface EmprestimoService {

    EmprestimoEntity solicitar(EmprestimoEntity e);
    EmprestimoEntity aprovar(Long id, BigDecimal valorAprovado);
    EmprestimoEntity rejeitar(Long id, String motivo);
    List<EmprestimoEntity> listarTodos();
    EmprestimoEntity buscarPorId(Long id);
}
