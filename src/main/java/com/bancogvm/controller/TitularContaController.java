package com.bancogvm.controller;

import com.bancogvm.service.TitularContaService;
import com.bancogvm.service.model.TitularContaEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/titulares")
public class TitularContaController {

    private final TitularContaService service;

    public TitularContaController(TitularContaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TitularContaEntity> vincular(
            @RequestBody Map<String, Object> body
    ) {
        Long clienteId = Long.valueOf(body.get("clienteId").toString());
        Long contaId   = Long.valueOf(body.get("contaId").toString());
        String tipo    = body.get("tipoTitularidade").toString();
        return ResponseEntity.ok(service.vincular(clienteId, contaId, tipo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desvincular(@PathVariable Long id) {
        service.desvincular(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<TitularContaEntity>> listar() {
        return ResponseEntity.ok(service.listar());
    }
}
