package com.bancogvm.service;

import com.bancogvm.repository.ContaRepository;
import com.bancogvm.service.model.ContaCorrenteEntity;
import com.bancogvm.service.model.ContaEntity;
import com.bancogvm.service.model.ContaPoupancaEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * TU-06 a TU-10: Testes Unitários para ContaServiceImpl
 * Estes testes NÃO utilizam mocks, mas sim o banco H2 em memória.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Unitários - ContaServiceImpl (SEM MOCKS)")
public class ContaServiceImplTest {

    @Autowired
    private ContaService contaService;

    @Autowired
    private ContaRepository contaRepository;

    @BeforeEach
    void setUp() {
        contaRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        contaRepository.deleteAll();
    }

    /**
     * TU-06: Criação de Conta Corrente Válida
     * Objetivo: Verificar se o método criarCorrente() cria uma conta corrente com limite de cheque especial.
     */
    @Test
    @DisplayName("TU-06-CT-01: Deve criar conta corrente com limite cheque especial")
    void deveCriarContaCorrenteValida() {
        // Given
        ContaCorrenteEntity contaCorrente = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.valueOf(1000.00))
                .build();
        contaCorrente.setNumeroConta("12345-6");
        contaCorrente.setAgencia("0001");
        contaCorrente.setSaldo(BigDecimal.valueOf(0.00));
        contaCorrente.setStatusConta("ATIVA");

        // When
        ContaCorrenteEntity contaSalva = contaService.criarCorrente(contaCorrente);

        // Then
        assertThat(contaSalva).isNotNull();
        assertThat(contaSalva.getId()).isNotNull();
        assertThat(contaSalva.getDataAbertura()).isNotNull();
        assertThat(contaSalva.getLimiteChequeEspecial()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
        assertThat(contaSalva.getAgencia()).isEqualTo("0001");
        assertThat(contaSalva.getNumeroConta()).isEqualTo("12345-6");
    }

    /**
     * TU-07: Criação de Conta Poupança Válida
     * Objetivo: Verificar se o método criarPoupanca() cria uma conta poupança com taxa de juros anual.
     */
    @Test
    @DisplayName("TU-07-CT-01: Deve criar conta poupança com taxa de juros anual")
    void deveCriarContaPoupancaValida() {
        // Given
        ContaPoupancaEntity contaPoupanca = ContaPoupancaEntity.builder()
                .taxaRendimentoAnual(BigDecimal.valueOf(0.05))
                .dataAniversario(LocalDate.now())
                .build();
        contaPoupanca.setNumeroConta("98765-4");
        contaPoupanca.setAgencia("0001");
        contaPoupanca.setSaldo(BigDecimal.valueOf(0.00));
        contaPoupanca.setStatusConta("ATIVA");

        // When
        ContaPoupancaEntity contaSalva = contaService.criarPoupanca(contaPoupanca);

        // Then
        assertThat(contaSalva).isNotNull();
        assertThat(contaSalva.getId()).isNotNull();
        assertThat(contaSalva.getDataAbertura()).isNotNull();
        assertThat(contaSalva.getTaxaRendimentoAnual()).isEqualByComparingTo(BigDecimal.valueOf(0.05));
        assertThat(contaSalva.getAgencia()).isEqualTo("0001");
        assertThat(contaSalva.getNumeroConta()).isEqualTo("98765-4");
    }

    /**
     * TU-08: Validação de Saldo Insuficiente em Saque
     * Objetivo: Verificar se o método sacar() retorna false quando não há saldo suficiente.
     */
    @Test
    @DisplayName("TU-08-CT-01: Deve retornar false ao tentar sacar valor maior que saldo disponível")
    void deveValidarSaldoInsuficiente() {
        // Given
        ContaCorrenteEntity conta = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.valueOf(0.00))
                .build();
        conta.setNumeroConta("11111-1");
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.valueOf(100.00));
        conta.setStatusConta("ATIVA");

        ContaCorrenteEntity contaSalva = contaService.criarCorrente(conta);

        // When
        boolean sucesso = contaSalva.sacar(BigDecimal.valueOf(150.00));

        // Then
        assertThat(sucesso).isFalse();
        assertThat(contaSalva.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
    }

    /**
     * TU-09: Validação de Saldo Suficiente em Saque
     * Objetivo: Verificar se o método sacar() permite saque quando há saldo suficiente.
     */
    @Test
    @DisplayName("TU-09-CT-01: Deve permitir saque quando há saldo suficiente")
    void devePermitirSaqueComSaldoSuficiente() {
        // Given
        ContaCorrenteEntity conta = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.valueOf(0.00))
                .build();
        conta.setNumeroConta("22222-2");
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.valueOf(100.00));
        conta.setStatusConta("ATIVA");

        ContaCorrenteEntity contaSalva = contaService.criarCorrente(conta);

        // When
        boolean sucesso = contaSalva.sacar(BigDecimal.valueOf(50.00));

        // Then
        assertThat(sucesso).isTrue();
        assertThat(contaSalva.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
    }

    /**
     * TU-10: Busca de Conta por Número
     * Objetivo: Verificar se o método buscarPorId() retorna a conta correta.
     */
    @Test
    @DisplayName("TU-10-CT-01: Deve buscar conta existente por ID")
    void deveBuscarContaPorId() {
        // Given
        ContaCorrenteEntity conta = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.valueOf(500.00))
                .build();
        conta.setNumeroConta("33333-3");
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.valueOf(1000.00));
        conta.setStatusConta("ATIVA");

        ContaCorrenteEntity contaSalva = contaService.criarCorrente(conta);
        Long idSalvo = contaSalva.getId();

        // When
        ContaEntity contaEncontrada = contaService.buscarPorId(idSalvo);

        // Then
        assertThat(contaEncontrada).isNotNull();
        assertThat(contaEncontrada.getId()).isEqualTo(idSalvo);
        assertThat(contaEncontrada.getNumeroConta()).isEqualTo("33333-3");
        assertThat(contaEncontrada.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(1000.00));
    }
}
