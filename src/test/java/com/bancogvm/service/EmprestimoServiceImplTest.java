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
     * TU-16: Cálculo de Juros de Empréstimo
     * Objetivo: Verificar se os valores de juros e parcelas são calculados corretamente.
     * Nota: Como o cálculo não está implementado no service, testamos apenas os dados armazenados.
     */
    @Test
    @DisplayName("TU-16-CT-01: Deve armazenar corretamente valores de empréstimo com taxa de juros")
    void deveArmazenarValoresDeEmprestimo() {
        // Given
        BigDecimal valorSolicitado = BigDecimal.valueOf(1000.00);
        BigDecimal taxaJuros = BigDecimal.valueOf(0.02); // 2% ao mês
        Integer numeroParcelas = 12;

        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(valorSolicitado)
                .taxaJurosMensal(taxaJuros)
                .numeroParcelas(numeroParcelas)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();

        // When
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // Then
        assertThat(emprestimoSalvo).isNotNull();
        assertThat(emprestimoSalvo.getValorSolicitado()).isEqualByComparingTo(valorSolicitado);
        assertThat(emprestimoSalvo.getTaxaJurosMensal()).isEqualByComparingTo(taxaJuros);
        assertThat(emprestimoSalvo.getNumeroParcelas()).isEqualTo(numeroParcelas);
    }

    /**
     * TU-17: Solicitação de Empréstimo Válida
     * Objetivo: Verificar se o método solicitar() cria empréstimo com status PENDENTE.
     */
    @Test
    @DisplayName("TU-17-CT-01: Deve criar empréstimo com status PENDENTE e dataSolicitacao preenchida")
    void deveSolicitarEmprestimoValido() {
        // Given
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(5000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.03))
                .numeroParcelas(24)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();

        // When
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // Then
        assertThat(emprestimoSalvo).isNotNull();
        assertThat(emprestimoSalvo.getId()).isNotNull();
        assertThat(emprestimoSalvo.getStatusEmprestimo()).isEqualTo("PENDENTE");
        assertThat(emprestimoSalvo.getDataSolicitacao()).isNotNull();
        assertThat(emprestimoSalvo.getValorAprovado()).isNull();
    }

    /**
     * TU-18: Aprovação de Empréstimo com Valor Diferente
     * Objetivo: Verificar se o método aprovar() atualiza o status e o valor aprovado.
     */
    @Test
    @DisplayName("TU-18-CT-01: Deve aprovar empréstimo com valor diferente do solicitado")
    void deveAprovarEmprestimoComValorDiferente() {
        // Given
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(10000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.025))
                .numeroParcelas(36)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When
        BigDecimal valorAprovado = BigDecimal.valueOf(8000.00);
        EmprestimoEntity emprestimoAprovado = emprestimoService.aprovar(emprestimoSalvo.getId(), valorAprovado);

        // Then
        assertThat(emprestimoAprovado).isNotNull();
        assertThat(emprestimoAprovado.getStatusEmprestimo()).isEqualTo("APROVADO");
        assertThat(emprestimoAprovado.getValorAprovado()).isEqualByComparingTo(valorAprovado);
        assertThat(emprestimoAprovado.getDataAprovacao()).isNotNull();
        assertThat(emprestimoAprovado.getMotivoRejeicao()).isNull();
    }

    /**
     * TU-19: Rejeição de Empréstimo com Motivo
     * Objetivo: Verificar se o método rejeitar() atualiza o status e registra o motivo.
     */
    @Test
    @DisplayName("TU-19-CT-01: Deve rejeitar empréstimo com motivo registrado")
    void deveRejeitarEmprestimoComMotivo() {
        // Given
        EmprestimoEntity emprestimo = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(50000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.04))
                .numeroParcelas(48)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        EmprestimoEntity emprestimoSalvo = emprestimoService.solicitar(emprestimo);

        // When
        String motivoRejeicao = "Renda insuficiente";
        EmprestimoEntity emprestimoRejeitado = emprestimoService.rejeitar(emprestimoSalvo.getId(), motivoRejeicao);

        // Then
        assertThat(emprestimoRejeitado).isNotNull();
        assertThat(emprestimoRejeitado.getStatusEmprestimo()).isEqualTo("REJEITADO");
        assertThat(emprestimoRejeitado.getMotivoRejeicao()).isEqualTo(motivoRejeicao);
        assertThat(emprestimoRejeitado.getDataAprovacao()).isNotNull();
        assertThat(emprestimoRejeitado.getValorAprovado()).isNull();
    }

    /**
     * TU-20: Busca de Histórico de Empréstimos por Cliente
     * Objetivo: Verificar se é possível buscar todos os empréstimos de um cliente.
     */
    @Test
    @DisplayName("TU-20-CT-01: Deve listar todos os empréstimos cadastrados")
    void deveListarTodosOsEmprestimos() {
        // Given
        EmprestimoEntity emprestimo1 = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(1000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.02))
                .numeroParcelas(12)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        emprestimoService.solicitar(emprestimo1);

        EmprestimoEntity emprestimo2 = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(2000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.03))
                .numeroParcelas(18)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        emprestimoService.solicitar(emprestimo2);

        EmprestimoEntity emprestimo3 = EmprestimoEntity.builder()
                .valorSolicitado(BigDecimal.valueOf(3000.00))
                .taxaJurosMensal(BigDecimal.valueOf(0.025))
                .numeroParcelas(24)
                .cliente(cliente)
                .contaCredito(contaCredito)
                .build();
        emprestimoService.solicitar(emprestimo3);

        // When
        List<EmprestimoEntity> emprestimos = emprestimoService.listarTodos();

        // Then
        assertThat(emprestimos).isNotNull();
        assertThat(emprestimos).hasSize(3);
        assertThat(emprestimos).extracting(EmprestimoEntity::getValorSolicitado)
                .contains(
                        BigDecimal.valueOf(1000.00),
                        BigDecimal.valueOf(2000.00),
                        BigDecimal.valueOf(3000.00)
                );
    }
}
