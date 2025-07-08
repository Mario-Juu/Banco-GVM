package com.bancogvm.repository;

import com.bancogvm.service.model.CartaoDebitoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartaoDebitoRepository  extends JpaRepository<CartaoDebitoEntity,  Long> {
}