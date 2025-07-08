package com.bancogvm.repository;

import com.bancogvm.service.model.BeneficiarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeneficiarioRepository extends JpaRepository<BeneficiarioEntity, Long> {
}
