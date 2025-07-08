package com.bancogvm.repository;

import com.bancogvm.service.model.TitularContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TitularContaRepository extends JpaRepository<TitularContaEntity, Long> {
    List<TitularContaEntity> findByClienteId(Long clienteId);
    List<TitularContaEntity> findByContaId(Long contaId);
}