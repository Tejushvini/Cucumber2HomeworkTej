# Troubleshooting Guide - Registration Test Issues

## Issue 1: Parameter Mismatch Error
### Error Message:
```
io.cucumber.core.exception.CucumberException: Step [the admin enters valid firstname John] 
is defined with 1 parameters but the gherkin step has 0 arguments
```

### Root Cause:
The step definition expects a parameter (captured in regex), but the feature file doesn't provide it in the right format.

### Solution Applied:
✅ Updated step definitions to use regex patterns:
```java
@When("^the admin enters valid firstname (.+)$")
public void the_admin_enters_valid_firstname(String firstname) {
    // ... code ...
}
```

### Feature File Should Be:
```gherkin
When the admin enters valid firstname John       # ✓ Correct - has value
When the admin enters valid firstname <firstname> # ✓ Correct - parameter
```

---

## Issue 2: Element Not Interactable Error
### Error Message:
```
org.openqa.selenium.ElementNotInteractableException: element not interactable
Element: [[ChromeDriver: chrome on windows] -> id: register-group]
```

### Root Cause:
The dropdown element exists but is not visible/clickable when the script tries to interact with it.

### Solutions:

#### Option 1: Scroll Into View (Applied in Updated Code)
```java
WebElement selectEl = driver.findElement(By.id("register-group"));

// Scroll element into viewport
((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectEl);

// Wait a moment for scroll animation
Thread.sleep(500);

// Now click
selectEl.click();
```

#### Option 2: Use JavaScript to Set Value
```java
WebElement selectEl = driver.findElement(By.id("register-group"));

// Set value via JavaScript
((JavascriptExecutor) driver).executeScript(
    "var s = document.getElementById('register-group'); " +
    "if(s) { s.value = arguments[0]; s.dispatchEvent(new Event('change')); }",
    "Group T (2026)"
);
```

#### Option 3: Wait for Visibility
```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

// Wait for element to be visible
WebElement selectEl = wait.until(
    ExpectedConditions.visibilityOfElementLocated(By.id("register-group"))
);

// Then click
selectEl.click();
```

### Updated Code in Registration.java:
```java
@When("^the admin selects the correct group (.+)$")
public void the_admin_selects_the_correct_group(String group) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement selectEl = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("register-group")));
    
    try {
        // Try normal Select API
        Select sel = new Select(selectEl);
        sel.selectByVisibleText(group);
        return;
    } catch (Exception e) {
        // Fallback: scroll and click option manually
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", selectEl);
            wait.until(ExpectedConditions.elementToBeClickable(selectEl)).click();
            
            WebElement option = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//select[@id='register-group']/option[normalize-space(text())='" + group + "']")
            ));
            option.click();
            return;
        } catch (Exception ex) {
            // Final fallback: set via JS
            ((JavascriptExecutor) driver).executeScript(
                "var s=document.getElementById('register-group'); " +
                "if(s){s.value=arguments[0]; s.dispatchEvent(new Event('change'));}", 
                group
            );
        }
    }
}
```

---

## Issue 3: Button Not Found Error
### Error Message:
```
org.openqa.selenium.TimeoutException: Expected condition failed: waiting for element found by 
By.id: createAccountBtn to be clickable, but the element was not found
```

### Root Cause:
The button element doesn't exist or has a different ID than expected.

### Solution Applied:
✅ Added multiple fallback selectors and better error handling:

```java
@When("the admin click the create account button")
public void the_admin_click_the_create_account_button() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    try {
        WebElement submitBtn = null;
        
        // Try primary selector
        try {
            submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-submit")));
        } catch (TimeoutException e) {
            System.out.println("[DEBUG] Could not find 'register-submit', trying alternatives...");
            
            // Try secondary selector
            try {
                submitBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("createAccountBtn")));
            } catch (TimeoutException e2) {
                // Try XPath
                submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(), 'Create') or contains(text(), 'Register')]")
                ));
            }
        }
        
        if (submitBtn != null) {
            // Scroll into view
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
```

### Debugging Tips:
1. **Check Element ID in Browser DevTools**: Open browser console and check:
   ```javascript
   document.getElementById("register-submit")
   document.getElementById("createAccountBtn")
   ```

2. **List All Buttons on Page**:
   ```javascript
   Array.from(document.querySelectorAll("button")).map(b => b.id + " : " + b.textContent)
   ```

3. **Add Logging in Your Step**:
   ```java
   // Get all buttons on page
   List<WebElement> buttons = driver.findElements(By.tagName("button"));
   System.out.println("Found " + buttons.size() + " buttons on page");
   
   buttons.forEach(btn -> {
       System.out.println("  - ID: " + btn.getAttribute("id") + 
                         ", Text: " + btn.getText());
   });
   ```

---

## Issue 4: Password Field Issues
### Common Problems:
- ❌ Password field not clearing properly
- ❌ Password field not accepting input
- ❌ Confirm password doesn't match

### Solutions:

