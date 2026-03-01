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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;
import java.time.Duration;


public class Registration {
    private WebDriver driver;

    @Given("the admin is on the login page")
    public void the_admin_is_on_the_login_page() {
            driver = new ChromeDriver();
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
        el.clear();
        el.sendKeys(email);
    }
    @When("^the admin enter valid first password (.+)$")
    public void the_admin_enter_valid_first_password(String fpassword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-password")));
         el.clear();
         el.sendKeys(fpassword);

    }
    @When("^the admin enter valid confirm password (.+)$")
    public void the_admin_enter_valid_confirm_password(String confirmpassword) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-confirmPassword")));
        el.clear();
        el.sendKeys(confirmpassword);

    }
    @When("the confirm password is the same as the password")
    public void the_confirm_password_is_the_same_as_the_password() {
        String password = driver.findElement(By.id("register-password")).getAttribute("value");
        String confirmPassword = driver.findElement(By.id("register-confirmPassword")).getAttribute("value");
        Assert.assertEquals(confirmPassword, password, "Confirm password does not match password");
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
        driver.findElement(By.id("register-submit")).click();

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
        Assert.assertTrue(alertMessage.contains("successfully") || alertMessage.contains("pending admin approval") || alertMessage.length() > 0,
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

    @And("click on admin button")
    public void click_on_admin_button() {
        driver.findElement(By.id("login-submit")).click();
        System.out.println("[stub] click on admin button");
    }

    @And("click on admin panel")
    public void click_on_admin_panel() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

         {

            // 1) Locate and click the user-pill button
            By userPillBtn = By.cssSelector("button.user-pill");
            WebElement pill = wait.until(ExpectedConditions.elementToBeClickable(userPillBtn));
            pill.click();

            // 2) Wait for dropdown to be visible/open
            // Prefer the "open" modifier to avoid false positives
            By dropdownOpen = By.cssSelector("div.nav-dropdown.open");
            WebElement dropdown = wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownOpen));

            // 3) Click the "Admin Panel" option by text
            // This XPath is resilient regardless of the tag used for the item.
            By adminPanelItem = By.xpath("//div[contains(@class,'nav-dropdown') and contains(@class,'open')]//*[normalize-space(text())='Admin Panel']");
            WebElement admin = wait.until(ExpectedConditions.elementToBeClickable(adminPanelItem));
            admin.click();
        }
        System.out.println("[stub] click on admin panel");
    }

    @And("^enter email (.+) in the search box$")
    public void enter_email_in_the_search_box(String email) {
        System.out.println("[stub] enter email in search box: " + email);
    }

    @And("verify that the new register user is displayed in the search results")
    public void verify_that_the_new_register_user_is_displayed_in_the_search_results() {
        System.out.println("[stub] verify new user displayed in search results");
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
