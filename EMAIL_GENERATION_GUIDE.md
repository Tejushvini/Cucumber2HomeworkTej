# Test Email Generation Guide

## Overview
This guide explains how to generate unique test email IDs for registration testing using the `EmailGenerator` utility class in your Cucumber test suite.

## Why Test Email Generation?

When automating registration tests, you need unique email addresses to:
- Avoid "email already exists" validation errors
- Prevent test failures due to duplicate email registrations
- Enable parallel test execution
- Create realistic test scenarios with different users

## Available Email Generation Methods

### 1. **Timestamp-Based Email** (Recommended)
Generates unique emails using the current timestamp.

```java
String email = EmailGenerator.generateEmailWithTimestamp();
// Output: testuser_20260302_085930@gmail.com
```

**Use Case**: Best for sequential testing where you want easily readable unique emails.

---

### 2. **UUID-Based Email**
Generates unique emails using UUID (8 characters).

```java
String email = EmailGenerator.generateEmailWithUUID();
// Output: testuser_a1b2c3d4@gmail.com
```

**Use Case**: Highly random and unique, good for parallel execution.

---

### 3. **Random Numbers Email**
Generates emails using random numbers.

```java
String email = EmailGenerator.generateEmailWithRandomNumbers();
// Output: testuser123456789@gmail.com
```

**Use Case**: Simple and fast generation.

---

### 4. **Custom Prefix Email** (Very Useful)
Generates emails with a custom prefix for better organization.

```java
String email = EmailGenerator.generateEmailWithCustomPrefix("john");
// Output: john_20260302_085930@gmail.com

String email = EmailGenerator.generateEmailWithCustomPrefix("alice");
// Output: alice_20260302_085930@gmail.com
```

**Use Case**: Perfect for tests where you want emails based on user names (john, alice, bob, etc.).

---

### 5. **Temporary Email**
Generates emails using a temporary email domain.

```java
String email = EmailGenerator.generateTempEmail();
// Output: testuser_20260302_085930@tempmail.com
```

**Use Case**: When testing with disposable email services.

---

### 6. **Sequential Email**
Generates emails in sequence.

```java
String email1 = EmailGenerator.generateSequentialEmail(1);
// Output: testuser_001@gmail.com

String email2 = EmailGenerator.generateSequentialEmail(2);
// Output: testuser_002@gmail.com
```

**Use Case**: Batch testing with multiple users.

---

## Integration with Cucumber Scenarios

### Option 1: Update Feature File to Use Data Table

```gherkin
Scenario: Register user with generated email
  Given the admin is on the login page
  When the admin click the sign up link
  Then the admin is on the registration page
  When the admin enters valid firstname John
  And the admin enters valid lastname Doe
  And the admin enters valid email <generated_email>
  And the admin enter valid first password @123456789
  And the admin enter valid confirm password @123456789
  And the confirm password is the same as the password
  And the admin selects the correct group Group T (2026)
  When the admin click the create account button
  Then the successful registration message is displayed
```

### Option 2: Generate Email Inside Step Definition

Update your step definition to generate an email dynamically:

```java
@When("the admin enters a unique email")
public void the_admin_enters_unique_email() {
    // Generate a unique email using custom prefix
    String uniqueEmail = EmailGenerator.generateEmailWithCustomPrefix("testuser");
    
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    emailField.clear();
    emailField.sendKeys(uniqueEmail);
    
    // Store the email for later verification
    System.out.println("Generated email: " + uniqueEmail);
}
```

### Option 3: Use Scenario Context to Share Generated Email

```java
public class Registration {
    private WebDriver driver;
    private String generatedEmail;

    @When("the admin generates and enters a unique email")
    public void the_admin_generates_unique_email() {
        // Generate email with first name as prefix
        this.generatedEmail = EmailGenerator.generateEmailWithCustomPrefix("john");
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailField = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
        emailField.clear();
        emailField.sendKeys(this.generatedEmail);
        
        System.out.println("✓ Generated and entered email: " + this.generatedEmail);
    }

    @Then("verify the registration email was {}")
    public void verify_registration_email(String emailToVerify) {
        Assert.assertEquals(this.generatedEmail, emailToVerify, 
            "Email mismatch in registration");
    }
}
```

---

## Current Integration in Your Tests

Your project already has the `EmailGenerator` utility available. Here's how to use it in your `Registration.java`:

### Current Test Data
```gherkin
Examples:
  | firstname | lastname | email               | fpassword  | confirmpassword | group             |
  | John      | Doe      | john103@gmail.com   | @123456789 | @123456789      | Group T (2026)    |
```

### Updated Approach with Email Generation

Instead of hardcoding `john103@gmail.com`, modify the step definition:

