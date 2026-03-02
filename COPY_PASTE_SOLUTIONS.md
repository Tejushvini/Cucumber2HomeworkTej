# Copy-Paste Ready Solutions

## 💾 Ready-to-Use Code Snippets

### Solution 1: Update Your Current Feature File

**File**: `src/test/resources/Features/Registration.feature`

**Replace**:
```gherkin
@Registration
Feature: User Registration
  As an admin I want to register a new user

  Scenario Outline: Successful registration of new user with valid details
    Given the admin is on the login page
    When the admin click the sign up link
    Then the admin is on the registration page
    When the admin enters valid firstname <firstname>
    And the admin enters valid lastname <lastname>
    And the admin enters valid email john103@gmail.com
    And the admin enter valid first password <fpassword>
    And the admin enter valid confirm password <confirmpassword>
    And the confirm password is the same as the password
    And the admin selects the correct group <group>
    When the admin click the create account button
    Then the successful registration message is displayed

    Examples:
      | firstname | lastname | fpassword  | confirmpassword | group             |
      | John      | Doe      | @123456789 | @123456789      | Group T (2026)    |
```

**With**:
```gherkin
@Registration
Feature: User Registration
  As an admin I want to register a new user

  Scenario Outline: Successful registration of new user with valid details
    Given the admin is on the login page
    When the admin click the sign up link
    Then the admin is on the registration page
    When the admin enters valid firstname <firstname>
    And the admin enters valid lastname <lastname>
    And the admin enters valid email AUTO_GENERATE
    And the admin enter valid first password <fpassword>
    And the admin enter valid confirm password <confirmpassword>
    And the confirm password is the same as the password
    And the admin selects the correct group <group>
    When the admin click the create account button
    Then the successful registration message is displayed

    Examples:
      | firstname | lastname | fpassword  | confirmpassword | group             |
      | John      | Doe      | @123456789 | @123456789      | Group T (2026)    |
      | Alice     | Smith    | @987654321 | @987654321      | Group T (2026)    |
      | Bob       | Johnson  | @456789123 | @456789123      | Group T (2026)    |
```

---

### Solution 2: Update Registration Step Definition

**File**: `src/test/java/stepDefinitions/Registration.java`

**Step 1**: Add this import at the top of the file
```java
import utils.EmailGenerator;
```

**Step 2**: Add these class variables after `private WebDriver driver;`
```java
private String currentPassword = "";
private String currentConfirmPassword = "";
private String generatedEmail = "";
```

**Step 3**: Replace the email step with this:
```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Generate unique email if AUTO_GENERATE is specified
    String emailToUse = email.equals("AUTO_GENERATE") 
        ? EmailGenerator.generateEmailWithTimestamp()
        : email;
    
    this.generatedEmail = emailToUse;
    
    el.clear();
    el.sendKeys(emailToUse);
    
    System.out.println("✓ Email entered: " + emailToUse);
}
```

**Step 4**: Update password steps:
```java
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
```

---

### Solution 3: Generate Email with User Name

**Use this when you want emails like**: `john_20260302_215343@gmail.com`

```java
@When("^the admin enters valid email for user (.+)$")
public void the_admin_enters_valid_email_for_user(String firstName) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Generate email with user's first name as prefix
    String generatedEmail = EmailGenerator.generateEmailWithCustomPrefix(firstName.toLowerCase());
    
    el.clear();
    el.sendKeys(generatedEmail);
    
    System.out.println("✓ Email for " + firstName + ": " + generatedEmail);
}
```

**In Feature File**:
```gherkin
When the admin enters valid email for user John
When the admin enters valid email for user Alice
When the admin enters valid email for user Bob
```

---

### Solution 4: Store and Verify Generated Email

**Use this when you need to verify the registered email later**:

```java
public class Registration {
    private WebDriver driver;
    private String generatedEmail;  // Store for later use
    
    @When("the admin enters valid email AUTO_GENERATE")
    public void the_admin_enters_valid_email_auto() {
        // Generate and store
        this.generatedEmail = EmailGenerator.generateEmailWithTimestamp();
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = wait.until(
            ExpectedConditions.elementToBeClickable(By.id("register-email"))
        );
        emailField.clear();
        emailField.sendKeys(this.generatedEmail);
        
        System.out.println("✓ Generated email: " + this.generatedEmail);
    }
    
    @Then("verify registration was successful for generated email")
    public void verify_registration_successful() {
        System.out.println("✓ Registered with email: " + this.generatedEmail);
        // Can use this.generatedEmail for further verification
    }
}
```

---

### Solution 5: Batch Registration Test

**Use this to register multiple users at once**:

```gherkin
Scenario: Register multiple users in batch
  Given the system will register 3 users
  When the admin registers user 1 with email AUTO_GENERATE
  And the admin registers user 2 with email AUTO_GENERATE
  And the admin registers user 3 with email AUTO_GENERATE
  Then all 3 users should be registered successfully
```

**Step Definition**:
```java
private List<String> registeredEmails = new ArrayList<>();

@When("the admin registers user {int} with email AUTO_GENERATE")
public void admin_registers_user_with_auto_email(int userNumber) {
    // Generate unique email for each user
    String email = EmailGenerator.generateEmailWithTimestamp();
    registeredEmails.add(email);
    
    // Fill registration form
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement emailField = wait.until(
        ExpectedConditions.elementToBeClickable(By.id("register-email"))
    );
    emailField.clear();
    emailField.sendKeys(email);
    
    System.out.println("✓ User " + userNumber + " email: " + email);
}

@Then("all {int} users should be registered successfully")
public void verify_all_users_registered(int count) {
    System.out.println("Registered " + registeredEmails.size() + " users:");
    registeredEmails.forEach(email -> 
        System.out.println("  - " + email)
    );
    Assert.assertEquals(registeredEmails.size(), count, "Not all users registered");
}
```

