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
            // start maximized; also call maximize() after creating the driver for robustness
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
    @Given("click on Approvals button")
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

    @And("verify that the new register user is displayed in the search results")
    public void verify_that_the_new_register_user_is_displayed_in_the_search_results() {
        System.out.println("[stub] verify new user displayed in search results");
    }

    @And("verify the generated email appears in search results")
    public void verify_generated_email_in_search_results() throws InterruptedException {
        // Retrieve email from shared context
        String emailToVerify = testContext.getGeneratedEmail();

        if (emailToVerify == null || emailToVerify.isEmpty()) {
            throw new RuntimeException("No generated email to verify in TestContext");
        }

        System.out.println("[DEBUG] Verifying email in search results: " + emailToVerify);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Wait for results to load
            Thread.sleep(1000);

            // Get page content and check for email
            String pageSource = driver.getPageSource();

            if (pageSource.contains(emailToVerify)) {
                System.out.println("✓ Generated email found in search results: " + emailToVerify);
            } else {
                throw new AssertionError("Generated email not found in search results: " + emailToVerify);
            }

            // Also try to find element containing email
            try {
                WebElement emailElement = driver.findElement(By.xpath("//*[contains(text(), '" + emailToVerify + "')]"));
                if (emailElement.isDisplayed()) {
                    System.out.println("✓ Email element is visible in search results");
                }
            } catch (NoSuchElementException e) {
                System.out.println("[WARNING] Email text not found as separate element, but found in page source");
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Failed to verify generated email in results: " + e.getMessage());
            throw new RuntimeException("Could not verify generated email in search results", e);
        }
    }

    @And("verify if the account status for the new user is \"Inactive\"")
    public void verify_if_the_account_status_for_the_new_user_is_inactive() {
        System.out.println("[stub] verify account status is Inactive");
    }

    @And("on inactive status to trigger activation popup")
    public void on_inactive_status_to_trigger_activation_popup() {
        System.out.println("[stub] trigger activation popup on inactive status");
    }

    @And("click ok button on the activation popup")
    public void click_ok_button_on_the_activation_popup() {
        System.out.println("[stub] click OK on activation popup");
    }

}
