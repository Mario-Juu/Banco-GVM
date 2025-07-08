package com.bancogvm.service;

import com.bancogvm.service.model.TransacaoEntity;

import java.util.List;

public interface TransacaoService {

    TransacaoEntity registrar(TransacaoEntity t);
    List<TransacaoEntity> listarTodas();
    TransacaoEntity buscarPorId(Long id);
    List<TransacaoEntity> extratoPorConta(Long contaId);
}
