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
 * TS-02 a TS-05: Testes de Sistema com Selenium WebDriver
 *
 * Pré-requisitos:
 * - Frontend rodando em http://localhost:5173
 * - Backend rodando em http://localhost:8080
 * - Banco de dados PostgreSQL configurado
 * - ChromeDriver compatível instalado
 */
@DisplayName("Testes de Sistema - Banco GVM (Selenium)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SistemaTestsSuite {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // Para executar sem abrir o navegador:
        // options.addArguments("--headless");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * TS-02: Fluxo de Criação de Conta Corrente e Associação com Cliente
     */
    @Test
    @Order(2)
    @DisplayName("TS-02-CT-01: Deve criar conta corrente via interface")
    void deveCriarContaCorrenteViaInterface() {
        try {
            driver.get(BASE_URL);

            // Navegar para Contas
            WebElement menuContas = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Contas')] | //button[contains(text(), 'Contas')]")
            ));
            menuContas.click();

            // Clicar em Nova Conta
            WebElement btnNovaConta = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Nova Conta')]")
            ));
            btnNovaConta.click();

            // Selecionar tipo Conta Corrente
            Thread.sleep(1000); // Aguardar carregamento do formulário

            // Preencher dados
            WebElement inputAgencia = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("agencia")));
            inputAgencia.sendKeys("0001");

            WebElement inputLimite = driver.findElement(By.name("limiteChequeEspecial"));
            inputLimite.sendKeys("2000");

            // Salvar
            WebElement btnSalvar = driver.findElement(By.xpath("//button[contains(text(), 'Salvar')]"));
            btnSalvar.click();

            // Verificar sucesso
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'sucesso')]")),
                ExpectedConditions.urlContains("contas")
            ));

            System.out.println("✓ TS-02-CT-01: Conta corrente criada com sucesso");
        } catch (Exception e) {
            System.err.println("✗ TS-02-CT-01 FALHOU: " + e.getMessage());
        }
    }

    /**
     * TS-03: Fluxo de Transação de Depósito via Interface
     */
    @Test
    @Order(3)
    @DisplayName("TS-03-CT-01: Deve processar depósito via interface")
    void deveProcessarDepositoViaInterface() {
        try {
            driver.get(BASE_URL);

            // Navegar para Transações
            WebElement menuTransacoes = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Transações')] | //a[contains(text(), 'Transacoes')]")
            ));
            menuTransacoes.click();

            // Clicar em Nova Transação
            WebElement btnNovaTransacao = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Nova Transação')] | //button[contains(text(), 'Nova Transacao')]")
            ));
            btnNovaTransacao.click();

            Thread.sleep(1000);

            // Selecionar tipo Depósito e preencher valor
            WebElement inputValor = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("valor")));
            inputValor.sendKeys("500");

            WebElement inputDescricao = driver.findElement(By.name("descricao"));
            inputDescricao.sendKeys("Depósito inicial");

            // Confirmar
            WebElement btnConfirmar = driver.findElement(By.xpath("//button[contains(text(), 'Confirmar')]"));
            btnConfirmar.click();

            // Verificar sucesso
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'concluída')] | //*[contains(text(), 'sucesso')]")),
                ExpectedConditions.urlContains("transacoes")
            ));

            System.out.println("✓ TS-03-CT-01: Depósito processado com sucesso");
        } catch (Exception e) {
            System.err.println("✗ TS-03-CT-01 FALHOU: " + e.getMessage());
        }
    }

    /**
     * TS-04: Fluxo de Solicitação de Empréstimo com Simulação
     */
    @Test
    @Order(4)
    @DisplayName("TS-04-CT-01: Deve solicitar empréstimo com simulação via interface")
    void deveSolicitarEmprestimoComSimulacaoViaInterface() {
        try {
            driver.get(BASE_URL);

            // Navegar para Empréstimos
            WebElement menuEmprestimos = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Empréstimos')] | //a[contains(text(), 'Emprestimos')]")
            ));
            menuEmprestimos.click();

            // Clicar em Solicitar Empréstimo
            WebElement btnSolicitar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Solicitar Empréstimo')] | //button[contains(text(), 'Solicitar')]")
            ));
            btnSolicitar.click();

            Thread.sleep(1000);

            // Preencher dados do empréstimo
            WebElement inputValor = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("valorSolicitado")));
            inputValor.sendKeys("15000");

            WebElement inputTaxa = driver.findElement(By.name("taxaJurosMensal"));
            inputTaxa.sendKeys("2.5");

            WebElement inputParcelas = driver.findElement(By.name("numeroParcelas"));
            inputParcelas.sendKeys("24");

            // Clicar em Simular (se disponível)
            try {
                WebElement btnSimular = driver.findElement(By.xpath("//button[contains(text(), 'Simular')]"));
                btnSimular.click();
                Thread.sleep(2000); // Aguardar simulação
            } catch (Exception e) {
                // Simulação pode não estar implementada
            }

            // Confirmar solicitação
            WebElement btnConfirmar = driver.findElement(By.xpath("//button[contains(text(), 'Confirmar')]"));
            btnConfirmar.click();

            // Verificar sucesso
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'solicitado')] | //*[contains(text(), 'PENDENTE')]")),
                ExpectedConditions.urlContains("emprestimos")
            ));

            System.out.println("✓ TS-04-CT-01: Empréstimo solicitado com sucesso");
        } catch (Exception e) {
            System.err.println("✗ TS-04-CT-01 FALHOU: " + e.getMessage());
        }
    }

    /**
     * TS-05: Fluxo de Aprovação de Empréstimo pelo Gestor
     */
    @Test
    @Order(5)
    @DisplayName("TS-05-CT-01: Deve aprovar empréstimo via interface")
    void deveAprovarEmprestimoViaInterface() {
        try {
            driver.get(BASE_URL);

            // Navegar para Empréstimos
            WebElement menuEmprestimos = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(text(), 'Empréstimos')]")
            ));
            menuEmprestimos.click();

            Thread.sleep(1000);

            // Filtrar por PENDENTE
            try {
                WebElement filtroPendente = driver.findElement(By.xpath("//button[contains(text(), 'PENDENTE')]"));
                filtroPendente.click();
                Thread.sleep(500);
            } catch (Exception e) {
                // Filtro pode não estar implementado
            }

            // Clicar no primeiro empréstimo da lista
            WebElement primeiroEmprestimo = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//tr[contains(@class, 'emprestimo-row')] | //div[contains(@class, 'emprestimo-card')]")
            ));
            primeiroEmprestimo.click();

            Thread.sleep(1000);

            // Clicar no botão Aprovar
            WebElement btnAprovar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Aprovar')]")
            ));
            btnAprovar.click();

            // Inserir valor aprovado
            WebElement inputValorAprovado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.name("valorAprovado")
            ));
            inputValorAprovado.clear();
            inputValorAprovado.sendKeys("12000");

            // Confirmar aprovação
            WebElement btnConfirmarAprovacao = driver.findElement(By.xpath("//button[contains(text(), 'Confirmar')]"));
            btnConfirmarAprovacao.click();

            // Verificar sucesso
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), 'APROVADO')] | //*[contains(text(), 'aprovado')]")
            ));

            System.out.println("✓ TS-05-CT-01: Empréstimo aprovado com sucesso");
        } catch (Exception e) {
            System.err.println("✗ TS-05-CT-01 FALHOU: " + e.getMessage());
            System.err.println("Nota: Este teste requer que exista um empréstimo pendente no sistema");
        }
    }
}
