package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.Random;

/**
 * Utility class to generate unique test email IDs for automation testing
 */
public class EmailGenerator {

    private static final String BASE_EMAIL_DOMAIN = "gmail.com";
    private static final String TEMP_EMAIL_DOMAIN = "tempmail.com";
    private static final Random random = new Random();

    /**
     * Generate a unique email using timestamp
     * Format: testuser_yyyyMMdd_HHmmss@gmail.com
     * Example: testuser_20260302_085930@gmail.com
     *
     * @return Unique email address
     */
    public static String generateEmailWithTimestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        return "testuser_" + timestamp + "@" + BASE_EMAIL_DOMAIN;
    }

    /**
     * Generate a unique email using UUID
     * Format: testuser_[uuid-first-8-chars]@gmail.com
     * Example: testuser_a1b2c3d4@gmail.com
     *
     * @return Unique email address
     */
    public static String generateEmailWithUUID() {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return "testuser_" + uuid + "@" + BASE_EMAIL_DOMAIN;
    }

    /**
     * Generate a unique email using random numbers
     * Format: testuser[randomnumber]@gmail.com
     * Example: testuser123456789@gmail.com
     *
     * @return Unique email address
     */
    public static String generateEmailWithRandomNumbers() {
        long randomNum = System.currentTimeMillis() + random.nextInt(1000);
        return "testuser" + randomNum + "@" + BASE_EMAIL_DOMAIN;
    }

    /**
     * Generate email with custom prefix
     * Format: [prefix]_[timestamp]@gmail.com
     * Example: john_20260302_085930@gmail.com
     *
     * @param prefix Custom prefix for email
     * @return Unique email address
     */
    public static String generateEmailWithCustomPrefix(String prefix) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        return prefix + "_" + timestamp + "@" + BASE_EMAIL_DOMAIN;
    }

    /**
     * Generate email using temporary email domain (for testing purposes)
     * Format: testuser_[timestamp]@tempmail.com
     *
     * @return Unique temporary email address
     */
    public static String generateTempEmail() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String timestamp = now.format(formatter);
        return "testuser_" + timestamp + "@" + TEMP_EMAIL_DOMAIN;
    }

    /**
     * Generate sequential emails for batch testing
     * Example: testuser_001@gmail.com, testuser_002@gmail.com, etc.
     *
     * @param index Sequential number
     * @return Sequential email address
     */
    public static String generateSequentialEmail(int index) {
        return String.format("testuser_%03d@%s", index, BASE_EMAIL_DOMAIN);
    }

    /**
     * Validate email format (basic validation)
     *
     * @param email Email address to validate
     * @return true if email is valid format, false otherwise
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email != null && email.matches(emailRegex);
    }

    // Example usage and testing
    public static void main(String[] args) {
        System.out.println("=== Test Email Generation Examples ===\n");

        System.out.println("1. Email with Timestamp:");
        System.out.println("   " + generateEmailWithTimestamp());

        System.out.println("\n2. Email with UUID:");
        System.out.println("   " + generateEmailWithUUID());

        System.out.println("\n3. Email with Random Numbers:");
        System.out.println("   " + generateEmailWithRandomNumbers());

        System.out.println("\n4. Email with Custom Prefix (john):");
        System.out.println("   " + generateEmailWithCustomPrefix("john"));

        System.out.println("\n5. Temporary Email:");
        System.out.println("   " + generateTempEmail());

        System.out.println("\n6. Sequential Emails:");
        for (int i = 1; i <= 5; i++) {
            System.out.println("   " + generateSequentialEmail(i));
        }

        System.out.println("\n7. Email Validation Examples:");
        System.out.println("   john@gmail.com is valid: " + isValidEmail("john@gmail.com"));
        System.out.println("   invalid-email is valid: " + isValidEmail("invalid-email"));
    }
}

