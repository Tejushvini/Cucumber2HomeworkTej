# Quick Start: Using EmailGenerator in Your Tests

## 1. Simple Usage Examples

### Example 1: Generate Email with Timestamp (Most Common)
```java
import utils.EmailGenerator;

// In your step definition
@When("the admin enters valid email")
public void the_admin_enters_valid_email() {
    String uniqueEmail = EmailGenerator.generateEmailWithTimestamp();
    // Example output: testuser_20260302_215343@gmail.com
    
    WebElement emailField = driver.findElement(By.id("register-email"));
    emailField.clear();
    emailField.sendKeys(uniqueEmail);
    
    System.out.println("✓ Email entered: " + uniqueEmail);
}
```

### Example 2: Generate Email with User's Name
```java
@When("^the admin enters valid email for (.+)$")
public void the_admin_enters_valid_email_for_user(String firstName) {
    String uniqueEmail = EmailGenerator.generateEmailWithCustomPrefix(firstName.toLowerCase());
    // Example output: john_20260302_215343@gmail.com
    
    WebElement emailField = driver.findElement(By.id("register-email"));
    emailField.clear();
    emailField.sendKeys(uniqueEmail);
    
    System.out.println("✓ Email entered for " + firstName + ": " + uniqueEmail);
}
```

### Example 3: Use Sequential Emails for Batch Testing
```java
@Given("the system has generated {int} test users")
public void generate_test_users(int count) {
    for (int i = 1; i <= count; i++) {
        String email = EmailGenerator.generateSequentialEmail(i);
        // testuser_001@gmail.com
        // testuser_002@gmail.com
        // testuser_003@gmail.com
        System.out.println("Test user " + i + ": " + email);
    }
}
```

---

## 2. Update Your Feature File

### Current Registration Feature
```gherkin
@Registration
Feature: User Registration
  
  Scenario Outline: Successful registration of new user with valid details
    Given the admin is on the login page
    When the admin click the sign up link
    Then the admin is on the registration page
    When the admin enters valid firstname <firstname>
    And the admin enters valid lastname <lastname>
    And the admin enters valid email <email>
    And the admin enter valid first password <fpassword>
    And the admin enter valid confirm password <confirmpassword>
    And the confirm password is the same as the password
    And the admin selects the correct group <group>
    When the admin click the create account button
    Then the successful registration message is displayed

    Examples:
      | firstname | lastname | email               | fpassword  | confirmpassword | group             |
      | John      | Doe      | john103@gmail.com   | @123456789 | @123456789      | Group T (2026)    |
```

### Improved Approach: Use AUTO_GENERATE
```gherkin
@Registration
Feature: User Registration
  
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

### Updated Step Definition
```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Generate unique email if AUTO_GENERATE is specified
    String emailToUse = email.equals("AUTO_GENERATE") 
        ? EmailGenerator.generateEmailWithTimestamp()
        : email;
    
    el.clear();
    el.sendKeys(emailToUse);
    
    System.out.println("✓ Email entered: " + emailToUse);
}
```

---

## 3. Generate and Store Email for Later Use

```java
public class Registration {
    private WebDriver driver;
    private String generatedEmail;  // Store generated email
    
