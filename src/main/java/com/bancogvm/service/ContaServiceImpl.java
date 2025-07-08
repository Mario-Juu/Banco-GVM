package com.bancogvm.service;

import com.bancogvm.repository.ContaRepository;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.ContaPoupancaEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ContaServiceImpl implements ContaService{

    private final ContaRepository contaRepo;

    @Override
    public ContaCorrenteEntity criarCorrente(ContaCorrenteEntity cc) {
        cc.setDataAbertura(Instant.now());
        return contaRepo.save(cc);
    }

    @Override
    public ContaPoupancaEntity criarPoupanca(ContaPoupancaEntity cp) {
        cp.setDataAbertura(Instant.now());
        return contaRepo.save(cp);
    }

    @Override
    public List<ContaEntity> listarTodas() {
        return contaRepo.findAll();
    }

    @Override
    public ContaEntity buscarPorId(Long id) {
        return contaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }
}