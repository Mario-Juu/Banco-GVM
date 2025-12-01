package com.bancogvm.controller;

import com.bancogvm.controller.model.EmprestimoRequest;
import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.repository.ContaRepository;
import com.bancogvm.service.EmprestimoService;
import com.bancogvm.service.model.ClienteEntity;
import com.bancogvm.service.model.ContaCorrenteEntity;
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
    private final ClienteRepository clienteRepository;
    private final ContaRepository contaRepository;

    public EmprestimoController(EmprestimoService service, ClienteRepository clienteRepository, ContaRepository contaRepository) {
        this.service = service;
        this.clienteRepository = clienteRepository;
        this.contaRepository = contaRepository;
    }

    @PostMapping
    public ResponseEntity<EmprestimoEntity> solicitar(@RequestBody EmprestimoRequest request) {
        // Buscar cliente e conta pelos IDs
        ClienteEntity cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        ContaCorrenteEntity conta = (ContaCorrenteEntity) contaRepository.findById(request.getContaCreditoId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        // Criar entidade de empréstimo
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(request.getValorSolicitado())
                .taxaJurosMensal(request.getTaxaJurosMensal())
                .numeroParcelas(request.getNumeroParcelas())
                .cliente(cliente)
                .contaCredito(conta)
                .build();

        return ResponseEntity.ok(service.solicitar(emprestimo));
    }

    @PostMapping("/{id}/aprovar")
    public ResponseEntity<EmprestimoEntity> aprovar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body
    ) {
        Object valorObj = body.get("valorAprovado");
        BigDecimal valorAprovado = null;

        if (valorObj instanceof Number) {
            valorAprovado = BigDecimal.valueOf(((Number) valorObj).doubleValue());
        } else if (valorObj instanceof String) {
            valorAprovado = new BigDecimal((String) valorObj);
        } else if (valorObj != null) {
            valorAprovado = new BigDecimal(valorObj.toString());
        }

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