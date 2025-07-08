package com.bancogvm.service;

import com.bancogvm.repository.BeneficiarioRepository;
import com.bancogvm.service.model.BeneficiarioEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BeneficiarioServiceImpl implements BeneficiarioService{

    private final BeneficiarioRepository repo;

    @Override
    public BeneficiarioEntity criar(BeneficiarioEntity b) {
        return repo.save(b);
    }

    @Override
    public List<BeneficiarioEntity> listarTodos() {
        return repo.findAll();
    }

    @Override
    public BeneficiarioEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Beneficiário não encontrado"));
    }
}
