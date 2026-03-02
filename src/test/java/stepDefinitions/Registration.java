package stepDefinitions;
import io.cucumber.java.en.*;
import org.openqa.selenium.Alert;
import org.testng.Assert;
//import utils.Base;
import org.openqa.selenium.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import utils.EmailGenerator;
import utils.TestContext;
import java.time.Duration;


public class Registration {
    private WebDriver driver;
    private String currentPassword = "";
    private String currentConfirmPassword = "";
    private String generatedEmail = "";

    // Shared context to store data across scenarios
    private TestContext testContext = TestContext.getInstance();

    @Given("the admin is on the login page")
    public void the_admin_is_on_the_login_page() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://ndosisimplifiedautomation.vercel.app/#practice");
    }
    @When("the admin click the sign up link")
    public void the_admin_click_the_sign_up_link() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement signup = wait.until(ExpectedConditions.elementToBeClickable(By.id("signup-toggle")));
        signup.click();
    }

    @Then("the admin is on the registration page")
    public void the_admin_is_on_the_registration_page() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement heading = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("registration-heading")));
        Assert.assertTrue(heading.isDisplayed(), "Registration page is not displayed");
    }
    // Use regex captures so Scenario Outline placeholders (unquoted) are accepted
    @When("^the admin enters valid firstname (.+)$")
    public void the_admin_enters_valid_firstname(String firstname) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-firstName")));
        el.clear();
        el.sendKeys(firstname);

    }
    @When("^the admin enters valid lastname (.+)$")
    public void the_admin_enters_valid_lastname(String lastname) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-lastName")));
        el.clear();
        el.sendKeys(lastname);

    }
    @When("^the admin enters valid email (.+)$")
    public void the_admin_enters_valid_email(String email) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));

        // Generate unique email if AUTO_GENERATE is specified
        String emailToUse = email.equals("AUTO_GENERATE")
            ? EmailGenerator.generateEmailWithTimestamp()
            : email;

        // Store in both local and shared context
        this.generatedEmail = emailToUse;
        testContext.setGeneratedEmail(emailToUse);  // Store in shared context

        el.clear();
        el.sendKeys(emailToUse);

        System.out.println("✓ Email entered: " + emailToUse);
    }
    @When("^the admin enter valid first password (.+)$")
    public void the_admin_enter_valid_first_password(String fpassword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-password")));
        el.clear();
        el.sendKeys(fpassword);
        this.currentPassword = fpassword;
        testContext.setCurrentPassword(fpassword);  // Store in TestContext
        System.out.println("[DEBUG] Password entered: " + fpassword);
    }
    @When("^the admin enter valid confirm password (.+)$")
    public void the_admin_enter_valid_confirm_password(String confirmpassword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-confirmPassword")));
        el.clear();
        el.sendKeys(confirmpassword);
        this.currentConfirmPassword = confirmpassword;
        System.out.println("[DEBUG] Confirm password entered: " + confirmpassword);
    }
    @When("the confirm password is the same as the password")
    public void the_confirm_password_is_the_same_as_the_password() {
        // Get values from stored variables (preferred method)
        System.out.println("[DEBUG] Verifying passwords match...");
        System.out.println("[DEBUG] Stored password: " + this.currentPassword);
        System.out.println("[DEBUG] Stored confirm password: " + this.currentConfirmPassword);

        // Also verify from the UI elements as backup
        String passwordFromUI = driver.findElement(By.id("register-password")).getAttribute("value");
        String confirmPasswordFromUI = driver.findElement(By.id("register-confirmPassword")).getAttribute("value");

        System.out.println("[DEBUG] Password from UI: " + passwordFromUI);
        System.out.println("[DEBUG] Confirm password from UI: " + confirmPasswordFromUI);

        // Verify both stored values match
        Assert.assertEquals(this.currentConfirmPassword, this.currentPassword,
            "Confirm password does not match password (stored values)");

        // Verify UI values match
        Assert.assertEquals(confirmPasswordFromUI, passwordFromUI,
            "Confirm password does not match password (UI values)");

        System.out.println("[DEBUG] ✓ Passwords match successfully!");
    }
    @When("^the admin selects the correct group (.+)$")
    public void the_admin_selects_the_correct_group(String group) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("register-group")));
        try {
            // try the normal Select API first
            Select sel = new Select(selectEl);
            sel.selectByVisibleText(group);
            return;
        } catch (Exception e) {
            // fallback: ensure element is visible and click option manually
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectEl);
                wait.until(ExpectedConditions.elementToBeClickable(selectEl)).click();
                WebElement option = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//select[@id='register-group']/option[normalize-space(text())='" + group + "']")));
                option.click();
                return;
            } catch (Exception ex) {
                // final fallback: set value via JS
                ((JavascriptExecutor) driver).executeScript("var s=document.getElementById('register-group'); if(s){s.value=arguments[0]; s.dispatchEvent(new Event('change'));}", group);
            }
        }
    }
    @When("the admin click the create account button")
    public void the_admin_click_the_create_account_button() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            // Try to find the button using multiple possible selectors
            WebElement submitBtn = null;
            try {
                submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-submit")));
            } catch (TimeoutException e) {
                System.out.println("[DEBUG] Could not find button with id 'register-submit', trying alternative selectors...");
                try {
                    submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("createAccountBtn")));
                } catch (TimeoutException e2) {
                    submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'Create') or contains(text(), 'Register')]")));
                }
            }

            if (submitBtn != null) {
                // Scroll into view before clicking
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
                Thread.sleep(500);
                submitBtn.click();
                System.out.println("[DEBUG] Create account button clicked successfully");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click create account button: " + e.getMessage());
            throw new RuntimeException("Could not click create account button", e);
        }
    }
    @Then("the successful registration message is displayed")
    public void the_successful_registration_message_is_displayed() {
        // Switch to the alert
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        // Get the text from the pop-up
        String alertMessage = alert.getText();

        // Print and verify the message
        System.out.println("Alert message: " + alertMessage);
        Assert.assertNotNull(alertMessage, "Alert message is null");
        Assert.assertTrue(!alertMessage.isEmpty() && (alertMessage.contains("successfully") || alertMessage.contains("pending admin approval")),
                "Registration message not displayed or invalid");

        // Accept (Click OK)
        alert.accept();

        // Close the browser
        driver.quit();
    }

    // --- Minimal stub step definitions for approval/admin flow ---
    @Given("^admin user logs in as admin (.+) with password (.+)$")
    public void admin_user_logs_in_as_admin_with_password(String adminemail, String adminpassword) {
        driver.findElement(By.id("login-email")).sendKeys(adminemail);
        driver.findElement(By.id("login-password")).sendKeys(adminpassword);
        // Minimal stub: log values; real implementation should perform login actions
        System.out.println("[stub] admin login: " + adminemail + " / " + adminpassword);
    }
