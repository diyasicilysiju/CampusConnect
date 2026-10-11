
package com.campusconnect.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected final String baseUrl =
            System.getProperty("baseUrl", "http://localhost:8080");

    private HttpClient httpClient;

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();

        // Chrome opens visibly by default.
        // Headless mode can still be enabled using -Dheadless=true.
        if (Boolean.parseBoolean(
                System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1440,1000");

        driver = new ChromeDriver(options);

        // Use explicit waits instead of mixing implicit and explicit waits.
        driver.manage().timeouts()
                .implicitlyWait(Duration.ZERO);

        wait = new WebDriverWait(
                driver, Duration.ofSeconds(10));

        httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    @BeforeMethod(alwaysRun = true)
    public void resetStateBeforeEachTest() {
        resetDatabase();

        // Clear cookies and browser storage before each test.
        driver.manage().deleteAllCookies();
        driver.get(baseUrl + "/login.html");

        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear();" +
                "window.sessionStorage.clear();"
        );

        driver.get("about:blank");
    }

    protected void resetDatabase() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/data/reset"))
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() < 200
                    || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Database reset failed. HTTP status: "
                                + response.statusCode()
                                + ". Response: " + response.body()
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Could not reset the test database. "
                            + "Check that CampusConnect is running "
                            + "and /api/data/reset is available.",
                    e
            );
        }
    }

    protected void loginAs(String email, String password) {
        driver.get(baseUrl + "/login.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                org.openqa.selenium.By.id("email")));

        driver.findElement(
                org.openqa.selenium.By.id("email")).clear();
        driver.findElement(
                org.openqa.selenium.By.id("email")).sendKeys(email);

        driver.findElement(
                org.openqa.selenium.By.id("password")).clear();
        driver.findElement(
                org.openqa.selenium.By.id("password")).sendKeys(password);

        driver.findElement(
                org.openqa.selenium.By.id("loginButton")).click();

        wait.until(d ->
                d.getCurrentUrl().contains("dashboard")
                        || d.getCurrentUrl().contains("events")
        );
    }

    protected void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(
                org.openqa.selenium.By.id("logoutButton")
        )).click();

        wait.until(ExpectedConditions.urlContains("login.html"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}