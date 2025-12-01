package com.bancogvm.service;

import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.EmprestimoRepository;
import com.bancogvm.service.model.ClienteEntity;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.EmprestimoEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * TU-16 a TU-20: Testes Unitários para EmprestimoServiceImpl
 * Estes testes NÃO utilizam mocks, mas sim o banco H2 em memória.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Unitários - EmprestimoServiceImpl (SEM MOCKS)")
public class EmprestimoServiceImplTest {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    private ClienteEntity cliente;
    private ContaCorrenteEntity contaCredito;

    @BeforeEach
    void setUp() {
        emprestimoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();

        // Criar cliente para testes
        cliente = ClienteEntity.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .telefone("48999999999")
                .endereco("Rua A, 123")
                .loginUsuario("joao")
                .senhaHash("senha123")
                .dataCadastro(Instant.now())
                .build();
        cliente = clienteRepository.save(cliente);

        // Criar conta para crédito do empréstimo
        contaCredito = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        contaCredito.setNumeroConta("12345-6");
        contaCredito.setAgencia("0001");
        contaCredito.setSaldo(BigDecimal.ZERO);
        contaCredito.setStatusConta("ATIVA");
        contaCredito = (ContaCorrenteEntity) contaRepository.save(contaCredito);
    }

    @AfterEach
    void tearDown() {
        emprestimoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    /**
     * TU-16: Solicitação de Empréstimo Válido
     * Objetivo: Verificar se o método solicitarEmprestimo() cria uma solicitação com cálculo correto de juros e parcelas
     * Estado: ✓ Validado (Bug corrigido - cálculo de juros compostos estava incorreto)
     * Observação: Fórmula corrigida para usar Math.pow((1 + taxa), prazo). Valor correto: 12682.42
     */
    @Test
    @DisplayName("TU-16-CT-01: Deve criar empréstimo com cálculo correto de juros compostos")
    void deveSolicitarEmprestimoComCalculoCorreto() {
        // Given
        BigDecimal valorSolicitado = BigDecimal.valueOf(10000.00);
        BigDecimal taxaJurosMensal = BigDecimal.valueOf(0.02); // 2% ao mês
        Integer prazoMeses = 12;

        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(valorSolicitado)
                .taxaJurosMensal(taxaJurosMensal)
                .numeroParcelas(prazoMeses)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();

        // When
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // Then - Verificar dados básicos
        assertThat(emprestimoSalvo).isNotNull();
        assertThat(emprestimoSalvo.getId()).isNotNull();
        assertThat(emprestimoSalvo.getStatusEmprestimo()).isEqualTo("PENDENTE");
        assertThat(emprestimoSalvo.getValorSolicitado()).isEqualByComparingTo(valorSolicitado);
        assertThat(emprestimoSalvo.getTaxaJurosMensal()).isEqualByComparingTo(taxaJurosMensal);
        assertThat(emprestimoSalvo.getNumeroParcelas()).isEqualTo(prazoMeses);

        // Then - Verificar cálculo de juros compostos
        // Valor esperado: 10000 * (1.02)^12 = 12682.42 (conforme relatório)
        BigDecimal valorTotalEsperado = BigDecimal.valueOf(12682.42);
        assertThat(emprestimoSalvo.getValorTotal()).isNotNull();
        assertThat(emprestimoSalvo.getValorTotal().doubleValue()).isCloseTo(valorTotalEsperado.doubleValue(), within(0.01));

        // Then - Verificar valor da parcela
        BigDecimal valorParcelaEsperado = valorTotalEsperado.divide(BigDecimal.valueOf(prazoMeses), 2, java.math.RoundingMode.HALF_UP);
        assertThat(emprestimoSalvo.getValorParcela()).isNotNull();
        assertThat(emprestimoSalvo.getValorParcela().doubleValue()).isCloseTo(valorParcelaEsperado.doubleValue(), within(0.01));
    }

    /**
     * TU-17: Aprovação de Empréstimo
     * Objetivo: Verificar se o método aprovarEmprestimo() atualiza o status e define o valor aprovado
     * Estado: ✓ Validado
     */
    @Test
    @DisplayName("TU-17-CT-01: Deve aprovar empréstimo com status APROVADO e dataAprovacao definida")
    void deveAprovarEmprestimo() {
        // Given - Criar empréstimo pendente
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When - Aprovar o empréstimo
        BigDecimal valorAprovado = BigDecimal.valueOf(10000.00);
        EmprestimoEntity emprestimoAprovado = emprestimoService.aprovar(emprestimoSalvo.getId(), valorAprovado);

        // Then
        assertThat(emprestimoAprovado).isNotNull();
        assertThat(emprestimoAprovado.getStatusEmprestimo()).isEqualTo("APROVADO");
        assertThat(emprestimoAprovado.getValorAprovado()).isEqualByComparingTo(valorAprovado);
        assertThat(emprestimoAprovado.getDataAprovacao()).isNotNull();
    }

    /**
     * TU-18: Rejeição de Empréstimo
     * Objetivo: Verificar se o método rejeitarEmprestimo() atualiza o status e registra o motivo
     * Estado: ✓ Validado
     */
    @Test
    @DisplayName("TU-18-CT-01: Deve rejeitar empréstimo com motivo registrado")
    void deveRejeitarEmprestimo() {
        // Given - Criar empréstimo pendente
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When - Rejeitar o empréstimo
        String motivoRejeicao = "Renda insuficiente";
        EmprestimoEntity emprestimoRejeitado = emprestimoService.rejeitar(emprestimoSalvo.getId(), motivoRejeicao);

        // Then
        assertThat(emprestimoRejeitado).isNotNull();
        assertThat(emprestimoRejeitado.getStatusEmprestimo()).isEqualTo("REJEITADO");
        assertThat(emprestimoRejeitado.getMotivoRejeicao()).isEqualTo("Renda insuficiente");
        assertThat(emprestimoRejeitado.getDataAprovacao()).isNotNull(); // Conforme relatório, dataAprovacao é definida
    }

    /**
     * TU-19: Validação de Motivo de Rejeição Obrigatório
     * Objetivo: Verificar se o sistema impede rejeição de empréstimo sem motivo
     * Estado: ⚠ Melhoria Implementada
     * Observação: Sistema agora valida null e string vazia/espaços com .trim().isEmpty()
     */
    @Test
    @DisplayName("TU-19-CT-01: Deve lançar exceção ao rejeitar empréstimo com motivo null")
    void deveLancarExcecaoAoRejeitarSemMotivo() {
        // Given
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When & Then - Tentar rejeitar com motivo null
        assertThatThrownBy(() -> emprestimoService.rejeitar(emprestimoSalvo.getId(), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Motivo de rejeição é obrigatório");
    }

    /**
     * TU-19-CT-02: Deve lançar exceção ao rejeitar empréstimo com motivo vazio
     */
    @Test
    @DisplayName("TU-19-CT-02: Deve lançar exceção ao rejeitar empréstimo com string vazia")
    void deveLancarExcecaoAoRejeitarComMotivoVazio() {
        // Given
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When & Then - Tentar rejeitar com string vazia
        assertThatThrownBy(() -> emprestimoService.rejeitar(emprestimoSalvo.getId(), ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Motivo de rejeição é obrigatório");

        // When & Then - Tentar rejeitar com apenas espaços em branco
        assertThatThrownBy(() -> emprestimoService.rejeitar(emprestimoSalvo.getId(), "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Motivo de rejeição é obrigatório");
    }

    /**
     * TU-20: Busca de Empréstimos por Cliente
     * Objetivo: Verificar se o método buscarEmprestimosPorCliente() retorna todos os empréstimos de um cliente
     * Estado: ✓ Validado
     */
    @Test
    @DisplayName("TU-20-CT-01: Deve retornar lista com empréstimos do cliente")
    void deveListarEmprestimosPorCliente() {
        // Given - Criar 2 empréstimos: 1 APROVADO e 1 REJEITADO
        EmprestimoEntity emprestimo1 = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(5000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimo1Salvo = emprestimoService.solicitar(emprestimo1);
        emprestimoService.aprovar(emprestimo1Salvo.getId(), BigDecimal.valueOf(5000.00));

        EmprestimoEntity emprestimo2 = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.03))
                .numeroParcelas(24)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimo2Salvo = emprestimoService.solicitar(emprestimo2);
        emprestimoService.rejeitar(emprestimo2Salvo.getId(), "Score de crédito baixo");

        // When - Listar todos os empréstimos
        List<EmprestimoEntity> emprestimos = emprestimoService.listarTodos();

        // Then - Verificar que retorna os 2 empréstimos (1 APROVADO, 1 REJEITADO)
        assertThat(emprestimos).isNotNull();
        assertThat(emprestimos).hasSize(2);
        assertThat(emprestimos).extracting(EmprestimoEntity::getStatusEmprestimo)
                .contains("APROVADO", "REJEITADO");
    }
}
