package com.bancogvm.controller;

import com.bancogvm.service.BeneficiarioService;
import com.bancogvm.service.model.BeneficiarioEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiarios")
@AllArgsConstructor
public class BeneficiarioController {

    private final BeneficiarioService service;

    @PostMapping
    public ResponseEntity<BeneficiarioEntity> criar(@RequestBody BeneficiarioEntity b) {
        return ResponseEntity.ok(service.criar(b));
    }

    @GetMapping
    public ResponseEntity<List<BeneficiarioEntity>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeneficiarioEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
