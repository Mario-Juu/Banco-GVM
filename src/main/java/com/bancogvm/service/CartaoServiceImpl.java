package com.bancogvm.service;

import com.bancogvm.repository.CartaoRepository;
import com.bancogvm.service.model.CartaoCreditoEntity;
import com.bancogvm.service.model.CartaoDebitoEntity;
import com.bancogvm.service.model.CartaoEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CartaoServiceImpl implements CartaoService{

    private final CartaoRepository repo;

    @Override
    public CartaoCreditoEntity emitirCredito(CartaoCreditoEntity cc) {
        cc.setDataEmissao(Instant.now());
        cc.setStatusCartao("ATIVO");
        return repo.save(cc);
    }

    @Override
    public CartaoDebitoEntity emitirDebito(CartaoDebitoEntity cd) {
        cd.setDataEmissao(Instant.now());
        cd.setStatusCartao("ATIVO");
        return repo.save(cd);
    }

    @Override
    public List<CartaoEntity> listarTodos() {
        return repo.findAll();
    }

    @Override
    public CartaoEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cartão não encontrado"));
    }

    @Override
    public void bloquear(Long id) {
        CartaoEntity c = buscarPorId(id);
        c.bloquear();
        repo.save(c);
    }

    @Override
    public void desbloquear(Long id) {
        CartaoEntity c = buscarPorId(id);
        c.desbloquear();
        repo.save(c);
    }
}