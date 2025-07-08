package com.bancogvm.service;

import com.bancogvm.repository.*;
import com.bancogvm.service.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TitularContaServiceImpl implements TitularContaService{

    private final TitularContaRepository repo;
    private final ClienteRepository clienteRepo;
    private final ContaRepository contaRepo;

    @Override
    public TitularContaEntity vincular(Long clienteId, Long contaId, String tipo) {
        var cliente = clienteRepo.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        var conta = contaRepo.findById(contaId)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        var t = TitularContaEntity.builder()
                .cliente(cliente)
                .conta(conta)
                .tipoTitularidade(tipo)
                .dataAssociacao(Instant.now())
                .build();
        return repo.save(t);
    }

    @Override
    public void desvincular(Long id) {
        repo.deleteById(id);
    }

    @Override
    public List<TitularContaEntity> listar() {
        return repo.findAll();
    }
}
