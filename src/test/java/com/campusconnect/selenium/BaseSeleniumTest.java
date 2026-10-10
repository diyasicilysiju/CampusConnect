package com.campusconnect.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl = "http://localhost:8080";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Support running in headless mode (default for CI/automated testing)
        // Can be toggled with -Dheadless=false
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        if (headless) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(4));
        wait = new WebDriverWait(driver, Duration.ofSeconds(6));

        // Reset database to ensure clean test state
        resetDatabase();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void resetDatabase() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/data/reset"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            client.send(request, HttpResponse.BodyHandlers.ofString());
            Thread.sleep(500);
        } catch (Exception e) {
            System.err.println("Could not reset test database: " + e.getMessage());
        }
    }

    protected void loginAs(String email, String password) {
        driver.get(baseUrl + "/login.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        WebElement emailField = driver.findElement(By.id("email"));
        WebElement passField = driver.findElement(By.id("password"));
        WebElement loginBtn = driver.findElement(By.id("loginButton"));

        emailField.clear();
        emailField.sendKeys(email);
        passField.clear();
        passField.sendKeys(password);
        loginBtn.click();

        // Wait for redirect or success
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("dashboard"),
                ExpectedConditions.urlContains("events")
        ));
    }

    protected void logout() {
        try {
            WebElement logoutBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("logoutButton")));
            logoutBtn.click();
            wait.until(ExpectedConditions.urlContains("login.html"));
        } catch (Exception e) {
            driver.get(baseUrl + "/login.html");
        }
    }
}
