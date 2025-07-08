package com.bancogvm.service;

import com.bancogvm.repository.TransacaoRepository;
import com.bancogvm.service.model.TransacaoEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository repo;

    @Override
    public TransacaoEntity registrar(TransacaoEntity t) {
        t.setDataHora(Instant.now());
        t.setStatusTransacao("PENDENTE");
        // aqui poderia chamar lógica de débito/crédito nas contas
        t.setStatusTransacao("CONCLUIDA");
        return repo.save(t);
    }

    @Override
    public List<TransacaoEntity> listarTodas() {
        return repo.findAll();
    }

    @Override
    public TransacaoEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    @Override
    public List<TransacaoEntity> extratoPorConta(Long contaId) {
        List<TransacaoEntity> saídas = repo.findByContaOrigemId(contaId);
        List<TransacaoEntity> entradas = repo.findByContaDestinoId(contaId);
        saídas.addAll(entradas);
        return saídas;
    }
}
