package com.bancogvm.repository;

import com.bancogvm.service.model.EmprestimoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<EmprestimoEntity, Long> {
}
