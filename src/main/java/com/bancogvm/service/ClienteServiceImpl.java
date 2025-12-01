package com.bancogvm.service;

import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.service.model.ClienteEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repo;

    @Override
    public ClienteEntity cadastrar(ClienteEntity cliente) {
        // Validar CPF duplicado
        if (repo.findByCpf(cliente.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }

        cliente.setDataCadastro(Instant.now());
        // aqui você pode hashear a senha antes de salvar
        return repo.save(cliente);
    }

    @Override
    public List<ClienteEntity> listarTodos() {
        return repo.findAll();
    }

    @Override
    public ClienteEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }
}