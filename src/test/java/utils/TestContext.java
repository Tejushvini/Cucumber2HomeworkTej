package utils;

/**
 * Shared context to store data across different scenarios
 * This uses a singleton pattern to share data between step definition instances
 */
public class TestContext {
    private static TestContext instance;

    private String generatedEmail;
    private String currentPassword;
    private String currentConfirmPassword;

    // Private constructor for singleton
    private TestContext() {
        this.generatedEmail = "";
        this.currentPassword = "";
        this.currentConfirmPassword = "";
    }

    // Get singleton instance
    public static TestContext getInstance() {
        if (instance == null) {
            instance = new TestContext();
        }
        return instance;
    }

    // Reset all data (call this before each test run if needed)
    public void reset() {
        this.generatedEmail = "";
        this.currentPassword = "";
        this.currentConfirmPassword = "";
    }

    // Getters and Setters
    public String getGeneratedEmail() {
        return generatedEmail;
    }

    public void setGeneratedEmail(String generatedEmail) {
        this.generatedEmail = generatedEmail;
        System.out.println("[TestContext] Stored email: " + generatedEmail);
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getCurrentConfirmPassword() {
        return currentConfirmPassword;
    }

    public void setCurrentConfirmPassword(String currentConfirmPassword) {
        this.currentConfirmPassword = currentConfirmPassword;
    }

    // Helper method to check if email is available
    public boolean hasGeneratedEmail() {
        return generatedEmail != null && !generatedEmail.isEmpty();
    }
}