```java
@When("^the admin enters valid email (.+)$")
public void the_admin_enters_valid_email(String email) {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.id("register-email")));
    
    // If email is "AUTO_GENERATE", generate a unique one
    String emailToUse = email.equals("AUTO_GENERATE") 
        ? EmailGenerator.generateEmailWithTimestamp()
        : email;
    
    el.clear();
    el.sendKeys(emailToUse);
    
    System.out.println("✓ Email entered: " + emailToUse);
}
```

Then update your Feature file:
```gherkin
Examples:
  | firstname | lastname | email           | fpassword  | confirmpassword | group             |
  | John      | Doe      | AUTO_GENERATE   | @123456789 | @123456789      | Group T (2026)    |
```

---

## Email Validation

Validate email format before sending:

```java
String email = EmailGenerator.generateEmailWithTimestamp();

if (EmailGenerator.isValidEmail(email)) {
    System.out.println("✓ Valid email: " + email);
} else {
    System.out.println("✗ Invalid email: " + email);
}
```

---

## Best Practices

1. **Use Timestamp Method for Sequential Tests**
   ```java
   EmailGenerator.generateEmailWithTimestamp()
   ```

2. **Use Custom Prefix for Test Traceability**
   ```java
   EmailGenerator.generateEmailWithCustomPrefix("john")
   // Email contains user's first name for easy identification
   ```

3. **Log Generated Emails for Debugging**
   ```java
   String email = EmailGenerator.generateEmailWithCustomPrefix("alice");
   System.out.println("Generated email for user: " + email);
   ```

4. **Reuse Generated Email Across Test Steps**
   ```java
   // Store in instance variable
   private String registeredEmail;
   
   @When("admin registers with unique email")
   public void admin_registers() {
       this.registeredEmail = EmailGenerator.generateEmailWithTimestamp();
       // ... use it
   }
   
   @Then("verify email in admin panel")
   public void verify_email() {
       // Can reference this.registeredEmail later
   }
   ```

5. **Use Temporary Email for Specific Testing**
   ```java
   EmailGenerator.generateTempEmail()
   // For testing with throwaway email addresses
   ```

---

## Example: Complete Test Scenario

```java
@When("the admin registers with a unique email")
public void admin_registers_with_unique_email() {
    // Generate unique email with first name as prefix
    String firstName = "John";
    String uniqueEmail = EmailGenerator.generateEmailWithCustomPrefix(firstName.toLowerCase());
    
    // Store for later verification
    this.generatedEmail = uniqueEmail;
    
    // Enter email in form
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement emailField = wait.until(
        ExpectedConditions.elementToBeClickable(By.id("register-email"))
    );
    emailField.clear();
    emailField.sendKeys(uniqueEmail);
    
    // Log for debugging
    System.out.println("✓ Registered with email: " + uniqueEmail);
}

@Then("the user should be able to login with the generated email")
public void verify_login_with_generated_email() {
    // Use this.generatedEmail to verify login works
    Assert.assertNotNull(this.generatedEmail, "Email was not generated");
    System.out.println("✓ Verified login email: " + this.generatedEmail);
}
```

---

## Running Tests with Email Generation

### From Command Line

Run your tests normally:
```bash
mvn test
```

The tests will automatically generate unique emails for each execution.

### For Parallel Execution

Since each generated email is unique based on timestamp/UUID, multiple parallel test runs won't conflict:

```bash
mvn test -DthreadCount=4 -DparalllelSuites=true
```

---

## Troubleshooting

### Issue: Email "already registered" error
**Solution**: Ensure you're using a unique email generation method:
```java
// BAD - Same email every time
String email = "test@gmail.com";

// GOOD - Unique every time
String email = EmailGenerator.generateEmailWithTimestamp();
```

### Issue: Email validation fails
**Solution**: Verify the generated email format:
```java
String email = EmailGenerator.generateEmailWithTimestamp();
if (!EmailGenerator.isValidEmail(email)) {
    System.out.println("Invalid format: " + email);
}
```

### Issue: Can't find generated email in reports
**Solution**: Log the generated email clearly:
```java
String email = EmailGenerator.generateEmailWithCustomPrefix("user");
System.out.println("✓ Generated email: " + email);
```

---

## Summary

| Method | Example Output | Best For |
|--------|----------------|----------|
| `generateEmailWithTimestamp()` | `testuser_20260302_085930@gmail.com` | Sequential testing |
| `generateEmailWithUUID()` | `testuser_a1b2c3d4@gmail.com` | Parallel execution |
| `generateEmailWithRandomNumbers()` | `testuser123456789@gmail.com` | Simple & fast |
| `generateEmailWithCustomPrefix()` | `john_20260302_085930@gmail.com` | User-based tracing |
| `generateTempEmail()` | `testuser_20260302_085930@tempmail.com` | Throwaway emails |
| `generateSequentialEmail()` | `testuser_001@gmail.com` | Batch testing |

Choose the method that best fits your testing needs!

