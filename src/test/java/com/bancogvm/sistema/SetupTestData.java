package com.bancogvm.sistema;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Classe para criar dados de teste via API REST
 * Executa antes dos testes de sistema para popular o banco de dados
 */
@DisplayName("Setup - Criar Dados de Teste via API")
public class SetupTestData {

    private static final String BASE_URL = "http://localhost:8080";
    private static Long clienteId;
    private static Long contaId;

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    @DisplayName("Setup-01: Criar cliente de teste via API")
    void deveCriarClienteViAPI() {
        String timestamp = String.valueOf(System.currentTimeMillis());

        String clienteJson = String.format("""
                {
                    "nome": "Pedro Oliveira Teste",
                    "cpf": "11122233344",
                    "email": "pedro%s@teste.com",
                    "dataNascimento": "1988-03-15",
                    "telefone": "48987654321",
                    "endereco": "Rua C, 789",
                    "loginUsuario": "pedro%s",
                    "senhaHash": "senha123"
                }
                """, timestamp.substring(0, 8), timestamp.substring(0, 4));

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(clienteJson)
        .when()
                .post("/api/clientes")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("nome", equalTo("Pedro Oliveira Teste"))
                .body("id", notNullValue())
        .extract()
                .path("id");

        clienteId = id.longValue();
        System.out.println("✓ Cliente criado via API com ID: " + clienteId);
    }

    @Test
    @DisplayName("Setup-02: Criar conta de teste via API")
    void deveCriarContaViaAPI() {
        // Primeiro garantir que temos um cliente
        if (clienteId == null) {
            deveCriarClienteViAPI();
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        String contaJson = String.format("""
                {
                    "numeroConta": "%s",
                    "agencia": "0001",
                    "saldo": 1000.00,
                    "dataAbertura": "2024-01-01T00:00:00Z",
                    "statusConta": "ATIVA",
                    "limiteChequeEspecial": 2000.00,
                    "titulares": [{"cliente": {"id": %d}}]
                }
                """, timestamp.substring(0, 8), clienteId);

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(contaJson)
        .when()
                .post("/api/contas/corrente")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("numeroConta", notNullValue())
                .body("id", notNullValue())
        .extract()
                .path("id");

        contaId = id.longValue();
        System.out.println("✓ Conta criada via API com ID: " + contaId);
    }

    @Test
    @DisplayName("Setup-03: Verificar dados criados")
    void deveVerificarDadosCriados() {
        System.out.println("=".repeat(60));
        System.out.println("DADOS DE TESTE CRIADOS COM SUCESSO:");
        System.out.println("Cliente ID: " + (clienteId != null ? clienteId : "não criado"));
        System.out.println("Conta ID: " + (contaId != null ? contaId : "não criado"));
        System.out.println("=".repeat(60));
    }
}
