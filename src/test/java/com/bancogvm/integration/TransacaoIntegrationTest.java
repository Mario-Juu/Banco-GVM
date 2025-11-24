package com.bancogvm.integration;

import com.bancogvm.repository.ContaRepository;
import com.bancogvm.repository.TransacaoRepository;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * TI-05 e TI-06: Testes de Integração para API de Transações
 * Testam o fluxo completo de transações bancárias via API.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - API de Transações")
public class TransacaoIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    private Long contaOrigemId;
    private Long contaDestinoId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();

        // Criar contas para testes
        ContaCorrenteEntity contaOrigem = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        contaOrigem.setNumeroConta("11111-1");
        contaOrigem.setAgencia("0001");
        contaOrigem.setSaldo(BigDecimal.valueOf(500.00));
        contaOrigem.setStatusConta("ATIVA");
        contaOrigem = (ContaCorrenteEntity) contaRepository.save(contaOrigem);
        contaOrigemId = contaOrigem.getId();

        ContaCorrenteEntity contaDestino = ContaCorrenteEntity.builder()
                .limiteChequeEspecial(BigDecimal.ZERO)
                .build();
        contaDestino.setNumeroConta("22222-2");
        contaDestino.setAgencia("0001");
        contaDestino.setSaldo(BigDecimal.valueOf(100.00));
        contaDestino.setStatusConta("ATIVA");
        contaDestino = (ContaCorrenteEntity) contaRepository.save(contaDestino);
        contaDestinoId = contaDestino.getId();
    }

    @AfterEach
    void tearDown() {
        transacaoRepository.deleteAll();
        contaRepository.deleteAll();
    }

    /**
     * TI-05: Processamento de Transação de Saque com Saldo Insuficiente
     * Objetivo: Verificar se a API rejeita saque quando não há saldo suficiente.
     */
    @Test
    @DisplayName("TI-05-CT-01: POST /api/transacoes - Deve rejeitar saque com saldo insuficiente")
    void deveRejeitarSaqueComSaldoInsuficiente() {
        // Conta tem saldo de 500, tentando sacar 1000
        String transacaoJson = String.format("""
                {
                    "tipoTransacao": "SAQUE",
                    "valor": 1000.00,
                    "descricao": "Saque teste",
                    "contaOrigemId": %d
                }
                """, contaOrigemId);

        given()
                .contentType(ContentType.JSON)
                .body(transacaoJson)
        .when()
                .post("/api/transacoes")
        .then()
                // Pode retornar 400 ou criar com status FALHOU, ambos são válidos
                .statusCode(anyOf(is(200), is(201), is(400)))
                .body(anyOf(
                        hasEntry(equalTo("statusTransacao"), equalTo("FALHOU")),
                        hasKey("message")
                ));
    }

    /**
     * TI-06: Processamento de Transferência entre Contas
     * Objetivo: Verificar o fluxo completo de transferência via API.
     */
    @Test
    @DisplayName("TI-06-CT-01: POST /api/transacoes - Deve processar transferência entre contas")
    void deveProcessarTransferenciaViaAPI() {
        String transacaoJson = String.format("""
                {
                    "tipoTransacao": "TRANSFERENCIA",
                    "valor": 150.00,
                    "descricao": "Transferência teste",
                    "contaOrigemId": %d,
                    "contaDestinoId": %d
                }
                """, contaOrigemId, contaDestinoId);

        given()
                .contentType(ContentType.JSON)
                .body(transacaoJson)
        .when()
                .post("/api/transacoes")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("tipoTransacao", equalTo("TRANSFERENCIA"))
                .body("valor", equalTo(150.00f))
                .body("statusTransacao", notNullValue());
    }

    /**
     * TI-07: Solicitação de Empréstimo via API (movido para EmprestimoIntegrationTest)
     */

    /**
     * TI-10: Extrato de Transações por Conta via API
     * Objetivo: Verificar se o endpoint de extrato retorna todas as transações da conta.
     */
    @Test
    @DisplayName("TI-10-CT-01: GET /api/transacoes/extrato/{contaId} - Deve retornar extrato da conta")
    void deveRetornarExtratoDaContaViaAPI() {
        // Criar algumas transações primeiro
        String transacao1Json = String.format("""
                {
                    "tipoTransacao": "DEPOSITO",
                    "valor": 100.00,
                    "descricao": "Depósito 1",
                    "contaOrigemId": %d
                }
                """, contaOrigemId);

        given()
                .contentType(ContentType.JSON)
                .body(transacao1Json)
        .when()
                .post("/api/transacoes");

        String transacao2Json = String.format("""
                {
                    "tipoTransacao": "SAQUE",
                    "valor": 50.00,
                    "descricao": "Saque 1",
                    "contaOrigemId": %d
                }
                """, contaOrigemId);

        given()
                .contentType(ContentType.JSON)
                .body(transacao2Json)
        .when()
                .post("/api/transacoes");

        // Buscar extrato
        given()
        .when()
                .get("/api/transacoes/extrato/" + contaOrigemId)
        .then()
                .statusCode(200)
                .body("$", not(empty()));
    }
}
