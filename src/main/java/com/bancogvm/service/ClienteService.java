package com.bancogvm.service;

import com.bancogvm.service.model.ClienteEntity;

import java.util.List;

public interface ClienteService {

    ClienteEntity cadastrar(ClienteEntity cliente);
    List<ClienteEntity> listarTodos();
    ClienteEntity buscarPorId(Long id);
}
