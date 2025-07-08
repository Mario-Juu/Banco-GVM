package com.bancogvm.repository;

import com.bancogvm.service.model.ContaPoupancaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaPoupancaRepository extends JpaRepository<ContaPoupancaEntity, Long> {
}