#### Better Password Handling:
```java
@When("^the admin enter valid first password (.+)$")
public void the_admin_enter_valid_first_password(String fpassword) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-password")));
    
    // Clear field properly
    el.clear();
    Thread.sleep(100); // Short delay
    
    // Optional: Select all and delete
    el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
    el.sendKeys(Keys.DELETE);
    
    // Now enter password
    el.sendKeys(fpassword);
    
    // Store for verification
    this.currentPassword = fpassword;
    
    System.out.println("[DEBUG] Password entered: " + fpassword);
}

@When("^the admin enter valid confirm password (.+)$")
public void the_admin_enter_valid_confirm_password(String confirmpassword) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-confirmPassword")));
    
    // Clear properly
    el.clear();
    Thread.sleep(100);
    
    // Enter confirm password
    el.sendKeys(confirmpassword);
    
    // Store for verification
    this.currentConfirmPassword = confirmpassword;
    
    System.out.println("[DEBUG] Confirm password entered: " + confirmpassword);
}
```

#### Verify Passwords Match:
```java
@When("the confirm password is the same as the password")
public void the_confirm_password_is_the_same_as_the_password() {
    System.out.println("[DEBUG] Verifying passwords match...");
    
    // Check stored values
    System.out.println("[DEBUG] Stored password: " + this.currentPassword);
    System.out.println("[DEBUG] Stored confirm password: " + this.currentConfirmPassword);
    
    // Check UI values
    String passwordUI = driver.findElement(By.id("register-password")).getAttribute("value");
    String confirmPasswordUI = driver.findElement(By.id("register-confirmPassword")).getAttribute("value");
    
    System.out.println("[DEBUG] Password from UI: " + passwordUI);
    System.out.println("[DEBUG] Confirm password from UI: " + confirmPasswordUI);
    
    // Assertions
    Assert.assertEquals(this.currentConfirmPassword, this.currentPassword, 
        "Stored passwords don't match");
    Assert.assertEquals(confirmPasswordUI, passwordUI, 
        "UI passwords don't match");
    
    System.out.println("[DEBUG] ✓ Passwords match!");
}
```

---

## Issue 5: Email Already Registered
### Error Message:
```
Email already registered
```

### Root Cause:
Same email address is being used repeatedly

### Solution:
✅ Use EmailGenerator to create unique emails:

```java
// BEFORE - Hardcoded email (fails on 2nd run)
String email = "john103@gmail.com";

// AFTER - Unique generated email (works every time)
String email = EmailGenerator.generateEmailWithTimestamp();
// testuser_20260302_215343@gmail.com - unique each time!
```

---

## Issue 6: Chrome Driver Version Mismatch
### Warning Message:
```
WARNING: Unable to find an exact match for CDP version 145, 
returning the closest version; found: 144
```

### Solution:
This is just a warning. Update Selenium version if needed:

```xml
<!-- In pom.xml, update version to latest -->
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.40.0</version>
    <scope>test</scope>
</dependency>
```

Or download matching ChromeDriver version from: https://chromedriver.chromium.org/

---

## General Debugging Techniques

### 1. Add Screenshot on Failure
```java
public static void takeScreenshot(WebDriver driver, String filename) {
    File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
    try {
        FileUtils.copyFile(src, new File("screenshots/" + filename + ".png"));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

// Use in steps:
try {
    // ... step code ...
} catch (Exception e) {
    takeScreenshot(driver, "failure_screenshot");
    throw e;
}
```

### 2. Print Page Source for Debugging
```java
public void printPageSource(WebDriver driver) {
    String pageSource = driver.getPageSource();
    System.out.println("=== PAGE SOURCE ===");
    System.out.println(pageSource);
    System.out.println("=== END PAGE SOURCE ===");
}
```

### 3. Wait for JavaScript to Complete
```java
public void waitForJSExecution(WebDriver driver) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(webDriver -> 
        (Boolean) ((JavascriptExecutor) webDriver)
            .executeScript("return document.readyState")
            .equals("complete")
    );
}
```

### 4. Add Implicit Waits
```java
public Registration(WebDriver driver) {
    this.driver = driver;
    // Add implicit wait
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
}
```

---

## Checklist Before Running Tests

- [ ] EmailGenerator is imported in Registration.java
- [ ] Step definitions use regex patterns: `@When("^pattern (.+)$")`
- [ ] Feature file uses proper format: `When step description <parameter>`
- [ ] All element IDs are correct (verify in browser DevTools)
- [ ] WebDriverWait is used for all element interactions
- [ ] Passwords are stored in instance variables for verification
- [ ] Chrome browser is installed and updated
- [ ] ChromeDriver version matches browser version
- [ ] Project compiles without errors: `mvn clean compile test-compile`

---

## Still Having Issues?

1. **Check Console Output**: Look for `[DEBUG]` messages
2. **Enable Logging**: Add `System.out.println()` statements
3. **Verify HTML Elements**: Open browser DevTools and inspect element IDs
4. **Check Feature File**: Ensure syntax is correct
5. **Test One Step at a Time**: Comment out steps and test individually

---

## Need More Help?

Refer to:
- `EMAIL_GENERATION_GUIDE.md` - Complete email generation guide
- `QUICK_START_EMAILGEN.md` - Quick start examples
- `Registration.java` - Updated step definitions with fixes
- `EmailGenerator.java` - All email generation methods

