package com.bancogvm.service;

import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.TransacaoRepository;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.TransacaoEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class TransacaoServiceImpl implements TransacaoService {

    private final TransacaoRepository repo;
    private final ContaRepository contaRepository;

    @Override
    @Transactional
    public TransacaoEntity registrar(TransacaoEntity t) {
        t.setDataHora(Instant.now());
        t.setStatusTransacao("PENDENTE");

        try {
            String tipoTransacao = t.getTipoTransacao().toUpperCase();

            switch (tipoTransacao) {
                case "SAQUE":
                    processarSaque(t);
                    break;
                case "DEPOSITO":
                    processarDeposito(t);
                    break;
                case "TRANSFERENCIA":
                    processarTransferencia(t);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de transação inválido: " + tipoTransacao);
            }

            t.setStatusTransacao("CONCLUIDA");
        } catch (Exception e) {
            t.setStatusTransacao("FALHOU");
            log.error("Erro ao processar transação: {}", e.getMessage());
        }

        return repo.save(t);
    }

    private void processarSaque(TransacaoEntity t) {
        ContaCorrenteEntity conta = t.getContaOrigem();
        if (conta.getSaldo().compareTo(t.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para saque");
        }
        conta.setSaldo(conta.getSaldo().subtract(t.getValor()));
        contaRepository.save(conta);
    }

    private void processarDeposito(TransacaoEntity t) {
        ContaCorrenteEntity conta = t.getContaOrigem();
        conta.setSaldo(conta.getSaldo().add(t.getValor()));
        contaRepository.save(conta);
    }

    private void processarTransferencia(TransacaoEntity t) {
        ContaCorrenteEntity origem = t.getContaOrigem();
        ContaCorrenteEntity destino = t.getContaDestino();

        if (origem.getSaldo().compareTo(t.getValor()) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência");
        }

        origem.setSaldo(origem.getSaldo().subtract(t.getValor()));
        destino.setSaldo(destino.getSaldo().add(t.getValor()));

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    @Override
    public List<TransacaoEntity> listarTodas() {
        return repo.findAll();
    }

    @Override
    public TransacaoEntity buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transação não encontrada"));
    }

    @Override
    public List<TransacaoEntity> extratoPorConta(Long contaId) {
        List<TransacaoEntity> saídas = repo.findByContaOrigemId(contaId);
        List<TransacaoEntity> entradas = repo.findByContaDestinoId(contaId);
        saídas.addAll(entradas);
        return saídas;
    }
}
