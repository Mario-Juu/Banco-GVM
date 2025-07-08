package com.bancogvm.repository;

import com.bancogvm.service.model.CartaoCreditoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoCreditoRepository extends JpaRepository<CartaoCreditoEntity, Long> {
}
