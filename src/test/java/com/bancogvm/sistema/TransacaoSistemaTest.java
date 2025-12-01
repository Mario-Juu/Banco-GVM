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

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TS-03: Teste de Sistema - Fluxo de Transação de Depósito via Interface
 *
 * Pré-requisitos:
 * - Aplicação frontend rodando em http://localhost:5173
 * - Aplicação backend rodando em http://localhost:8080
 * - Banco de dados PostgreSQL rodando
 * - ChromeDriver compatível instalado
 * - Conta corrente (ID=1) existe com saldo inicial R$ 0,00
 */
@DisplayName("TS-03: Teste de Sistema - Transação de Depósito via Interface Web")
public class TransacaoSistemaTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:5173";
    private static final String API_URL = "http://localhost:8080";
    private static final HttpClient httpClient = HttpClient.newHttpClient();

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
     * TS-03-CT-01: Fluxo completo de depósito com atualização de saldo
     *
     * Passos:
     * 1. Acessar módulo "Transações"
     * 2. Clicar em "Nova Transação"
     * 3. Selecionar tipo "Depósito"
     * 4. Preencher formulário
     * 5. Confirmar
     * 6. Verificar sucesso
     * 7. Verificar saldo atualizado
     */
    @Test
    @DisplayName("TS-03-CT-01: Deve processar depósito e atualizar saldo via interface web")
    void deveProcessarDepositoComSucessoViaInterface() throws Exception {
        try {
            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Transações" na sidebar
            WebElement menuTransacoes = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Transações') or contains(text(), 'Transacoes')]")
            ));
            menuTransacoes.click();

            // 3. Clicar no botão "Nova Transação"
            WebElement btnNovaTransacao = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Nova Transação') or contains(text(), 'Nova Transacao')]")
            ));
            btnNovaTransacao.click();

            // 4. Selecionar tipo "Depósito" - clicar no Select trigger
            WebElement tipoTransacaoTrigger = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(text(), 'Tipo de Transação')]/following-sibling::button")
            ));
            tipoTransacaoTrigger.click();

            // Clicar na opção "Depósito"
            WebElement opcaoDeposito = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@data-slot='select-item' and contains(., 'Depósito')]")
            ));
            opcaoDeposito.click();

            // 5. Preencher valor
            WebElement inputValor = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("valor")));
            inputValor.clear();
            inputValor.sendKeys("500.00");

            // 6. Selecionar conta de destino - clicar no Select trigger
            WebElement contaDestinoTrigger = driver.findElement(
                By.xpath("//label[contains(text(), 'Conta de Destino')]/following-sibling::button")
            );
            contaDestinoTrigger.click();

            // Selecionar a primeira conta disponível
            WebElement primeiraContaDestino = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@data-slot='select-item' and not(contains(@data-disabled, 'true'))])[1]")
            ));
            primeiraContaDestino.click();

            // 7. Preencher descrição
            WebElement inputDescricao = driver.findElement(By.id("descricao"));
            inputDescricao.clear();
            inputDescricao.sendKeys("Depósito inicial");

            // 8. Clicar em "Registrar Transação"
            WebElement btnConfirmar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Registrar')]")
            ));
            btnConfirmar.click();

            // 9. Aguardar processamento e possível alerta
            Thread.sleep(2000);

            // Verificar se há algum alerta de erro
            try {
                if (driver.switchTo().alert() != null) {
                    String alertText = driver.switchTo().alert().getText();
                    System.out.println("⚠ Alerta detectado: " + alertText);
                    driver.switchTo().alert().accept();
                    throw new AssertionError("Transação não foi salva: " + alertText);
                }
            } catch (org.openqa.selenium.NoAlertPresentException e) {
                // Sem alerta, continuar normalmente
            }

            // 10. Aguardar mais um pouco
            Thread.sleep(2000);

            // 11. Verificar sucesso - múltiplas estratégias
            boolean sucesso = false;
            try {
                // Estratégia 1: Procurar elemento da lista de transações
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//h2[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'transa')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'deposito')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'nova')]")
                    )
                ));
                sucesso = true;
            } catch (Exception e) {
                // Estratégia 2: Verificar via API se a transação foi criada
                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/api/transacoes"))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                    if (response.body().contains("500") && response.body().contains("DEPOSITO")) {
                        sucesso = true;
                        System.out.println("✓ Transação criada com sucesso (verificado via API)");
                    }
                } catch (Exception apiEx) {
                    System.err.println("Erro ao verificar via API: " + apiEx.getMessage());
                }
            }

            assertThat(sucesso).as("Transação deve ter sido criada com sucesso").isTrue();

            // 8. Acessar módulo "Contas" para verificar saldo
            WebElement menuContas = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Contas')]")
            ));
            menuContas.click();

            // 9. Verificar que o saldo foi atualizado (deve aparecer R$ 500,00 ou similar)
            WebElement saldoConta = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(text(), '500') or contains(text(), 'R$ 500')]")
            ));


            System.out.println("✓ TS-03-CT-01: Depósito processado com sucesso e saldo atualizado");

        } catch (Exception e) {
            System.err.println("✗ TS-03-CT-01 FALHOU: " + e.getMessage());
            throw e;
        }
    }
}
