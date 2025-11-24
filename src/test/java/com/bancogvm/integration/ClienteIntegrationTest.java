package com.bancogvm.integration;

import com.bancogvm.repository.ClienteRepository;
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
 * TI-01 e TI-02: Testes de Integração para API de Clientes
 * Testam o fluxo completo: Controller → Service → Repository → Database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Testes de Integração - API de Clientes")
public class ClienteIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ClienteRepository clienteRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        clienteRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    /**
     * TI-01: Criação de Cliente Válido via API
     * Objetivo: Verificar o fluxo completo de criação de cliente via API REST.
     */
    @Test
    @DisplayName("TI-01-CT-01: POST /api/clientes - Deve criar cliente com dados válidos")
    void deveCriarClienteValidoViaAPI() {
        String clienteJson = """
                {
                    "nome": "Maria Teste",
                    "cpf": "99988877766",
                    "email": "maria@teste.com",
                    "dataNascimento": "1995-05-05",
                    "telefone": "48999999999",
                    "endereco": "Rua B, 456",
                    "loginUsuario": "maria",
                    "senhaHash": "senha123"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(clienteJson)
        .when()
                .post("/api/clientes")
        .then()
                .statusCode(anyOf(is(200), is(201)))
                .body("nome", equalTo("Maria Teste"))
                .body("cpf", equalTo("99988877766"))
                .body("email", equalTo("maria@teste.com"))
                .body("id", notNullValue())
                .body("dataCadastro", notNullValue());
    }

    /**
     * TI-02: Criação de Cliente com CPF Duplicado via API
     * Objetivo: Verificar se a API rejeita a criação de cliente com CPF já cadastrado.
     */
    @Test
    @DisplayName("TI-02-CT-01: POST /api/clientes - Deve rejeitar CPF duplicado")
    void deveRejeitarCPFDuplicadoViaAPI() {
        // Primeiro, criar um cliente
        ClienteEntity clienteExistente = ClienteEntity.builder()
                .nome("João Existente")
                .cpf("99988877766")
                .email("joao@teste.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .telefone("48988888888")
                .endereco("Rua A, 123")
                .loginUsuario("joao")
                .senhaHash("senha")
                .dataCadastro(Instant.now())
                .build();
        clienteRepository.save(clienteExistente);

        // Tentar criar outro com o mesmo CPF
        String clienteDuplicadoJson = """
                {
                    "nome": "Outra Maria",
                    "cpf": "99988877766",
                    "email": "outra@teste.com",
                    "dataNascimento": "1992-03-10",
                    "telefone": "48977777777",
                    "endereco": "Rua C, 789",
                    "loginUsuario": "outra",
                    "senhaHash": "senha456"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(clienteDuplicadoJson)
        .when()
                .post("/api/clientes")
        .then()
                .statusCode(anyOf(is(400), is(409), is(500))); // Aceitar vários códigos de erro possíveis
    }
}
