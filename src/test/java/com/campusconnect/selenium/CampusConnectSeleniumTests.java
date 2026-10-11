package com.campusconnect.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CampusConnectSeleniumTests extends BaseSeleniumTest {

    // -------------------------------------------------------------
    // Scenario 1: Student registration with valid data
    // -------------------------------------------------------------
    @Test(priority = 1)
    public void test01_StudentRegistration_ValidData() {
        driver.get(baseUrl + "/register.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));

        driver.findElement(By.id("fullName")).sendKeys("Alice Walker");
        driver.findElement(By.id("studentId")).sendKeys("STU-2026-999");
        driver.findElement(By.id("email")).sendKeys("alice.walker@campusconnect.edu");
        driver.findElement(By.id("password")).sendKeys("AlicePass@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("AlicePass@123");

        driver.findElement(By.id("registerButton")).click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("successful"), "Expected registration success message");
    }

    // -------------------------------------------------------------
    // Scenario 2: Registration with empty required fields
    // -------------------------------------------------------------
    @Test(priority = 2)
    public void test02_StudentRegistration_EmptyRequiredFields() {
        driver.get(baseUrl + "/register.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));

        driver.findElement(By.id("registerButton")).click();

        WebElement validation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationMessage")));
        Assert.assertTrue(validation.getText().contains("required"), "Expected required field validation message");
    }

    // -------------------------------------------------------------
    // Scenario 3: Registration with an invalid email
    // -------------------------------------------------------------
    @Test(priority = 3)
    public void test03_StudentRegistration_InvalidEmail() {
        driver.get(baseUrl + "/register.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));

        driver.findElement(By.id("fullName")).sendKeys("Bad Email Student");
        driver.findElement(By.id("studentId")).sendKeys("STU-BAD-001");
        driver.findElement(By.id("email")).sendKeys("invalid-email-format");
        driver.findElement(By.id("password")).sendKeys("Secret@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Secret@123");

        driver.findElement(By.id("registerButton")).click();

        WebElement validation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationMessage")));
        Assert.assertTrue(validation.getText().toLowerCase().contains("valid email"), "Expected invalid email message");
    }

    // -------------------------------------------------------------
    // Scenario 4: Registration with mismatched passwords
    // -------------------------------------------------------------
    @Test(priority = 4)
    public void test04_StudentRegistration_MismatchedPasswords() {
        driver.get(baseUrl + "/register.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));

        driver.findElement(By.id("fullName")).sendKeys("Mismatch Tester");
        driver.findElement(By.id("studentId")).sendKeys("STU-MIS-002");
        driver.findElement(By.id("email")).sendKeys("mismatch@campusconnect.edu");
        driver.findElement(By.id("password")).sendKeys("Password123");
        driver.findElement(By.id("confirmPassword")).sendKeys("DifferentPassword");

        driver.findElement(By.id("registerButton")).click();

        WebElement validation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationMessage")));
        Assert.assertTrue(validation.getText().toLowerCase().contains("match"), "Expected password mismatch message");
    }

    // -------------------------------------------------------------
    // Scenario 5: Registration with an existing email
    // -------------------------------------------------------------
    @Test(priority = 5)
    public void test05_StudentRegistration_ExistingEmail() {
        driver.get(baseUrl + "/register.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fullName")));

        // Attempt registering with seed student email
        driver.findElement(By.id("fullName")).sendKeys("Duplicate Person");
        driver.findElement(By.id("studentId")).sendKeys("STU-NEW-009");
        driver.findElement(By.id("email")).sendKeys("john.doe@campusconnect.edu");
        driver.findElement(By.id("password")).sendKeys("Secret@123");
        driver.findElement(By.id("confirmPassword")).sendKeys("Secret@123");

        driver.findElement(By.id("registerButton")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        Assert.assertTrue(error.getText().toLowerCase().contains("already exists"), "Expected duplicate email error");
    }

    // -------------------------------------------------------------
    // Scenario 6: Login with valid credentials
    // -------------------------------------------------------------
    @Test(priority = 6)
    public void test06_Login_ValidCredentials() {
        loginAs("john.doe@campusconnect.edu", "Student@123");
        WebElement userBadge = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userBadge")));
        Assert.assertTrue(userBadge.getText().contains("John Doe"), "Expected John Doe in user badge");
    }

    // -------------------------------------------------------------
    // Scenario 7: Login with an incorrect password
    // -------------------------------------------------------------
    @Test(priority = 7)
    public void test07_Login_IncorrectPassword() {
        driver.get(baseUrl + "/login.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("john.doe@campusconnect.edu");
        driver.findElement(By.id("password")).sendKeys("WrongPasswordXYZ");
        driver.findElement(By.id("loginButton")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        Assert.assertTrue(error.getText().toLowerCase().contains("invalid"), "Expected invalid credentials error");
    }

    // -------------------------------------------------------------
    // Scenario 8: Login with an unknown email
    // -------------------------------------------------------------
    @Test(priority = 8)
    public void test08_Login_UnknownEmail() {
        driver.get(baseUrl + "/login.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));

        driver.findElement(By.id("email")).sendKeys("nonexistent.user@campusconnect.edu");
        driver.findElement(By.id("password")).sendKeys("RandomPass123");
        driver.findElement(By.id("loginButton")).click();

        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("errorMessage")));
        Assert.assertTrue(error.getText().toLowerCase().contains("invalid"), "Expected invalid email/password error");
    }

    // -------------------------------------------------------------
    // Scenario 9: Logout
    // -------------------------------------------------------------
    @Test(priority = 9)
    public void test09_Logout() {
        loginAs("john.doe@campusconnect.edu", "Student@123");
        logout();
        Assert.assertTrue(driver.getCurrentUrl().contains("login.html"), "Expected redirect to login after logout");
    }

    // -------------------------------------------------------------
    // Scenario 10: Display upcoming events
    // -------------------------------------------------------------
    @Test(priority = 10)
    public void test10_DisplayUpcomingEvents() {
        driver.get(baseUrl + "/events.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("event-card")));

        List<WebElement> cards = driver.findElements(By.className("event-card"));
        Assert.assertTrue(cards.size() >= 5, "Expected at least 5 event cards displayed");
    }

    // -------------------------------------------------------------
    // Scenario 11: Search events by title
    // -------------------------------------------------------------
    @Test(priority = 11)
    public void test11_SearchEventsByTitle() {
        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));

        searchInput.clear();
        searchInput.sendKeys("CodeFest");

        wait.until(d -> {
            List<WebElement> cards = d.findElements(By.className("event-card"));
            return cards.size() == 1 && cards.get(0).getText().contains("CodeFest");
        });
        List<WebElement> cards = driver.findElements(By.className("event-card"));
        Assert.assertEquals(cards.size(), 1, "Expected exactly 1 card matching CodeFest");
    }

    // -------------------------------------------------------------
    // Scenario 12: Filter events by category
    // -------------------------------------------------------------
    @Test(priority = 12)
    public void test12_FilterEventsByCategory() {
    driver.get(baseUrl + "/events.html");
    WebElement categoryFilter = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                    By.id("categoryFilter")));

    Select select = new Select(categoryFilter);
    select.selectByVisibleText("Sports");

    wait.until(d -> {
        List<WebElement> cards =
                d.findElements(By.className("event-card"));

        return !cards.isEmpty()
                && cards.stream().allMatch(
                        c -> c.getText().contains("Sports"));
    });

    List<WebElement> cards =
            driver.findElements(By.className("event-card"));

    Assert.assertTrue(cards.size() >= 1,
            "Expected at least 1 sports event card");
}
    

    // -------------------------------------------------------------
    // Scenario 13: Search with no matching results
    // -------------------------------------------------------------
    @Test(priority = 13)
    public void test13_SearchWithNoMatchingResults() {
        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));

        searchInput.clear();
        searchInput.sendKeys("NonexistentGibberishKeyword9999");

        WebElement emptyMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("emptyEventsMessage")));
        Assert.assertTrue(emptyMsg.isDisplayed(), "Expected emptyEventsMessage to be displayed");
    }

    // -------------------------------------------------------------
    // Scenario 14: View event details
    // -------------------------------------------------------------
    @Test(priority = 14)
    public void test14_ViewEventDetails() {
        driver.get(baseUrl + "/events.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("event-card")));

        WebElement eventCard = driver.findElement(By.cssSelector(".event-card"));
        WebElement detailsBtn = eventCard.findElement(By.cssSelector(".eventDetailsButton"));
        detailsBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("detailTitle")));
        WebElement titleEl = driver.findElement(By.id("detailTitle"));
        Assert.assertFalse(titleEl.getText().isEmpty(), "Expected event title to be populated on details page");
    }

    // -------------------------------------------------------------
    // Scenario 15: Register for an event successfully
    // -------------------------------------------------------------
    @Test(priority = 15)
    public void test15_RegisterForEventSuccessfully() {
        // Login as student 3 who has not registered for AI workshop
        loginAs("alex.kumar@campusconnect.edu", "Student@123");

        // Navigate to events, find AI workshop
        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
        searchInput.clear();
        searchInput.sendKeys("AI and Machine Learning");

        wait.until(d -> d.findElements(By.className("event-card")).size() == 1);
        WebElement eventCard = driver.findElement(By.cssSelector(".event-card"));
        eventCard.findElement(By.cssSelector(".eventDetailsButton")).click();

        wait.until(ExpectedConditions.urlContains("event-details.html"));
        WebElement regBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("eventRegisterButton")));
        regBtn.click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("registered"), "Expected registration success message");
    }

    // -------------------------------------------------------------
    // Scenario 16: Prevent duplicate event registration
    // -------------------------------------------------------------
    @Test(priority = 16)
    public void test16_PreventDuplicateEventRegistration() {
        // John Doe is already registered for CodeFest 2026 in seed data
        loginAs("john.doe@campusconnect.edu", "Student@123");

        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
        searchInput.clear();
        searchInput.sendKeys("CodeFest");
        wait.until(d -> d.findElements(By.className("event-card")).size() == 1);
        WebElement eventCard = driver.findElement(By.cssSelector(".event-card"));
        eventCard.findElement(By.cssSelector(".eventDetailsButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventRegisterButton")));
        WebElement regBtn = driver.findElement(By.id("eventRegisterButton"));

        // Button should show already registered and be disabled
        Assert.assertTrue(regBtn.getText().contains("Already Registered") || !regBtn.isEnabled(),
                "Expected user to be prevented from duplicate registration");
    }

    // -------------------------------------------------------------
    // Scenario 17: Prevent registration for a full event
    // -------------------------------------------------------------
    @Test(priority = 17)
    public void test17_PreventRegistrationForFullEvent() {
        // Photography Competition has capacity 2, both seats filled in seed
        loginAs("alex.kumar@campusconnect.edu", "Student@123");

        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
        searchInput.sendKeys("Photography");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("event-title"), "Photography"));
        WebElement eventCard = driver.findElement(By.cssSelector(".event-card"));
        eventCard.findElement(By.cssSelector(".eventDetailsButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventRegisterButton")));
        WebElement regBtn = driver.findElement(By.id("eventRegisterButton"));
        Assert.assertTrue(regBtn.getText().contains("Full") || !regBtn.isEnabled(),
                "Expected registration to be disabled for full event");
    }

    // -------------------------------------------------------------
    // Scenario 18: Prevent registration for a past event
    // -------------------------------------------------------------
    @Test(priority = 18)
    public void test18_PreventRegistrationForPastEvent() {
        loginAs("alex.kumar@campusconnect.edu", "Student@123");

        driver.get(baseUrl + "/events.html");
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("searchInput")));
        searchInput.sendKeys("Campus Orientation");

        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("event-title"), "Campus Orientation"));
        WebElement eventCard = driver.findElement(By.cssSelector(".event-card"));
        eventCard.findElement(By.cssSelector(".eventDetailsButton")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventRegisterButton")));
        WebElement regBtn = driver.findElement(By.id("eventRegisterButton"));
        Assert.assertTrue(regBtn.getText().contains("Past Event") || !regBtn.isEnabled(),
                "Expected past event registration to be prevented");
    }

    // -------------------------------------------------------------
    // Scenario 19: Display My Registrations correctly
    // -------------------------------------------------------------
    @Test(priority = 19)
    public void test19_DisplayMyRegistrationsCorrectly() {
        loginAs("john.doe@campusconnect.edu", "Student@123");
        driver.get(baseUrl + "/my-registrations.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registrationsTableWrapper")));
        List<WebElement> rows = driver.findElements(By.cssSelector("#registrationsTableBody tr"));
        Assert.assertTrue(rows.size() >= 1, "Expected John Doe to have at least 1 registration displayed");
    }

    // -------------------------------------------------------------
    // Scenario 20: Cancel an event registration
    // -------------------------------------------------------------
    @Test(priority = 20)
    public void test20_CancelEventRegistration() {
        loginAs("john.doe@campusconnect.edu", "Student@123");
        driver.get(baseUrl + "/my-registrations.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cancelRegistrationButton")));
        WebElement cancelBtn = driver.findElement(By.id("cancelRegistrationButton"));
        cancelBtn.click();

        // Confirmation modal
        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmCancelButton")));
        confirmBtn.click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("cancelled"), "Expected cancellation success message");
    }

    // -------------------------------------------------------------
    // Scenario 21: Update available seats after cancellation
    // -------------------------------------------------------------
    @Test(priority = 21)
    public void test21_UpdateAvailableSeatsAfterCancellation() {
        loginAs("john.doe@campusconnect.edu", "Student@123");
    
        driver.get(baseUrl + "/events.html");
    
        WebElement eventCard = wait.until(d ->
                d.findElements(By.className("event-card"))
                        .stream()
                        .filter(card -> card.getText().contains("CodeFest"))
                        .findFirst()
                        .orElse(null));
    
        Assert.assertNotNull(eventCard, "CodeFest event was not found");
    
        int seatsBefore = readAvailableSeats(eventCard);
    
        driver.get(baseUrl + "/my-registrations.html");
    
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("registrationsTableWrapper")));
    
        WebElement registrationRow = driver.findElements(
                        By.cssSelector("#registrationsTableBody tr"))
                .stream()
                .filter(row -> row.getText().contains("CodeFest"))
                .findFirst()
                .orElse(null);
    
        Assert.assertNotNull(registrationRow,
                "John Doe's CodeFest registration was not found");
    
        registrationRow.findElement(
                By.id("cancelRegistrationButton")).click();
    
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("confirmCancelButton"))).click();
    
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("successMessage")));
    
        driver.get(baseUrl + "/events.html");
    
        WebElement updatedCard = wait.until(d ->
                d.findElements(By.className("event-card"))
                        .stream()
                        .filter(card -> card.getText().contains("CodeFest"))
                        .findFirst()
                        .orElse(null));
    
        Assert.assertNotNull(updatedCard, "CodeFest event disappeared");
    
        int seatsAfter = readAvailableSeats(updatedCard);
    
        Assert.assertEquals(seatsAfter, seatsBefore + 1,
                "Available seats should increase by one after cancellation");
    }

    // -------------------------------------------------------------
    // Scenario 22: Create an event as admin
    // -------------------------------------------------------------
    @Test(priority = 22)
    public void test22_CreateEventAsAdmin() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/event-form.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventTitle")));
        driver.findElement(By.id("eventTitle")).sendKeys("Selenium Automation Summit 2026");

        Select categorySelect = new Select(driver.findElement(By.id("eventCategory")));
        categorySelect.selectByVisibleText("Technical");

        driver.findElement(By.id("eventDescription")).sendKeys("National convention on test automation, CI/CD, and quality engineering.");
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("document.getElementById('eventDate').value = '2026-11-20'; document.getElementById('eventTime').value = '10:00';");
        driver.findElement(By.id("eventVenue")).sendKeys("Auditorium C");
        driver.findElement(By.id("eventOrganizer")).sendKeys("QA Engineering Guild");
        driver.findElement(By.id("eventCapacity")).sendKeys("75");

        driver.findElement(By.id("saveEventButton")).click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("success"), "Expected event creation success message");
    }

    // -------------------------------------------------------------
    // Scenario 23: Validate an event form with missing fields
    // -------------------------------------------------------------
    @Test(priority = 23)
    public void test23_ValidateEventFormWithMissingFields() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/event-form.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventTitle")));
        driver.findElement(By.id("saveEventButton")).click();

        WebElement validation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationMessage")));
        Assert.assertTrue(validation.getText().contains("required"), "Expected required fields validation message");
    }

    // -------------------------------------------------------------
    // Scenario 24: Reject an invalid event capacity
    // -------------------------------------------------------------
    @Test(priority = 24)
    public void test24_RejectInvalidEventCapacity() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/event-form.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventTitle")));
        driver.findElement(By.id("eventTitle")).sendKeys("Invalid Capacity Test");
        new Select(driver.findElement(By.id("eventCategory"))).selectByVisibleText("Technical");
        driver.findElement(By.id("eventDescription")).sendKeys("Testing capacity validation.");
        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("document.getElementById('eventDate').value = '2026-11-25'; document.getElementById('eventTime').value = '11:00';");
        driver.findElement(By.id("eventVenue")).sendKeys("Lab 1");
        driver.findElement(By.id("eventOrganizer")).sendKeys("Tester");
        driver.findElement(By.id("eventCapacity")).sendKeys("-10");

        driver.findElement(By.id("saveEventButton")).click();

        WebElement validation = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("validationMessage")));
        Assert.assertTrue(validation.getText().toLowerCase().contains("positive"), "Expected positive capacity error");
    }

    // -------------------------------------------------------------
    // Scenario 25: Edit an existing event
    // -------------------------------------------------------------
    @Test(priority = 25)
    public void test25_EditExistingEvent() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/admin-dashboard.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("edit-event-btn")));
        driver.findElement(By.className("edit-event-btn")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eventTitle")));
        WebElement titleInput = driver.findElement(By.id("eventTitle"));
        titleInput.clear();
        titleInput.sendKeys("Updated Event Title QA");

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("document.getElementById('eventDate').value = '2026-11-28';");

        driver.findElement(By.id("saveEventButton")).click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("updated"), "Expected update success message");
    }

    // -------------------------------------------------------------
    // Scenario 26: Delete an event after confirmation
    // -------------------------------------------------------------
    @Test(priority = 26)
    public void test26_DeleteEventAfterConfirmation() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/admin-dashboard.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("delete-event-btn")));
        driver.findElement(By.className("delete-event-btn")).click();

        WebElement confirmBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmDeleteButton")));
        confirmBtn.click();

        WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("successMessage")));
        Assert.assertTrue(success.getText().contains("deleted"), "Expected event deletion success message");
    }

    // -------------------------------------------------------------
    // Scenario 27: View registered students as admin
    // -------------------------------------------------------------
    @Test(priority = 27)
    public void test27_ViewRegisteredStudentsAsAdmin() {
        loginAs("admin@campusconnect.edu", "Admin@123");
        driver.get(baseUrl + "/admin-dashboard.html");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("view-reg-btn")));
        driver.findElement(By.className("view-reg-btn")).click();

        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("studentsModal")));
        Assert.assertTrue(modal.isDisplayed(), "Expected registered students modal to be open");
    }

    // -------------------------------------------------------------
    // Scenario 28: Prevent students from accessing admin-only pages
    // -------------------------------------------------------------
    @Test(priority = 28)
    public void test28_PreventStudentsFromAccessingAdminPages() {
        loginAs("john.doe@campusconnect.edu", "Student@123");

        // Attempt direct URL access to admin dashboard
        driver.get(baseUrl + "/admin-dashboard.html");

        // Should alert and redirect student
        try {
            driver.switchTo().alert().accept();
        } catch (Exception ignored) {}

        wait.until(ExpectedConditions.not(ExpectedConditions.urlContains("admin-dashboard.html")));
        Assert.assertFalse(driver.getCurrentUrl().contains("admin-dashboard.html"),
                "Expected student to be redirected away from admin dashboard");
    }

    // -------------------------------------------------------------
    // Scenario 29: Prevent students from calling admin-only backend endpoints
    // -------------------------------------------------------------
    @Test(priority = 29)
    public void test29_PreventStudentsFromCallingAdminBackendEndpoints()
            throws Exception {
    
        loginAs("john.doe@campusconnect.edu", "Student@123");
    
        String cookieHeader = driver.manage().getCookies()
                .stream()
                .map(cookie -> cookie.getName() + "=" + cookie.getValue())
                .collect(java.util.stream.Collectors.joining("; "));
    
        Assert.assertFalse(cookieHeader.isBlank(),
                "No session cookies found. Check the application's authentication.");
    
        HttpClient client = HttpClient.newHttpClient();
    
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/dashboard/stats"))
                .header("Cookie", cookieHeader)
                .GET()
                .build();
    
        HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());
    
        Assert.assertTrue(
                response.statusCode() == 401
                        || response.statusCode() == 403,
                "A logged-in student must not access admin statistics. HTTP status: "
                        + response.statusCode());
    }

    // -------------------------------------------------------------
    // Scenario 30: Verify navigation, session behavior, and page refresh behavior
    // -------------------------------------------------------------
    @Test(priority = 30)
    public void test30_VerifyNavigationSessionAndPageRefresh() {
        loginAs("john.doe@campusconnect.edu", "Student@123");

        driver.get(baseUrl + "/events.html");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userBadge")));

        // Refresh the page
        driver.navigate().refresh();

        WebElement badgeAfterRefresh = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("userBadge")));
        Assert.assertTrue(badgeAfterRefresh.getText().contains("John Doe"),
                "Expected user session to persist after page refresh");
        Assert.assertEquals(
        driver.findElements(
                By.cssSelector("a[href='/my-registrations.html']")).size(),
        1,
        "My Registrations must appear exactly once");

        Assert.assertEquals(
        driver.findElements(
                By.cssSelector("a[href='/admin-dashboard.html']")).size(),
        0,
        "A student must not see Admin Dashboard");
    }
    private int readAvailableSeats(WebElement eventCard) {
        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile(
                        "(?i)(?:available\\s+seats|seats\\s+available|available)"
                        + "\\s*:?\\s*(\\d+)|(\\d+)\\s+seats\\s+available");
    
        java.util.regex.Matcher matcher =
                pattern.matcher(eventCard.getText());
    
        Assert.assertTrue(matcher.find(),
                "Could not find available-seat count in event card: "
                        + eventCard.getText());
    
        String count = matcher.group(1) != null
                ? matcher.group(1)
                : matcher.group(2);
    
        return Integer.parseInt(count);
    }
}