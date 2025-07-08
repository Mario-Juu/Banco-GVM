package com.bancogvm.repository;

import com.bancogvm.service.model.ContaCorrenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrenteEntity, Long> {
}
