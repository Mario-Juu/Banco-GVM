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

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // Descomentar para executar sem abrir o navegador
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
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
        try {
            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Clientes" na sidebar
            WebElement menuClientes = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Clientes')]")
            ));
            menuClientes.click();

            // 3. Clicar no botão "Novo Cliente"
            WebElement btnNovoCliente = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Novo Cliente')]")
            ));
            btnNovoCliente.click();

            // 4. Preencher formulário - aguardar formulário estar completamente carregado
            Thread.sleep(1000); // Aguardar animações/carregamento
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Nome
            WebElement inputNome = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("nome")));
            inputNome.clear();
            inputNome.sendKeys("Pedro Oliveira Teste " + timestamp);

            // CPF
            WebElement inputCPF = driver.findElement(By.id("cpf"));
            inputCPF.clear();
            inputCPF.sendKeys("111222333" + timestamp.substring(0, 2));

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
                By.xpath("//button[@type='submit' and (contains(text(), 'Cadastrar') or contains(text(), 'Salvar'))]")
            ));
            btnSalvar.click();

            // 7. Aguardar processamento e retorno à lista
            Thread.sleep(3000); // Aguardar API processar e retornar à lista

            // 8. Verificar que voltamos para a tela de clientes (procurar por elementos da lista)
            WebElement listaClientes = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h2[contains(text(), 'Clientes')] | //*[contains(text(), 'cliente')]")
            ));

            // 9. Verificar se o cliente criado aparece na lista (procurar pelo nome)
            WebElement clienteCriado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'Pedro Oliveira Teste')]")
            ));
            assertThat(clienteCriado.getText()).contains("Pedro Oliveira");

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
