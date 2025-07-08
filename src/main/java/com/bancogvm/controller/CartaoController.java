package com.bancogvm.controller;

import com.bancogvm.service.CartaoService;
import com.bancogvm.service.model.CartaoCreditoEntity;
import com.bancogvm.service.model.CartaoDebitoEntity;
import com.bancogvm.service.model.CartaoEntity;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cartoes")
@AllArgsConstructor
public class CartaoController {

    private final CartaoService service;

    @PostMapping("/credito")
    public ResponseEntity<CartaoCreditoEntity> criarCredito(@RequestBody CartaoCreditoEntity cc) {
        return ResponseEntity.ok(service.emitirCredito(cc));
    }

    @PostMapping("/debito")
    public ResponseEntity<CartaoDebitoEntity> criarDebito(@RequestBody CartaoDebitoEntity cd) {
        return ResponseEntity.ok(service.emitirDebito(cd));
    }

    @GetMapping
    public ResponseEntity<List<CartaoEntity>> listar() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartaoEntity> porId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/{id}/bloquear")
    public ResponseEntity<Void> bloquear(@PathVariable Long id) {
        service.bloquear(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<Void> desbloquear(@PathVariable Long id) {
        service.desbloquear(id);
        return ResponseEntity.ok().build();
    }
}