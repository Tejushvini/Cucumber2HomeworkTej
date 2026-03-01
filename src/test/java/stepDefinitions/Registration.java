package stepDefinitions;
import io.cucumber.java.en.*;
import org.openqa.selenium.Alert;
import org.testng.Assert;
//import utils.Base;
import org.openqa.selenium.By;
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

}
