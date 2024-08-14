package src.utils;

import java.time.LocalDate;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Helper {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$");

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean getValidBooleanInput(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
                return Boolean.parseBoolean(input);
            } else {
                System.out.println("--> Invalid input. Please enter either 'true' or 'false'.");
            }
        }
    }

    public static boolean isValidBooleanInput(String input) {
        return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
    }
}
