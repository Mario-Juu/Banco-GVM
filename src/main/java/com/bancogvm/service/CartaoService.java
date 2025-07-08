package com.bancogvm.service;

import com.bancogvm.service.model.CartaoCreditoEntity;
import com.bancogvm.service.model.CartaoDebitoEntity;
import com.bancogvm.service.model.CartaoEntity;

import java.util.List;

public interface CartaoService {

    CartaoCreditoEntity emitirCredito(CartaoCreditoEntity cc);
    CartaoDebitoEntity emitirDebito(CartaoDebitoEntity cd);
    List<CartaoEntity> listarTodos();
    CartaoEntity buscarPorId(Long id);
    void bloquear(Long id);
    void desbloquear(Long id);
}
