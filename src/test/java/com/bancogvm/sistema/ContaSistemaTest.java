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
 * TS-02: Teste de Sistema - Fluxo de Criação de Conta Corrente e Associação com Cliente
 *
 * Pré-requisitos:
 * - Aplicação frontend rodando em http://localhost:5173
 * - Aplicação backend rodando em http://localhost:8080
 * - Banco de dados PostgreSQL rodando
 * - ChromeDriver compatível instalado
 * - Cliente "Pedro Oliveira" (ID=1) já cadastrado
 */
@DisplayName("TS-02: Teste de Sistema - Criação de Conta Corrente via Interface Web")
public class ContaSistemaTest {

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
     * TS-02-CT-01: Fluxo completo de criação de conta corrente
     *
     * Passos:
     * 1. Acessar módulo "Contas" via sidebar
     * 2. Clicar em "Nova Conta"
     * 3. Selecionar tipo "Conta Corrente"
     * 4. Preencher formulário
     * 5. Salvar
     * 6. Verificar sucesso
     */
    @Test
    @DisplayName("TS-02-CT-01: Deve criar conta corrente com dados válidos via interface web")
    void deveCriarContaCorrenteComSucessoViaInterface() throws Exception {
        try {
            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Contas" na sidebar
            WebElement menuContas = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Contas')]")
            ));
            menuContas.click();

            // 3. Clicar no botão "Nova Conta"
            WebElement btnNovaConta = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Nova Conta')]")
            ));
            btnNovaConta.click();

            // 4. Preencher número da conta
            WebElement inputNumeroConta = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.id("numeroConta")
            ));
            inputNumeroConta.clear();
            inputNumeroConta.sendKeys(String.valueOf(System.currentTimeMillis()).substring(0, 8));

            // 5. Preencher agência
            WebElement inputAgencia = driver.findElement(By.id("agencia"));
            inputAgencia.clear();
            inputAgencia.sendKeys("0001");

            // 6. Preencher saldo inicial
            WebElement inputSaldo = driver.findElement(By.id("saldo"));
            inputSaldo.clear();
            inputSaldo.sendKeys("0.00");

            // 7. Selecionar tipo "Conta Corrente" (já é o padrão, mas vamos garantir)
            // O tipo CORRENTE já vem selecionado por padrão no formulário

            // 8. Preencher limite de cheque especial
            WebElement inputLimite = driver.findElement(By.id("limiteChequeEspecial"));
            inputLimite.clear();
            inputLimite.sendKeys("2000.00");

            // 9. Selecionar cliente titular - clicar no Select trigger
            WebElement selectTitularTrigger = driver.findElement(
                By.xpath("//label[contains(text(), 'Cliente Titular')]/following-sibling::button")
            );
            selectTitularTrigger.click();

            // 10. Aguardar o dropdown abrir e clicar na primeira opção disponível
            WebElement primeiraOpcao = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@data-slot='select-item' and not(contains(@data-disabled, 'true'))])[1]")
            ));
            primeiraOpcao.click();

            // 11. Clicar em "Salvar"
            WebElement btnSalvar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Cadastrar')]")
            ));
            btnSalvar.click();

            // 12. Aguardar processamento e retorno à lista
            Thread.sleep(3000);

            // 13. Verificar que voltamos para a tela de contas
            WebElement listaContas = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//h2[contains(text(), 'Contas')] | //*[contains(text(), 'conta')]")
            ));

            // 14. Verificar que a nova conta aparece na lista (procurar por elementos de conta)
            WebElement contaCriada = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'Agência') or contains(text(), 'Ag�ncia') or contains(text(), '0001')]")
            ));
            assertThat(contaCriada.isDisplayed()).isTrue();

            System.out.println("✓ TS-02-CT-01: Conta corrente criada com sucesso via interface web");

        } catch (Exception e) {
            System.err.println("✗ TS-02-CT-01 FALHOU: " + e.getMessage());
            throw e;
        }
    }
}
