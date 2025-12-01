package com.bancogvm.integration;

import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.EmprestimoRepository;
import com.bancogvm.repository.TransacaoRepository;
import com.bancogvm.service.model.ClienteEntity;
import com.bancogvm.service.model.ContaCorrenteEntity;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * TI-07, TI-08 e TI-09: Testes de Integração para API de Empréstimos
 * Testam o fluxo completo de gestão de empréstimos via API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - API de Empréstimos")
public class EmprestimoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Long clienteId;
    private Long contaId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        emprestimoRepository.deleteAll();
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();

        // Criar cliente
        ClienteEntity cliente = ClienteEntity.builder()
                .nome("Pedro Oliveira")
                .cpf("98765432109")
                .email("pedro@email.com")
                .dataNascimento(LocalDate.of(1985, 3, 15))
                .telefone("48977777777")
                .endereco("Rua C, 789")
                .loginUsuario("pedro")
                .senhaHash("senha789")
                .dataCadastro(Instant.now())
                .build();
        cliente = clienteRepository.save(cliente);
        clienteId = cliente.getId();

        // Criar conta
        ContaCorrenteEntity conta = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        conta.setNumeroConta("33333-3");
        conta.setAgencia("0001");
        conta.setSaldo(BigDecimal.ZERO);
        conta.setStatusConta("ATIVA");
        conta = (ContaCorrenteEntity) contaRepository.save(conta);
        contaId = conta.getId();
    }

    @AfterEach
    void tearDown() {
        emprestimoRepository.deleteAll();
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    /**
     * TI-07: Solicitação de Empréstimo via API
     * Objetivo: Verificar o fluxo de solicitação de empréstimo através da API.
     */
    @Test
    @DisplayName("TI-07-CT-01: POST /api/emprestimos - Deve solicitar empréstimo")
    void deveSolicitarEmprestimoViaAPI() {
        String emprestimoJson = String.format("""
                {
                    "valorSolicitado": 10000.00,
                    "taxaJurosMensal": 0.025,
                    "numeroParcelas": 36,
                    "clienteId": %d,
                    "contaCreditoId": %d
                }
                """, clienteId, contaId);

        given()
                .contentType(ContentType.JSON)
                .body(emprestimoJson)
        .when()
                .post("/api/emprestimos")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("valorSolicitado", equalTo(10000.00f))
                .body("statusEmprestimo", equalTo("PENDENTE"))
                .body("id", notNullValue())
                .body("dataSolicitacao", notNullValue());
    }

    /**
     * TI-08: Aprovação de Empréstimo via API
     * Objetivo: Verificar o fluxo de aprovação de empréstimo através do endpoint.
     */
    @Test
    @DisplayName("TI-08-CT-01: POST /api/emprestimos/{id}/aprovar - Deve aprovar empréstimo")
    void deveAprovarEmprestimoViaAPI() {
        // Primeiro criar um empréstimo
        String emprestimoJson = String.format("""
                {
                    "valorSolicitado": 15000.00,
                    "taxaJurosMensal": 0.03,
                    "numeroParcelas": 24,
                    "clienteId": %d,
                    "contaCreditoId": %d
                }
                """, clienteId, contaId);

        Integer emprestimoId = given()
                .contentType(ContentType.JSON)
                .body(emprestimoJson)
        .when()
                .post("/api/emprestimos")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .extract()
                .path("id");

        // Aprovar o empréstimo
        String aprovacaoJson = """
                {
                    "valorAprovado": 12000.00
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(aprovacaoJson)
        .when()
                .post("/api/emprestimos/" + emprestimoId + "/aprovar")
        .then()
                .statusCode(anyOf(is(200), is(204)))
                .body("statusEmprestimo", equalTo("APROVADO"))
                .body("valorAprovado", equalTo(12000.00f));
    }

    /**
     * TI-09: Rejeição de Empréstimo via API
     * Objetivo: Verificar o fluxo de rejeição de empréstimo com registro de motivo.
     */
    @Test
    @DisplayName("TI-09-CT-01: POST /api/emprestimos/{id}/rejeitar - Deve rejeitar empréstimo com motivo")
    void deveRejeitarEmprestimoViaAPI() {
        // Primeiro criar um empréstimo
        String emprestimoJson = String.format("""
                {
                    "valorSolicitado": 50000.00,
                    "taxaJurosMensal": 0.04,
                    "numeroParcelas": 48,
                    "clienteId": %d,
                    "contaCreditoId": %d
                }
                """, clienteId, contaId);

        Integer emprestimoId = given()
                .contentType(ContentType.JSON)
                .body(emprestimoJson)
        .when()
                .post("/api/emprestimos")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .extract()
                .path("id");

        // Rejeitar o empréstimo
        String rejeicaoJson = """
                {
                    "motivo": "Renda insuficiente"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(rejeicaoJson)
        .when()
                .post("/api/emprestimos/" + emprestimoId + "/rejeitar")
        .then()
                .statusCode(anyOf(is(200), is(204)))
                .body("statusEmprestimo", equalTo("REJEITADO"))
                .body("motivoRejeicao", equalTo("Renda insuficiente"));
    }
}
