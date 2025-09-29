package com.example.demo.utils;

import java.security.SecureRandom;

/**
 * Utility class for generating various types of verification/confirmation codes.
 */
public class CodeGenerator {
    private static final String NUMBERS = "0123456789";
    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String ALPHANUMERIC = NUMBERS + LETTERS + LETTERS.toLowerCase();
    
    private static final SecureRandom RANDOM = new SecureRandom();

    private CodeGenerator() {
        // Private constructor to prevent instantiation
    }

    /**
     * Generates a numeric code of specified length.
     * @param length The length of the code to generate
     * @return The generated numeric code as a String
     */
    public static String generateNumericCode(int length) {
        return generateCode(length, NUMBERS);
    }

    /**
     * Generates an alphanumeric code of specified length.
     * @param length The length of the code to generate
     * @return The generated alphanumeric code as a String
     */
    public static String generateAlphanumericCode(int length) {
        return generateCode(length, ALPHANUMERIC);
    }

    /**
     * Generates a code using the specified character set.
     * @param length The length of the code to generate
     * @param charSet The character set to use for generating the code
     * @return The generated code as a String
     */
    public static String generateCode(int length, String charSet) {
        if (length <= 0) {
            throw new IllegalArgumentException("Code length must be greater than 0");
        }
        if (charSet == null || charSet.isEmpty()) {
            throw new IllegalArgumentException("Character set cannot be null or empty");
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(charSet.length());
            code.append(charSet.charAt(randomIndex));
        }
        return code.toString();
    }

    /**
     * Example usage of the CodeGenerator class
     */
    public static void main(String[] args) {
        System.out.println("=== CodeGenerator Demo ===");
        
        // Generate a 6-digit numeric code (common for SMS verification)
        String smsCode = CodeGenerator.generateNumericCode(6);
        System.out.println("6-digit SMS code: " + smsCode);
        
        // Generate an 8-character alphanumeric code (for password reset tokens, etc.)
        String resetToken = CodeGenerator.generateAlphanumericCode(8);
        System.out.println("8-char alphanumeric token: " + resetToken);
        
        // Generate a 10-character code with custom character set
        String customCode = CodeGenerator.generateCode(10, "!@#$%^&*");
        System.out.println("10-char custom code: " + customCode);
        
        // Generate multiple codes (e.g., for batch processing)
        System.out.println("\nGenerating 3 verification codes:");
        for (int i = 0; i < 3; i++) {
            System.out.println("Code " + (i+1) + ": " + CodeGenerator.generateNumericCode(6));
        }
    }
}
