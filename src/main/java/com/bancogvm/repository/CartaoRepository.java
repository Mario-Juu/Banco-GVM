package com.bancogvm.repository;

import com.bancogvm.service.model.CartaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoRepository extends JpaRepository<CartaoEntity, Long> {
}