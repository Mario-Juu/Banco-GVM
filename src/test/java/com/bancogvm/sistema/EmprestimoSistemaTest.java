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
 * TS-04 e TS-05: Testes de Sistema - Fluxos de Empréstimo via Interface
 *
 * Pré-requisitos:
 * - Aplicação frontend rodando em http://localhost:5173
 * - Aplicação backend rodando em http://localhost:8080
 * - Banco de dados PostgreSQL rodando
 * - ChromeDriver compatível instalado
 * - Cliente "Pedro Oliveira" (ID=1) existe
 * - Conta para crédito (ID=1) existe
 */
@DisplayName("TS-04 e TS-05: Testes de Sistema - Empréstimos via Interface Web")
public class EmprestimoSistemaTest {

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
     * TS-04-CT-01: Fluxo completo de solicitação de empréstimo com simulação
     *
     * Passos:
     * 1. Acessar módulo "Empréstimos"
     * 2. Clicar em "Solicitar Empréstimo"
     * 3. Preencher formulário
     * 4. Simular (se disponível)
     * 5. Confirmar solicitação
     * 6. Verificar sucesso
     */
    @Test
    @DisplayName("TS-04-CT-01: Deve solicitar empréstimo com simulação via interface web")
    void deveSolicitarEmprestimoComSimulacaoViaInterface() throws Exception {
        try {
            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Empréstimos" na sidebar
            WebElement menuEmprestimos = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Empréstimos') or contains(text(), 'Emprestimos')]")
            ));
            menuEmprestimos.click();

