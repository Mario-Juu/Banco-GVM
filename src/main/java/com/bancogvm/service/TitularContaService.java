package com.bancogvm.service;

import com.bancogvm.service.model.TitularContaEntity;
import java.util.List;

public interface TitularContaService {

    TitularContaEntity vincular(Long clienteId, Long contaId, String tipo);
    void desvincular(Long id);
    List<TitularContaEntity> listar();
}
