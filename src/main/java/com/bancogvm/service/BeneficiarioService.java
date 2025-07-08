package com.bancogvm.service;

import com.bancogvm.service.model.BeneficiarioEntity;

import java.util.List;

public interface BeneficiarioService {

    BeneficiarioEntity criar(BeneficiarioEntity b);
    List<BeneficiarioEntity> listarTodos();
    BeneficiarioEntity buscarPorId(Long id);
}
