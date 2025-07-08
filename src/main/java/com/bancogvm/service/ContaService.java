package com.bancogvm.service;

import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.ContaPoupancaEntity;

import java.util.List;

public interface ContaService {

    ContaCorrenteEntity criarCorrente(ContaCorrenteEntity cc);
    ContaPoupancaEntity criarPoupanca(ContaPoupancaEntity cp);
    List<ContaEntity> listarTodas();
    ContaEntity buscarPorId(Long id);
}
