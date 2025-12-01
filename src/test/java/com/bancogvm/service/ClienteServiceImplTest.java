package com.bancogvm.service;

import com.bancogvm.repository.ClienteRepository;
import com.bancogvm.service.model.ClienteEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * TU-01 a TU-05: Testes Unitários para ClienteServiceImpl
 * Estes testes NÃO utilizam mocks, mas sim o banco H2 em memória
 * para testar o comportamento real da integração com o banco de dados.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes Unitários - ClienteServiceImpl (SEM MOCKS)")
public class ClienteServiceImplTest {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteRepository clienteRepository;

    private ClienteEntity clienteValido;

    @BeforeEach
    void setUp() {
        // Limpar base antes de cada teste
        clienteRepository.deleteAll();

        // Preparar cliente válido para testes
        clienteValido = ClienteEntity.builder()
                .nome("João Silva")
                .cpf("12345678901")
                .email("joao@email.com")
                .dataNascimento(LocalDate.of(1990, 1, 1))
                .telefone("48999999999")
                .endereco("Rua A, 123")
                .loginUsuario("joao")
                .senhaHash("senha123")
                .build();
    }

    @AfterEach
    void tearDown() {
        clienteRepository.deleteAll();
    }

    /**
     * TU-01: Criação de Cliente Válido
     * Objetivo: Verificar se o método cadastrar() cria um cliente corretamente quando todos os dados são válidos.
     */
    @Test
    @DisplayName("TU-01-CT-01: Deve criar cliente com dados válidos e gerar ID e dataCadastro")
    void deveCriarClienteValido() {
        // When
        ClienteEntity clienteSalvo = clienteService.cadastrar(clienteValido);

        // Then
        assertThat(clienteSalvo).isNotNull();
        assertThat(clienteSalvo.getId()).isNotNull();
        assertThat(clienteSalvo.getDataCadastro()).isNotNull();
        assertThat(clienteSalvo.getNome()).isEqualTo("João Silva");
        assertThat(clienteSalvo.getCpf()).isEqualTo("12345678901");
        assertThat(clienteSalvo.getEmail()).isEqualTo("joao@email.com");

        // Verificar persistência
        ClienteEntity clienteBuscado = clienteRepository.findById(clienteSalvo.getId()).orElse(null);
        assertThat(clienteBuscado).isNotNull();
        assertThat(clienteBuscado.getNome()).isEqualTo("João Silva");
    }

    /**
     * TU-02: Validação de CPF Duplicado
     * Objetivo: Verificar se o sistema impede cadastro de CPF duplicado através de constraint do banco.
     */
    @Test
    @DisplayName("TU-02-CT-01: Deve lançar exceção ao tentar cadastrar CPF já existente")
    void deveLancarExcecaoAoCadastrarCPFDuplicado() {
        // Given
        clienteService.cadastrar(clienteValido);

        // When
        ClienteEntity clienteDuplicado = ClienteEntity.builder()
                .nome("Maria Silva")
                .cpf("12345678901") // Mesmo CPF
                .email("maria@email.com")
                .dataNascimento(LocalDate.of(1992, 5, 5))
                .telefone("48988888888")
                .endereco("Rua B, 456")
                .loginUsuario("maria")
                .senhaHash("senha456")
                .build();

        // Then
        assertThatThrownBy(() -> {
            clienteService.cadastrar(clienteDuplicado);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("CPF já cadastrado");
    }

    /**
     * TU-03: Busca de Cliente por ID Existente
     * Objetivo: Verificar se o método buscarPorId() retorna o cliente correto quando o ID existe.
     */
    @Test
    @DisplayName("TU-03-CT-01: Deve buscar cliente existente por ID")
    void deveBuscarClientePorIdExistente() {
        // Given
        ClienteEntity clienteSalvo = clienteService.cadastrar(clienteValido);
        Long idSalvo = clienteSalvo.getId();

        // When
        ClienteEntity clienteEncontrado = clienteService.buscarPorId(idSalvo);

        // Then
        assertThat(clienteEncontrado).isNotNull();
        assertThat(clienteEncontrado.getId()).isEqualTo(idSalvo);
        assertThat(clienteEncontrado.getNome()).isEqualTo("João Silva");
        assertThat(clienteEncontrado.getCpf()).isEqualTo("12345678901");
    }

    /**
     * TU-04: Busca de Cliente por ID Inexistente
     * Objetivo: Verificar se o método buscarPorId() lança exceção quando o ID não existe.
     */
    @Test
    @DisplayName("TU-04-CT-01: Deve lançar exceção ao buscar cliente inexistente")
    void deveLancarExcecaoAoBuscarClienteInexistente() {
        // When & Then
        assertThatThrownBy(() -> clienteService.buscarPorId(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Cliente não encontrado");
    }

    /**
     * TU-05: Listagem de Todos os Clientes
     * Objetivo: Verificar se o método listarTodos() retorna todos os clientes cadastrados.
     */
    @Test
    @DisplayName("TU-05-CT-01: Deve listar todos os clientes cadastrados")
    void deveListarTodosOsClientes() {
        // Given
        ClienteEntity cliente1 = clienteService.cadastrar(clienteValido);

        ClienteEntity cliente2 = ClienteEntity.builder()
                .nome("Pedro Oliveira")
                .cpf("98765432109")
                .email("pedro@email.com")
                .dataNascimento(LocalDate.of(1985, 3, 15))
                .telefone("48977777777")
                .endereco("Rua C, 789")
                .loginUsuario("pedro")
                .senhaHash("senha789")
                .build();
        clienteService.cadastrar(cliente2);

        // When
        List<ClienteEntity> clientes = clienteService.listarTodos();

        // Then
        assertThat(clientes).isNotNull();
        assertThat(clientes).hasSize(2);
        assertThat(clientes).extracting(ClienteEntity::getNome)
                .containsExactlyInAnyOrder("João Silva", "Pedro Oliveira");
    }
}