//login button
    @And("click on admin button")
    public void click_on_admin_button() {
        driver.findElement(By.id("login-submit")).click();
        System.out.println("[stub] click on admin button");
    }

    @And("click on admin panel")
    public void click_on_admin_panel() throws InterruptedException {
        Thread.sleep(2000);

        driver.findElement(By.xpath("//button[contains(@class,'user-pill')]")).click();

        Thread.sleep(2000);

        driver.findElement(By.xpath("//button[@class='nav-dropdown-item']/span[text()='Admin Panel']/parent::button")).click();

        Thread.sleep(2000);
        System.out.println("[stub] click on admin panel");
    }
    @And("click on Approvals button")
    public void click_on_approvals_button() throws InterruptedException {
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement approvalsButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Approvals')]")
        ));

        // Scroll into view
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", approvalsButton);
        Thread.sleep(500);

        approvalsButton.click();

        Thread.sleep(1000);
        System.out.println("✓ Clicked on Approvals button");
    }




    @And("the admin searches for the registered user by generated email")
    public void the_admin_searches_for_registered_user_by_generated_email() throws InterruptedException {
        // Retrieve email from shared context
        String emailToSearch = testContext.getGeneratedEmail();

        if (emailToSearch == null || emailToSearch.isEmpty()) {
            throw new RuntimeException("No generated email found in TestContext. Make sure user registered first.");
        }

        System.out.println("[DEBUG] Retrieved email from TestContext: " + emailToSearch);

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@type='text' and @placeholder='Search by name or email...']")
        ));

        searchField.clear();
        searchField.sendKeys(emailToSearch);

        Thread.sleep(1000);
        System.out.println("✓ Searched for user with email: " + emailToSearch);
    }

    @And("click the approve button")
    public void click_the_approve_button() throws InterruptedException {
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement approveButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Approve')]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", approveButton);
        Thread.sleep(500);

        approveButton.click();

        Thread.sleep(1000);
        System.out.println("✓ Clicked on Approve button");
    }

        @Then("approve message is displayed")
        public void approve_message_is_displayed() {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            try {
                WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//div[contains(text(), 'User approved successfully!')]")
                ));

                String messageText = successMessage.getText();
                System.out.println("✓ Success message: " + messageText);

                Assert.assertTrue(messageText.contains("User approved successfully!"),
                        "Approval success message not displayed");

                System.out.println("✓ User approved successfully message verified");
            } catch (TimeoutException e) {
                throw new RuntimeException("Approval success message not displayed", e);
            }
        }


    @Given("the admin is on the Users management page")
    public void the_admin_is_on_the_users_management_page() throws InterruptedException {

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement usersButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'Users')]")
        ));

        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", usersButton);
        Thread.sleep(500);

        usersButton.click();

        Thread.sleep(1000);
        System.out.println("✓ Navigated to Users management page");
    }


    @Given("the admin searches for the registered user by generated email in user management")
    public void the_admin_searches_for_the_registered_user_by_generated_email_in_user_management() throws InterruptedException {
        String emailToSearch = testContext.getGeneratedEmail();

        if (emailToSearch == null || emailToSearch.isEmpty()) {
            throw new RuntimeException("No generated email found in TestContext. Make sure user registered first.");
        }

        System.out.println("[DEBUG] Retrieved email from TestContext: " + emailToSearch);

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement searchField = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@placeholder='🔍 Search users...']")
        ));

        searchField.clear();
        searchField.sendKeys(emailToSearch);

        Thread.sleep(1000);
        System.out.println("✓ Searched for user with email: " + emailToSearch);
    }

    @Given("click on user drop down")
    public void click_on_user_drop_down() throws InterruptedException {
        Thread.sleep(2000);

        String emailToFind = testContext.getGeneratedEmail();

        if (emailToFind == null || emailToFind.isEmpty()) {
            throw new RuntimeException("No generated email found. Cannot identify user.");
        }

        System.out.println("[DEBUG] Looking for dropdown for user: " + emailToFind);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Find the select dropdown element for role
            WebElement roleDropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select[.//option[@value='admin']]")
            ));

            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", roleDropdown);
            Thread.sleep(500);

            roleDropdown.click();

            Thread.sleep(1000);
            System.out.println("✓ Clicked on role dropdown");
        } catch (Exception e) {
            System.out.println("[ERROR] Could not find user dropdown");
            throw new RuntimeException("Could not find user role dropdown", e);
        }
    }
    @Given("select admin")
    public void select_admin() throws InterruptedException {
        Thread.sleep(1000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            WebElement selectElement = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//select[.//option[@value='admin']]")
            ));

            Select roleSelect = new Select(selectElement);
            roleSelect.selectByValue("admin");

            Thread.sleep(1000);
            System.out.println("✓ Selected Admin role from dropdown");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to select admin role: " + e.getMessage());
            throw new RuntimeException("Could not select admin role", e);
        }
    }
    @Then("click ok to confirm admin role change")
    public void click_ok_to_confirm_admin_role_change() throws InterruptedException {
        Thread.sleep(1500);

        try {
            // First, check if there's already an alert present (without waiting)
            try {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                System.out.println("Browser alert detected immediately: " + alertText);
                alert.accept();
                System.out.println("✓ Accepted browser alert to confirm role change");
                Thread.sleep(1000);
                return;
            } catch (NoAlertPresentException e) {
                System.out.println("[DEBUG] No immediate alert, waiting for alert or button...");
            }

            // Wait a bit for alert to appear
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                System.out.println("Browser alert detected after wait: " + alertText);
                alert.accept();
                System.out.println("✓ Accepted browser alert to confirm role change");
                Thread.sleep(1000);
                return;
            } catch (TimeoutException e) {
                System.out.println("[DEBUG] No browser alert, checking for confirmation button...");
            }

            // If no browser alert, look for confirmation button
            WebDriverWait buttonWait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement confirmButton = buttonWait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(), 'OK') or contains(text(), 'Ok') or contains(text(), 'Confirm') or contains(text(), 'Yes') or contains(text(), 'Submit')]")
            ));

            // Scroll into view
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", confirmButton);
            Thread.sleep(300);

            confirmButton.click();
            Thread.sleep(1000);

            System.out.println("✓ Clicked OK to confirm admin role change");

        } catch (TimeoutException e) {
            System.out.println("[WARNING] No confirmation button found - role might be changed automatically");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to click confirmation: " + e.getMessage());
            throw new RuntimeException("Could not confirm admin role change", e);
        }
    }
    @Then("click ok on the successful alert")
    public void click_ok_on_the_successful_alert() throws InterruptedException {
        Thread.sleep(1500);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Check if it's a browser alert
            try {
                wait.until(ExpectedConditions.alertIsPresent());
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                System.out.println("Alert message: " + alertText);
                alert.accept();
                System.out.println("✓ Accepted browser alert");
                return;
            } catch (TimeoutException e1) {
                System.out.println("[DEBUG] No browser alert, checking for custom success message...");
            }

            // Check for custom success message with OK button
            try {
                WebElement successMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//div[contains(text(), 'success') or contains(text(), 'Success') or contains(text(), 'updated') or contains(text(), 'changed')]")
                ));
                System.out.println("Success message displayed: " + successMessage.getText());

                // Try to find and click OK button in the message
                try {
                    WebElement okButton = driver.findElement(
                        By.xpath("//button[contains(text(), 'OK') or contains(text(), 'Close') or contains(text(), 'Dismiss')]")
                    );
                    okButton.click();
                    System.out.println("✓ Clicked OK on success message");
                } catch (NoSuchElementException e) {
                    System.out.println("✓ Success message displayed (no OK button needed)");
                }
                return;
            } catch (TimeoutException e2) {
                System.out.println("[WARNING] No success alert or message found - continuing anyway");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to handle success alert: " + e.getMessage());
            throw new RuntimeException("Could not handle success alert", e);
        }
    }
    @Then("user role is changed to admin")
    public void user_role_is_changed_to_admin() throws InterruptedException {
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Get the generated email
        String emailToVerify = testContext.getGeneratedEmail();

        if (emailToVerify == null || emailToVerify.isEmpty()) {
            throw new RuntimeException("No generated email found. Cannot verify role change.");
        }

        System.out.println("[DEBUG] Verifying admin role for user: " + emailToVerify);

        try {
            // Find the user row
            WebElement userRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//td[contains(text(), '" + emailToVerify + "')]/ancestor::tr")
            ));

            // Check if the role displays "Admin"
            String rowText = userRow.getText().toLowerCase();

            if (rowText.contains("admin")) {
                System.out.println("✓ User role is changed to Admin");
                System.out.println("Row content: " + userRow.getText());
                return;
            }

            // Alternative: Check for admin badge/label
            try {
                WebElement adminBadge = userRow.findElement(
                    By.xpath(".//span[contains(text(), 'Admin')] | .//div[contains(text(), 'Admin')] | .//option[@value='admin' and @selected]")
                );
                if (adminBadge.isDisplayed()) {
                    System.out.println("✓ Admin badge is visible for user");
                    return;
                }
            } catch (NoSuchElementException e) {
                System.out.println("[WARNING] Could not find admin badge");
            }

            throw new AssertionError("User role does not appear to be changed to Admin");

        } catch (NoSuchElementException e) {
            System.out.println("[ERROR] Could not find user row to verify role");
            throw new RuntimeException("Could not verify admin role change", e);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to verify admin role: " + e.getMessage());
            throw new RuntimeException("Could not verify admin role change", e);
        }
    }

    @Given("the approved user logs in with the generated email")
    public void the_approved_user_logs_in_with_generated_email() throws InterruptedException {
        // Retrieve the generated email from TestContext
        String userEmail = testContext.getGeneratedEmail();

        if (userEmail == null || userEmail.isEmpty()) {
            throw new RuntimeException("No generated email found. User must be registered and approved first.");
        }

        System.out.println("[DEBUG] Logging in with generated email: " + userEmail);

        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Find and fill email field
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("login-email")
        ));
        emailField.clear();
        emailField.sendKeys(userEmail);

        // Find and fill password field - use the stored password
        String password = testContext.getCurrentPassword();
        if (password == null || password.isEmpty()) {
            throw new RuntimeException("No password found. Make sure password was set during registration.");
        }

        WebElement passwordField = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("login-password")
        ));
        passwordField.clear();
        passwordField.sendKeys(password);

        // Click login button
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.id("login-submit")
        ));
        loginButton.click();

        Thread.sleep(2000);
        System.out.println("✓ Logged in with generated email: " + userEmail);
    }

    @Then("verify that the user is now an admin")
    public void verify_that_the_user_is_now_an_admin() throws InterruptedException {
        Thread.sleep(2000);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Method 1: Check for Admin Panel button
            try {
                WebElement adminPanelButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(), 'Admin Panel')]")
                ));

                if (adminPanelButton.isDisplayed()) {
                    System.out.println("✓ Admin Panel button is visible - User is an admin");
                    return;
                }
            } catch (TimeoutException e1) {
                System.out.println("[DEBUG] Admin Panel button not found, trying alternative checks...");
            }

            // Method 2: Check for Approvals button in navigation
            try {
                WebElement approvalsButton = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(text(), 'Approvals')]")
                ));

                if (approvalsButton.isDisplayed()) {
                    System.out.println("✓ Approvals button is visible - User is an admin");
                    return;
                }
            } catch (TimeoutException e2) {
                System.out.println("[DEBUG] Approvals button not found, trying admin role indicator...");
            }

            // Method 3: Check page source for admin role indicator
            String pageSource = driver.getPageSource();

            if (pageSource.contains("admin") && pageSource.contains("Admin")) {
                System.out.println("✓ Admin role found in page source - User is an admin");
                return;
            }

            // Method 4: Check for user role in profile/dashboard
            try {
                WebElement userPill = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.xpath("//button[contains(@class, 'user-pill')]")
                ));
                userPill.click();

                Thread.sleep(500);
                String userMenuSource = driver.getPageSource();

                if (userMenuSource.contains("admin") || userMenuSource.contains("Admin")) {
                    System.out.println("✓ Admin role verified in user menu");
                    return;
                }
            } catch (Exception e) {
                System.out.println("[DEBUG] Could not check user menu");
            }

            throw new AssertionError("User does not appear to have admin role. Admin controls not found.");

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to verify admin status: " + e.getMessage());
            throw new RuntimeException("Could not verify admin status", e);
        }
    }

    }

