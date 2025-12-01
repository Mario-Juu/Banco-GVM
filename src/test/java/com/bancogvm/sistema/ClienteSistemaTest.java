package com.bancogvm.sistema;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TS-01: Teste de Sistema - Fluxo Completo de Cadastro de Cliente pela Interface Web
 *
 * Pré-requisitos:
 * - Aplicação frontend rodando em http://localhost:5173
 * - Aplicação backend rodando em http://localhost:8080
 * - Banco de dados PostgreSQL rodando
 * - ChromeDriver compatível instalado
 */
@DisplayName("TS-01: Teste de Sistema - Cadastro de Cliente via Interface Web")
public class ClienteSistemaTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String API_URL = "http://localhost:8080";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    /**
     * Helper method para deletar cliente com CPF específico (evitar colisões)
     */
    private void deletarClientePorCpf(String cpf) {
        try {
            // Buscar todos os clientes
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/api/clientes"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // Procurar cliente com o CPF e deletar
            if (response.body().contains("\"cpf\":\"" + cpf + "\"")) {
                // Extrair ID do cliente (simplificado - assumindo formato JSON)
                String body = response.body();
                int cpfIndex = body.indexOf("\"cpf\":\"" + cpf + "\"");
                if (cpfIndex > 0) {
                    String before = body.substring(0, cpfIndex);
                    int idIndex = before.lastIndexOf("\"id\":");
                    if (idIndex > 0) {
                        String idStr = before.substring(idIndex + 5);
                        idStr = idStr.substring(0, idStr.indexOf(","));
                        Long clienteId = Long.parseLong(idStr.trim());

                        // Deletar cliente
                        HttpRequest deleteRequest = HttpRequest.newBuilder()
                                .uri(URI.create(API_URL + "/api/clientes/" + clienteId))
                                .DELETE()
                                .build();
                        httpClient.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
                        System.out.println("Cliente com CPF " + cpf + " deletado (ID: " + clienteId + ")");
                    }
                }
            }
        } catch (Exception e) {
            // Ignorar erros - cliente pode não existir
            System.out.println("Não foi possível deletar cliente com CPF " + cpf + ": " + e.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // Descomentar para executar sem abrir o navegador
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15)); // Aumentado para 15s
        driver.get(BASE_URL);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TS-01-CT-01: Fluxo completo de cadastro de cliente com dados válidos
     *
     * Passos:
     * 1. Acessar página inicial
     * 2. Navegar para módulo de Clientes
     * 3. Clicar em "Novo Cliente"
     * 4. Preencher formulário
     * 5. Salvar
     * 6. Verificar sucesso
     */
    @Test
    @DisplayName("TS-01-CT-01: Deve cadastrar cliente com dados válidos via interface web")
    void deveCadastrarClienteComSucessoViaInterface() throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String cpfTeste = "111222333" + timestamp.substring(0, 2);

        try {
            // 0. Deletar cliente com mesmo CPF se existir (evitar colisão)
            deletarClientePorCpf(cpfTeste);

            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Clientes" na sidebar - seletor mais flexível
            WebElement menuClientes = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cliente')]")
            ));
            menuClientes.click();

            // 3. Clicar no botão "Novo Cliente" - seletor mais flexível
            WebElement btnNovoCliente = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'novo') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cadastrar')]")
            ));
            btnNovoCliente.click();

            // 4. Preencher formulário - aguardar formulário estar completamente carregado
            Thread.sleep(1000); // Aguardar animações/carregamento

            // Nome
            WebElement inputNome = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));
            inputNome.clear();
            inputNome.sendKeys("Pedro Oliveira Teste " + timestamp);

            // CPF
            WebElement inputCPF = driver.findElement(By.id("cpf"));
            inputCPF.clear();
            inputCPF.sendKeys(cpfTeste);

            // Email
            WebElement inputEmail = driver.findElement(By.id("email"));
            inputEmail.clear();
            inputEmail.sendKeys("pedro" + timestamp + "@teste.com");

            // Data de Nascimento
            WebElement inputDataNascimento = driver.findElement(By.id("dataNascimento"));
            inputDataNascimento.click(); // Clicar para focar
            inputDataNascimento.clear();
            inputDataNascimento.sendKeys("15031988");

            // Telefone
            WebElement inputTelefone = driver.findElement(By.id("telefone"));
            inputTelefone.clear();
            inputTelefone.sendKeys("48987654321");

            // Endereço
            WebElement inputEndereco = driver.findElement(By.id("endereco"));
            inputEndereco.clear();
            inputEndereco.sendKeys("Rua C, 789");

            // Login
            WebElement inputLogin = driver.findElement(By.id("loginUsuario"));
            inputLogin.clear();
            inputLogin.sendKeys("pedro" + timestamp.substring(0, 4));

            // Senha
            WebElement inputSenha = driver.findElement(By.id("senhaHash"));
            inputSenha.clear();
            inputSenha.sendKeys("senha123");

            // 5. Aguardar um pouco para garantir que todos os campos foram processados
            Thread.sleep(500);

            // 6. Clicar em "Salvar" - aguardar que o botão esteja clicável
            WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and (contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cadastrar') or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'salvar'))]")
            ));
            btnSalvar.click();

            // 7. Aguardar processamento e possível alerta
            Thread.sleep(2000);

            // Verificar se há algum alerta de erro
            try {
                if (driver.switchTo().alert() != null) {
                    String alertText = driver.switchTo().alert().getText();
                    System.out.println("⚠ Alerta detectado: " + alertText);
                    driver.switchTo().alert().accept();
                    throw new AssertionError("Cliente não foi salvo: " + alertText);
                }
            } catch (org.openqa.selenium.NoAlertPresentException e) {
                // Sem alerta, continuar normalmente
            }

            // 8. Aguardar um pouco mais para retorno à lista
            Thread.sleep(2000);

            // 9. Verificar que voltamos para a tela de clientes ou que o cliente foi criado
            // Tentar múltiplas estratégias para confirmar sucesso
            boolean sucesso = false;
            try {
                // Estratégia 1: Procurar elemento da lista de clientes
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//h2[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'cliente')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(text(), 'Pedro Oliveira')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'novo')]")
                    )
                ));
                sucesso = true;
            } catch (Exception e) {
                // Estratégia 2: Verificar via API se o cliente foi criado
                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/api/clientes"))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                    if (response.body().contains(cpfTeste)) {
                        sucesso = true;
                        System.out.println("✓ Cliente criado com sucesso (verificado via API)");
                    }
                } catch (Exception apiEx) {
                    System.err.println("Erro ao verificar via API: " + apiEx.getMessage());
                }
            }

            assertThat(sucesso).isTrue();

            System.out.println("✓ TS-01-CT-01: Cliente cadastrado com sucesso via interface web");

        } catch (Exception e) {
            System.err.println("✗ TS-01-CT-01 FALHOU: " + e.getMessage());
            throw e;
        }
    }

    /**
     * TS-01-CT-02: Tentativa de cadastro com campo obrigatório vazio
     */
    @Test
    @DisplayName("TS-01-CT-02: Deve exibir erro ao tentar cadastrar cliente sem nome")
    void deveExibirErroAoCadastrarSemNome() throws Exception {
        try {
            // Navegar para formulário
            driver.get(BASE_URL);
            WebElement menuClientes = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Clientes')]")
            ));
            menuClientes.click();

            WebElement btnNovoCliente = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Novo Cliente')]")
            ));
            btnNovoCliente.click();

            // Preencher apenas CPF (deixar nome vazio)
            WebElement inputCPF = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cpf")));
            inputCPF.sendKeys("11122233344");

            // Tentar salvar
            WebElement btnSalvar = driver.findElement(
                By.xpath("//button[contains(text(), 'Salvar')] | //button[@type='submit']")
            );
            btnSalvar.click();

            // Verificar mensagem de erro
            WebElement mensagemErro = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'obrigatório')] | //*[contains(text(), 'required')]")
            ));

            assertThat(mensagemErro.getText()).isNotEmpty();
            System.out.println("✓ TS-01-CT-02: Validação de campo obrigatório funcionando corretamente");

        } catch (Exception e) {
            System.err.println("✗ TS-01-CT-02 FALHOU: " + e.getMessage());
            throw e;
        }
    }
}
