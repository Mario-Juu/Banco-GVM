package com.bancogvm.repository;

import com.bancogvm.service.model.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<TransacaoEntity, Long> {
    List<TransacaoEntity> findByContaOrigemId(Long contaId);
    List<TransacaoEntity> findByContaDestinoId(Long contaId);
}