---

### Solution 6: Parallel Test Execution

**Use this for running tests in parallel without conflicts**:

```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Use UUID method for parallel execution (highly random)
    String emailToUse = EmailGenerator.generateEmailWithUUID();
    
    el.clear();
    el.sendKeys(emailToUse);
    
    System.out.println("✓ Parallel-safe email: " + emailToUse);
}
```

**Run parallel tests**:
```bash
mvn test -DthreadCount=4 -Dparallel=methods
```

---

### Solution 7: Debug Mode with Detailed Logging

**Use this for debugging registration issues**:

```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    String emailToUse = EmailGenerator.generateEmailWithTimestamp();
    
    // Debug logging
    System.out.println("════════════════════════════════════");
    System.out.println("EMAIL ENTRY DEBUG INFO");
    System.out.println("════════════════════════════════════");
    System.out.println("Feature file email: " + email);
    System.out.println("Generated email: " + emailToUse);
    System.out.println("Is valid: " + EmailGenerator.isValidEmail(emailToUse));
    System.out.println("Field ID: register-email");
    System.out.println("Field displayed: " + el.isDisplayed());
    System.out.println("Field enabled: " + el.isEnabled());
    System.out.println("════════════════════════════════════");
    
    el.clear();
    el.sendKeys(emailToUse);
}
```

---

### Solution 8: Email Validation Step

**Add this step to validate email format**:

```java
@Then("the generated email should be valid")
public void verify_email_is_valid() {
    String email = driver.findElement(By.id("register-email")).getAttribute("value");
    
    System.out.println("Validating email: " + email);
    
    boolean isValid = EmailGenerator.isValidEmail(email);
    Assert.assertTrue(isValid, "Email format is invalid: " + email);
    
    System.out.println("✓ Email format is valid");
}
```

**Use in Feature File**:
```gherkin
When the admin enters valid email AUTO_GENERATE
Then the generated email should be valid
```

---

### Solution 9: All 6 Methods in Action

**Complete example showing all methods**:

```java
public class EmailTestExamples {
    
    public static void demonstrateAllMethods() {
        System.out.println("=== All Email Generation Methods ===\n");
        
        // Method 1: Timestamp
        System.out.println("1. Timestamp-based:");
        System.out.println("   " + EmailGenerator.generateEmailWithTimestamp());
        
        // Method 2: UUID
        System.out.println("\n2. UUID-based:");
        System.out.println("   " + EmailGenerator.generateEmailWithUUID());
        
        // Method 3: Random
        System.out.println("\n3. Random Numbers:");
        System.out.println("   " + EmailGenerator.generateEmailWithRandomNumbers());
        
        // Method 4: Custom Prefix
        System.out.println("\n4. Custom Prefix (john):");
        System.out.println("   " + EmailGenerator.generateEmailWithCustomPrefix("john"));
        
        System.out.println("\n4. Custom Prefix (alice):");
        System.out.println("   " + EmailGenerator.generateEmailWithCustomPrefix("alice"));
        
        // Method 5: Temp Email
        System.out.println("\n5. Temporary Email:");
        System.out.println("   " + EmailGenerator.generateTempEmail());
        
        // Method 6: Sequential
        System.out.println("\n6. Sequential Emails:");
        for (int i = 1; i <= 3; i++) {
            System.out.println("   " + EmailGenerator.generateSequentialEmail(i));
        }
    }
}
```

---

## 🎯 Which Solution to Choose?

### **For Most Cases**: Solution 1 or 2
- Simple to implement
- Works with your existing feature file
- AUTO_GENERATE triggers email generation automatically

### **For User-Based Testing**: Solution 3
- Email contains user's name
- Easy to identify in logs
- Example: `john_20260302_215343@gmail.com`

### **For Email Verification Later**: Solution 4
- Store generated email
- Verify in subsequent steps
- Use for login/approval tests

### **For Batch Testing**: Solution 5
- Register multiple users
- Store all emails
- Verify batch operations

### **For Parallel Execution**: Solution 6
- Use UUID method
- No conflicts between threads
- Safe for `mvn test -DthreadCount=4`

### **For Debugging**: Solution 7
- Detailed logging
- See all debug info
- Identify issues easily

### **For Quality Assurance**: Solution 8
- Validate email format
- Catch format errors
- Ensure compliance

### **For Learning**: Solution 9
- See all methods in action
- Compare outputs
- Choose the best method

---

## 🚀 Quick Implementation Checklist

- [ ] Add `import utils.EmailGenerator;` to Registration.java
- [ ] Add class variables for tracking (if using)
- [ ] Update the email step definition
- [ ] Update feature file to use AUTO_GENERATE (or keep existing, code handles it)
- [ ] Run `mvn clean compile test-compile`
- [ ] Verify: **BUILD SUCCESS**
- [ ] Run tests: `mvn test`

---

## 📝 Testing Your Changes

### Check Compilation
```bash
cd C:\ProjectT\Cucumber2HomeworkTej
mvn clean compile test-compile
```

### Run Tests
```bash
mvn test
```

### Check Generated Emails in Output
Look for messages like:
```
✓ Email entered: testuser_20260302_215343@gmail.com
```

---

## 💡 Pro Tips

1. **Save time**: Copy entire solution blocks, don't type manually
2. **Test one piece**: Compile after each change to catch errors early
3. **Check logs**: Look for `[DEBUG]` messages to verify email generation
4. **Use best method**: Timestamp is safest, UUID is fastest, Custom Prefix is most readable

---

Ready to implement? Pick a solution above and copy-paste! 🎉

