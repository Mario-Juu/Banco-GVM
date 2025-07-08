package com.bancogvm.repository;

import com.bancogvm.service.model.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<ContaEntity, Long> {
}
