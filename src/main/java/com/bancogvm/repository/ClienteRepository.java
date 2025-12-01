package com.bancogvm.repository;

import com.bancogvm.service.model.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {
    Optional<ClienteEntity> findByLoginUsuario(String loginUsuario);
    Optional<ClienteEntity> findByCpf(String cpf);
}
