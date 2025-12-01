package com.bancogvm.service;

import com.bancogvm.repository.EmprestimoRepository;
import com.bancogvm.service.model.EmprestimoEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmprestimoServiceImpl implements EmprestimoService {

    private final EmprestimoRepository repo;

    public EmprestimoEntity solicitar(EmprestimoEntity e) {
        e.setDataSolicitacao(Instant.now());
        e.setStatusEmprestimo("PENDENTE");

        // Calcular juros compostos: VF = VP * (1 + i)^n
        if (e.getValorSolicitado() != null && e.getTaxaJurosMensal() != null && e.getNumeroParcelas() != null) {
            double valorSolicitado = e.getValorSolicitado().doubleValue();
            double taxaJuros = e.getTaxaJurosMensal().doubleValue();
            int numeroParcelas = e.getNumeroParcelas();

            // Fórmula de juros compostos
            double valorTotal = valorSolicitado * Math.pow((1 + taxaJuros), numeroParcelas);
            e.setValorTotal(BigDecimal.valueOf(valorTotal));

            // Calcular valor da parcela
            double valorParcela = valorTotal / numeroParcelas;
            e.setValorParcela(BigDecimal.valueOf(valorParcela));
        }

        return repo.save(e);
    }

    public EmprestimoEntity aprovar(Long id, BigDecimal valorAprovado) {
        EmprestimoEntity e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        e.setValorAprovado(valorAprovado);
        e.setDataAprovacao(Instant.now());
        e.setStatusEmprestimo("APROVADO");
        e.setMotivoRejeicao(null);
        return repo.save(e);
    }

    public EmprestimoEntity rejeitar(Long id, String motivo) {
        // Validar motivo obrigatório
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Motivo de rejeição é obrigatório");
        }

        EmprestimoEntity e = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        e.setStatusEmprestimo("REJEITADO");
        e.setMotivoRejeicao(motivo);
        e.setDataAprovacao(Instant.now());
        return repo.save(e);
    }

    public List<EmprestimoEntity> listarTodos() {
        return repo.findAll();
    }

    public EmprestimoEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
    }
}