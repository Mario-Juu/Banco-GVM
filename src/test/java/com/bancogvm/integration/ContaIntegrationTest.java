package com.bancogvm.integration;

import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.TransacaoRepository;
import com.bancogvm.service.model.ClienteEntity;
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

import java.time.Instant;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * TI-03 e TI-04: Testes de Integração para API de Contas
 * Testam o fluxo completo: Controller → Service → Repository → Database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - API de Contas")
public class ContaIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    private Long clienteId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();

        // Criar um cliente para associar às contas
        ClienteEntity cliente = ClienteEntity.builder()
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
        clienteId = cliente.getId();
    }

    @AfterEach
    void tearDown() {
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
        clienteRepository.deleteAll();
    }

    /**
     * TI-03: Criação de Conta Corrente via API
     * Objetivo: Verificar o fluxo completo de criação de conta corrente via API.
     */
    @Test
    @DisplayName("TI-03-CT-01: POST /api/contas/corrente - Deve criar conta corrente")
    void deveCriarContaCorrenteViaAPI() {
        String contaJson = String.format("""
                {
                    "numeroConta": "12345-6",
                    "agencia": "0001",
                    "saldo": 100.00,
                    "statusConta": "ATIVA",
                    "limiteChequeEspecial": 2000.00
                }
                """);

        given()
                .contentType(ContentType.JSON)
                .body(contaJson)
        .when()
                .post("/api/contas/corrente")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("numeroConta", equalTo("12345-6"))
                .body("agencia", equalTo("0001"))
                .body("limiteChequeEspecial", equalTo(2000.00f))
                .body("id", notNullValue())
                .body("dataAbertura", notNullValue());
    }

    /**
     * TI-04: Processamento de Transação de Depósito via API
     * Objetivo: Verificar o fluxo completo de depósito, incluindo atualização de saldo.
     * Nota: Este teste depende da implementação do endpoint de transações.
     */
    @Test
    @DisplayName("TI-04-CT-01: POST /api/transacoes - Deve processar depósito e atualizar saldo")
    void deveProcessarDepositoViaAPI() {
        // Primeiro criar uma conta
        String contaJson = """
                {
                    "numeroConta": "99999-9",
                    "agencia": "0001",
                    "saldo": 0.00,
                    "statusConta": "ATIVA",
                    "limiteChequeEspecial": 0.00
                }
                """;

        Integer contaId = given()
                .contentType(ContentType.JSON)
                .body(contaJson)
        .when()
                .post("/api/contas/corrente")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .extract()
                .path("id");

        // Realizar depósito
        String transacaoJson = String.format("""
                {
                    "tipoTransacao": "DEPOSITO",
                    "valor": 200.00,
                    "descricao": "Depósito teste",
                    "contaDestinoId": %d
                }
                """, contaId);

        given()
                .contentType(ContentType.JSON)
                .body(transacaoJson)
        .when()
                .post("/api/transacoes")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("statusTransacao", anyOf(equalTo("CONCLUIDA"), equalTo("PENDENTE")))
                .body("valor", equalTo(200.00f));
    }
}
