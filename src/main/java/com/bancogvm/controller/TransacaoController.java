package com.bancogvm.controller;


import com.bancogvm.controller.model.TransacaoRequest;
import com.bancogvm.repository.ContaRepository;
import com.bancogvm.service.TransacaoService;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.TransacaoEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
@AllArgsConstructor
public class TransacaoController {

    private final TransacaoService service;
    private final ContaRepository contaRepo;

    @PostMapping
    public ResponseEntity<TransacaoEntity> criar(@RequestBody TransacaoRequest req) {
        if (req.getContaDestinoId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "campo contaDestinoId é obrigatório");
        }
        TransacaoEntity t = new TransacaoEntity();
        t.setValor(req.getValor());
        t.setTipoTransacao(req.getTipoTransacao());
        t.setDescricao(req.getDescricao());

        if (req.getContaOrigemId() != null) {
            ContaCorrenteEntity origem = (ContaCorrenteEntity) contaRepo.findById(req.getContaOrigemId())
                    .orElseThrow(() -> new RuntimeException("Conta origem não encontrada"));
            t.setContaOrigem(origem);
        }

        ContaCorrenteEntity destino = (ContaCorrenteEntity) contaRepo.findById(req.getContaDestinoId())
                .orElseThrow(() -> new RuntimeException("Conta destino não encontrada"));
        t.setContaDestino(destino);

        TransacaoEntity salvo = service.registrar(t);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping
    public ResponseEntity<List<TransacaoEntity>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/extrato/{contaId}")
    public ResponseEntity<List<TransacaoEntity>> extrato(@PathVariable Long contaId) {
        return ResponseEntity.ok(service.extratoPorConta(contaId));
    }
}