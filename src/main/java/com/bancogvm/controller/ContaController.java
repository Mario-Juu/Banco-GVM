package com.bancogvm.controller;

import com.bancogvm.service.ContaService;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.ContaPoupancaEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@AllArgsConstructor
public class ContaController {

    private final ContaService service;

    @PostMapping("/corrente")
    public ResponseEntity<ContaCorrenteEntity> criarCorrente(@RequestBody ContaCorrenteEntity cc) {
        return ResponseEntity.ok(service.criarCorrente(cc));
    }

    @PostMapping("/poupanca")
    public ResponseEntity<ContaPoupancaEntity> criarPoupanca(@RequestBody ContaPoupancaEntity cp) {
        return ResponseEntity.ok(service.criarPoupanca(cp));
    }

    @GetMapping
    public ResponseEntity<List<ContaEntity>> listar() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