    @When("the admin enters valid email AUTO_GENERATE")
    public void the_admin_enters_valid_email_auto_generate() {
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
    
    @Then("the successful registration message is displayed")
    public void the_successful_registration_message_is_displayed() {
        // ... verify registration ...
        
        // Email can be used later for login/verification tests
        System.out.println("Registered user email: " + this.generatedEmail);
    }
}
```

---

## 4. All Available Methods

### Method 1: Timestamp-Based (RECOMMENDED)
```java
String email = EmailGenerator.generateEmailWithTimestamp();
// Output: testuser_20260302_215343@gmail.com
// Use: Sequential tests, readable unique emails
```

### Method 2: UUID-Based
```java
String email = EmailGenerator.generateEmailWithUUID();
// Output: testuser_a1b2c3d4@gmail.com
// Use: Highly random, parallel execution
```

### Method 3: Random Numbers
```java
String email = EmailGenerator.generateEmailWithRandomNumbers();
// Output: testuser1234567890123@gmail.com
// Use: Simple and fast
```

### Method 4: Custom Prefix (VERY USEFUL)
```java
String email = EmailGenerator.generateEmailWithCustomPrefix("john");
// Output: john_20260302_215343@gmail.com
// Use: User-based testing, easy to identify in logs
```

### Method 5: Temporary Email
```java
String email = EmailGenerator.generateTempEmail();
// Output: testuser_20260302_215343@tempmail.com
// Use: Throwaway email testing
```

### Method 6: Sequential
```java
String email = EmailGenerator.generateSequentialEmail(1);
// Output: testuser_001@gmail.com
// Use: Batch testing with multiple users
```

---

## 5. Test Data Examples

### Run 1 - Automatically Generated Emails
```
Test Run Date: 2026-03-02 21:53:43
Generated Emails:
  - testuser_20260302_215343@gmail.com (John Doe)
  - testuser_20260302_215344@gmail.com (Alice Smith)
  - testuser_20260302_215345@gmail.com (Bob Johnson)
```

### Run 2 - Custom Prefix (More Readable)
```
Generated Emails:
  - john_20260302_215343@gmail.com
  - alice_20260302_215344@gmail.com
  - bob_20260302_215345@gmail.com
```

### Run 3 - UUID-Based (Parallel Safe)
```
Generated Emails:
  - testuser_a1b2c3d4@gmail.com
  - testuser_e5f6g7h8@gmail.com
  - testuser_i9j0k1l2@gmail.com
```

---

## 6. Copy-Paste Ready Solutions

### Solution 1: Update Your Current Test
Replace your hardcoded email `john103@gmail.com` with:

```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Use generated email instead of hardcoded
    String emailToUse = EmailGenerator.generateEmailWithTimestamp();
    
    el.clear();
    el.sendKeys(emailToUse);
    
    System.out.println("✓ Email entered: " + emailToUse);
}
```

Update feature file:
```gherkin
Examples:
  | firstname | lastname | email | fpassword  | confirmpassword | group             |
  | John      | Doe      | any   | @123456789 | @123456789      | Group T (2026)    |
```

### Solution 2: Keep Feature File As-Is, Override in Code
```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // Always generate new email, ignore the one from feature file
    String emailToUse = EmailGenerator.generateEmailWithTimestamp();
    
    el.clear();
    el.sendKeys(emailToUse);
}
```

### Solution 3: Add Debug Logging
```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    String emailToUse = EmailGenerator.generateEmailWithTimestamp();
    
    el.clear();
    el.sendKeys(emailToUse);
    
    // Debug logging
    System.out.println("─────────────────────────────────────");
    System.out.println("Email Generation Debug Info:");
    System.out.println("  Feature file email: " + email);
    System.out.println("  Generated email: " + emailToUse);
    System.out.println("  Email valid: " + EmailGenerator.isValidEmail(emailToUse));
    System.out.println("─────────────────────────────────────");
}
```

---

## 7. Running Your Tests

```bash
# Clean and compile
mvn clean compile test-compile

# Run specific test
mvn test -Dtest=TestRunner

# Run with logging
mvn test -X
```

---

## Summary

✅ **Before**: Hardcoded email `john103@gmail.com`
- ❌ Can only run once (email already exists)
- ❌ Failed parallel execution
- ❌ Same test data every time

✅ **After**: Generated email `testuser_20260302_215343@gmail.com`
- ✅ Unique every time
- ✅ Can run multiple times
- ✅ Supports parallel execution
- ✅ Easy to track in logs

**Next Steps**: 
1. Use `EmailGenerator.generateEmailWithTimestamp()` in your step definitions
2. Update your feature file to use AUTO_GENERATE or remove the hardcoded email
3. Run tests - they will automatically generate unique emails!

---

## Questions?

Refer to `EMAIL_GENERATION_GUIDE.md` for complete documentation!

