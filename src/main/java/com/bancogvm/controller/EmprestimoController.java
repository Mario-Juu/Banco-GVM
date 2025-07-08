package com.bancogvm.controller;

import com.bancogvm.service.EmprestimoService;
import com.bancogvm.service.model.EmprestimoEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmprestimoEntity> solicitar(@RequestBody EmprestimoEntity e) {
        return ResponseEntity.ok(service.solicitar(e));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<EmprestimoEntity> aprovar(
            @PathVariable Long id,
            @RequestBody Map<String, BigDecimal> body
    ) {
        BigDecimal valorAprovado = body.get("valorAprovado");
        return ResponseEntity.ok(service.aprovar(id, valorAprovado));
    }

    @PostMapping("/{id}/rejeitar")
    public ResponseEntity<EmprestimoEntity> rejeitar(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String motivo = body.get("motivo");
        return ResponseEntity.ok(service.rejeitar(id, motivo));
    }

    @GetMapping
    public ResponseEntity<List<EmprestimoEntity>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmprestimoEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}