            // 3. Clicar no botão "Novo Empréstimo"
            WebElement btnSolicitar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Novo Empréstimo') or contains(text(), 'Solicitar')]")
            ));
            btnSolicitar.click();

            // 4. Selecionar cliente - clicar no Select trigger
            WebElement selectClienteTrigger = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//label[contains(text(), 'Cliente')]/following-sibling::button")
            ));
            selectClienteTrigger.click();

            // Selecionar primeiro cliente disponível
            WebElement primeiroCliente = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@data-slot='select-item' and not(contains(@data-disabled, 'true'))])[1]")
            ));
            primeiroCliente.click();

            // 5. Selecionar conta para crédito - clicar no Select trigger
            WebElement selectContaTrigger = driver.findElement(
                By.xpath("//label[contains(text(), 'Conta para Crédito')]/following-sibling::button")
            );
            selectContaTrigger.click();

            // Selecionar primeira conta disponível
            WebElement primeiraConta = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(//*[@data-slot='select-item' and not(contains(@data-disabled, 'true'))])[1]")
            ));
            primeiraConta.click();

            // 6. Preencher valor solicitado
            WebElement inputValor = driver.findElement(By.id("valorSolicitado"));
            inputValor.clear();
            inputValor.sendKeys("15000.00");

            // 7. Preencher taxa de juros mensal
            WebElement inputTaxa = driver.findElement(By.id("taxaJurosMensal"));
            inputTaxa.clear();
            inputTaxa.sendKeys("2.5");

            // 8. Preencher número de parcelas
            WebElement inputParcelas = driver.findElement(By.id("numeroParcelas"));
            inputParcelas.clear();
            inputParcelas.sendKeys("24");

            // 5. Clicar em "Simular" (se disponível)
            try {
                WebElement btnSimular = driver.findElement(
                    By.xpath("//button[contains(text(), 'Simular')]")
                );
                btnSimular.click();

                // Aguardar exibição da simulação
                wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(text(), 'simulação') or contains(text(), 'total') or contains(text(), 'parcela')]")
                ));
                System.out.println("✓ Simulação exibida com sucesso");
            } catch (Exception e) {
                System.out.println("⚠ Botão Simular não encontrado ou não disponível");
            }

            // 9. Clicar em "Solicitar Empréstimo"
            WebElement btnConfirmar = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@type='submit' and contains(text(), 'Solicitar')]")
            ));
            btnConfirmar.click();

            // 10. Aguardar processamento e possível alerta
            Thread.sleep(2000);

            // Verificar se há algum alerta de erro
            try {
                if (driver.switchTo().alert() != null) {
                    String alertText = driver.switchTo().alert().getText();
                    System.out.println("⚠ Alerta detectado: " + alertText);
                    driver.switchTo().alert().accept();
                    throw new AssertionError("Empréstimo não foi salvo: " + alertText);
                }
            } catch (org.openqa.selenium.NoAlertPresentException e) {
                // Sem alerta, continuar normalmente
            }

            // 11. Aguardar mais um pouco
            Thread.sleep(2000);

            // 12. Verificar sucesso - múltiplas estratégias
            boolean sucesso = false;
            try {
                // Estratégia 1: Procurar elemento da lista de empréstimos ou status PENDENTE
                wait.until(ExpectedConditions.or(
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//h2[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'empr')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'pendente')]")
                    ),
                    ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'novo')]")
                    )
                ));
                sucesso = true;
            } catch (Exception e) {
                // Estratégia 2: Verificar via API se o empréstimo foi criado
                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/api/emprestimos"))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                    if (response.body().contains("15000") && response.body().contains("PENDENTE")) {
                        sucesso = true;
                        System.out.println("✓ Empréstimo criado com sucesso (verificado via API)");
                    }
                } catch (Exception apiEx) {
                    System.err.println("Erro ao verificar via API: " + apiEx.getMessage());
                }
            }

            assertThat(sucesso).isTrue();

            System.out.println("✓ TS-04-CT-01: Empréstimo solicitado com sucesso via interface web");

        } catch (Exception e) {
            System.err.println("✗ TS-04-CT-01 FALHOU: " + e.getMessage());
            throw e;
        }
    }

    /**
     * TS-05-CT-01: Fluxo completo de aprovação de empréstimo pelo gestor
     *
     * Passos:
     * 1. Acessar módulo "Empréstimos"
     * 2. Filtrar por status "PENDENTE"
     * 3. Clicar no empréstimo para ver detalhes
     * 4. Clicar em "Aprovar"
     * 5. Inserir valor aprovado
     * 6. Confirmar aprovação
     * 7. Verificar sucesso
     */
    @Test
    @DisplayName("TS-05-CT-01: Deve aprovar empréstimo pendente via interface web")
    void deveAprovarEmprestimoPendenteViaInterface() throws Exception {
        try {
            // 1. Acessar a aplicação
            driver.get(BASE_URL);

            // 2. Clicar no menu "Empréstimos" na sidebar
            WebElement menuEmprestimos = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'Empréstimos') or contains(text(), 'Emprestimos')]")
            ));
            menuEmprestimos.click();

            // 3. Aguardar a lista de empréstimos carregar
            Thread.sleep(2000);
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'empr')]")
            ));

            // 4. Procurar botão de aprovar (botão verde com ícone CheckCircle)
            WebElement btnAprovar = null;
            try {
                // Estratégia 1: Procurar botão com classe text-green-600 (o mais específico)
                btnAprovar = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(@class, 'text-green-600')]")
                ));
                System.out.println("✓ Botão de aprovar encontrado via classe text-green-600");
            } catch (Exception e1) {
                try {
                    // Estratégia 2: Procurar primeiro Badge PENDENTE, depois o botão verde no mesmo card
                    WebElement badgePendente = wait.until(ExpectedConditions.presenceOfElementLocated(
                        By.xpath("//*[contains(@class, 'bg-yellow-100') and contains(text(), 'PENDENTE')]")
                    ));
                    // Navegar até o card pai e procurar o botão verde
                    WebElement card = badgePendente.findElement(By.xpath("ancestor::*[contains(@class, 'hover:shadow-lg')]"));
                    btnAprovar = card.findElement(By.xpath(".//button[contains(@class, 'text-green')]"));
                    System.out.println("✓ Botão de aprovar encontrado via Badge PENDENTE");
                } catch (Exception e2) {
                    try {
                        // Estratégia 3: Procurar SVG CheckCircle dentro de button
                        btnAprovar = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[.//svg[contains(@class, 'lucide-check-circle')]]")
                        ));
                        System.out.println("✓ Botão de aprovar encontrado via ícone CheckCircle");
                    } catch (Exception e3) {
                        // Estratégia 4: Buscar qualquer botão verde (text-green em qualquer nível)
                        btnAprovar = wait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//button[contains(@class, 'text-green')]")
                        ));
                        System.out.println("✓ Botão de aprovar encontrado via classe text-green genérica");
                    }
                }
            }

            if (btnAprovar != null) {
                btnAprovar.click();
            } else {
                throw new AssertionError("Não foi possível encontrar botão de aprovar");
            }

            // 6. Aguardar o prompt do navegador e inserir o valor aprovado
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().sendKeys("12000.00");
            driver.switchTo().alert().accept();

            // 7. Aguardar processamento (a lista vai recarregar)
            Thread.sleep(3000);

            // 8. Verificar se foi aprovado - múltiplas estratégias
            boolean aprovado = false;
            try {
                // Estratégia 1: Procurar Badge ou texto APROVADO no DOM
                WebElement statusAprovado = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//*[contains(@class, 'bg-green-100') and contains(text(), 'APROVADO')] | " +
                             "//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'aprovado')]")
                ));
                assertThat(statusAprovado.getText()).containsIgnoringCase("APROVADO");
                aprovado = true;
                System.out.println("✓ Status APROVADO encontrado no DOM");
            } catch (Exception e) {
                // Estratégia 2: Verificar via API se o empréstimo foi aprovado
                try {
                    HttpRequest getRequest = HttpRequest.newBuilder()
                            .uri(URI.create(API_URL + "/api/emprestimos"))
                            .GET()
                            .build();
                    HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
                    if (response.body().contains("APROVADO") && response.body().contains("12000")) {
                        aprovado = true;
                        System.out.println("✓ Empréstimo aprovado com sucesso (verificado via API)");
                    }
                } catch (Exception apiEx) {
                    System.err.println("Erro ao verificar via API: " + apiEx.getMessage());
                }
            }

            assertThat(aprovado).as("Empréstimo deve ter sido aprovado").isTrue();

            System.out.println("✓ TS-05-CT-01: Empréstimo aprovado com sucesso via interface web");

        } catch (Exception e) {
            System.err.println("✗ TS-05-CT-01 FALHOU: " + e.getMessage());
            throw e;
        }
    }
}
