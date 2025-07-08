package com.bancogvm.controller;

import com.bancogvm.service.ClienteService;
import com.bancogvm.service.model.ClienteEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@AllArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteEntity> criar(@RequestBody ClienteEntity cliente) {
        ClienteEntity criado = service.cadastrar(cliente);
        return ResponseEntity.ok(criado);
    }

    @GetMapping
    public ResponseEntity<List<ClienteEntity>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClienteEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
