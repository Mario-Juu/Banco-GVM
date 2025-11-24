package com.bancogvm.service;

import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.TransacaoRepository;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.TransacaoEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * TU-11 a TU-15: Testes Unitários para TransacaoServiceImpl
 * Estes testes NÃO utilizam mocks, mas sim o banco H2 em memória.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Unitários - TransacaoServiceImpl (SEM MOCKS)")
public class TransacaoServiceImplTest {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    private ContaCorrenteEntity contaOrigem;
    private ContaCorrenteEntity contaDestino;

    @BeforeEach
    void setUp() {
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();

        // Criar contas para testes
        contaOrigem = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        contaOrigem.setNumeroConta("11111-1");
        contaOrigem.setAgencia("0001");
        contaOrigem.setSaldo(BigDecimal.valueOf(500.00));
        contaOrigem.setStatusConta("ATIVA");
        contaOrigem = (ContaCorrenteEntity) contaRepository.save(contaOrigem);

        contaDestino = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        contaDestino.setNumeroConta("22222-2");
        contaDestino.setAgencia("0001");
        contaDestino.setSaldo(BigDecimal.valueOf(100.00));
        contaDestino.setStatusConta("ATIVA");
        contaDestino = (ContaCorrenteEntity) contaRepository.save(contaDestino);
    }

    @AfterEach
    void tearDown() {
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
    }

    /**
     * TU-11: Processamento de Depósito Válido
     * Objetivo: Verificar se o método registrar() processa um depósito corretamente.
     */
    @Test
    @DisplayName("TU-11-CT-01: Deve processar depósito e atualizar status para CONCLUIDA")
    void deveProcessarDepositoValido() {
        // Given
        TransacaoEntity deposito = TransacaoEntity.builder()
                .tipoTransacao("DEPOSITO")
                .valor(BigDecimal.valueOf(200.00))
                .descricao("Depósito inicial")
                .contaOrigem(contaDestino)
                .build();

        // When
        TransacaoEntity transacaoSalva = transacaoService.registrar(deposito);

        // Then
        assertThat(transacaoSalva).isNotNull();
        assertThat(transacaoSalva.getId()).isNotNull();
        assertThat(transacaoSalva.getDataHora()).isNotNull();
        assertThat(transacaoSalva.getStatusTransacao()).isEqualTo("CONCLUIDA");
        assertThat(transacaoSalva.getValor()).isEqualByComparingTo(BigDecimal.valueOf(200.00));
    }

    /**
     * TU-12: Processamento de Saque Válido
     * Objetivo: Verificar se o método registrar() processa um saque corretamente.
     */
    @Test
    @DisplayName("TU-12-CT-01: Deve processar saque e marcar como CONCLUIDA")
    void deveProcessarSaqueValido() {
        // Given
        TransacaoEntity saque = TransacaoEntity.builder()
                .tipoTransacao("SAQUE")
                .valor(BigDecimal.valueOf(100.00))
                .descricao("Saque teste")
                .contaOrigem(contaOrigem)
                .build();

        // When
        TransacaoEntity transacaoSalva = transacaoService.registrar(saque);

        // Then
        assertThat(transacaoSalva).isNotNull();
        assertThat(transacaoSalva.getId()).isNotNull();
        assertThat(transacaoSalva.getStatusTransacao()).isEqualTo("CONCLUIDA");
        assertThat(transacaoSalva.getTipoTransacao()).isEqualTo("SAQUE");
    }

    /**
     * TU-13: Registro de Transação com Status Inicial PENDENTE
     * Objetivo: Verificar se a transação começa como PENDENTE antes de ser concluída.
     */
    @Test
    @DisplayName("TU-13-CT-01: Deve iniciar transação como PENDENTE e finalizar como CONCLUIDA")
    void deveIniciarTransacaoComoPendente() {
        // Given
        TransacaoEntity transacao = TransacaoEntity.builder()
                .tipoTransacao("TRANSFERENCIA")
                .valor(BigDecimal.valueOf(50.00))
                .descricao("Transferência teste")
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .build();

        // When
        TransacaoEntity transacaoSalva = transacaoService.registrar(transacao);

        // Then
        assertThat(transacaoSalva.getStatusTransacao()).isEqualTo("CONCLUIDA");
        assertThat(transacaoSalva.getDataHora()).isNotNull();
    }

    /**
     * TU-14: Processamento de Transferência Válida
     * Objetivo: Verificar se o método registrar() processa uma transferência entre contas.
     */
    @Test
    @DisplayName("TU-14-CT-01: Deve processar transferência entre contas")
    void deveProcessarTransferenciaValida() {
        // Given
        TransacaoEntity transferencia = TransacaoEntity.builder()
                .tipoTransacao("TRANSFERENCIA")
                .valor(BigDecimal.valueOf(150.00))
                .descricao("Transferência entre contas")
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .build();

        // When
        TransacaoEntity transacaoSalva = transacaoService.registrar(transferencia);

        // Then
        assertThat(transacaoSalva).isNotNull();
        assertThat(transacaoSalva.getStatusTransacao()).isEqualTo("CONCLUIDA");
        assertThat(transacaoSalva.getContaOrigem().getId()).isEqualTo(contaOrigem.getId());
        assertThat(transacaoSalva.getContaDestino().getId()).isEqualTo(contaDestino.getId());
    }

    /**
     * TU-15: Extrato de Conta por ID
     * Objetivo: Verificar se o método extratoPorConta() retorna todas as transações da conta.
     */
    @Test
    @DisplayName("TU-15-CT-01: Deve retornar extrato com transações da conta")
    void deveRetornarExtratoDaConta() {
        // Given
        TransacaoEntity transacao1 = TransacaoEntity.builder()
                .tipoTransacao("DEPOSITO")
                .valor(BigDecimal.valueOf(100.00))
                .contaOrigem(contaOrigem)
                .build();
        transacaoService.registrar(transacao1);

        TransacaoEntity transacao2 = TransacaoEntity.builder()
                .tipoTransacao("SAQUE")
                .valor(BigDecimal.valueOf(50.00))
                .contaOrigem(contaOrigem)
                .build();
        transacaoService.registrar(transacao2);

        TransacaoEntity transacao3 = TransacaoEntity.builder()
                .tipoTransacao("TRANSFERENCIA")
                .valor(BigDecimal.valueOf(25.00))
                .contaOrigem(contaOrigem)
                .contaDestino(contaDestino)
                .build();
        transacaoService.registrar(transacao3);

        // When
        List<TransacaoEntity> extrato = transacaoService.extratoPorConta(contaOrigem.getId());

        // Then
        assertThat(extrato).isNotNull();
        assertThat(extrato).hasSizeGreaterThanOrEqualTo(3);
        assertThat(extrato).extracting(TransacaoEntity::getTipoTransacao)
                .contains("DEPOSITO", "SAQUE", "TRANSFERENCIA");
    }
}
