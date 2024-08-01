package src.ui;

import src.model.Patient;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class LifePrognosisUI {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@(.+)$"
    );

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$"
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Life Prognosis Management");
        System.out.println("Please enter patient details:");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        while (!isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email:");
            email = scanner.nextLine().trim();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        while (!isValidPassword(password)) {
            System.out.println("Password must be at least 8 characters long, include a special character, a capital letter, a small letter, and a number. Please enter a strong password:");
            password = scanner.nextLine().trim();
        }

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dateOfBirthStr = scanner.nextLine().trim();
        while (!isValidDate(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
            dateOfBirthStr = scanner.nextLine().trim();
        }

        System.out.print("Has HIV (true/false): ");
        boolean hasHIV = scanner.nextBoolean();

        String diagnosisDateStr = "";
        boolean onART = false;
        String artStartDateStr = "";
        scanner.nextLine(); // Consume newline left-over

        if (hasHIV) {
            System.out.print("Diagnosis Date (YYYY-MM-DD): ");
            diagnosisDateStr = scanner.nextLine().trim();
            while (!isValidDate(diagnosisDateStr)) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
                diagnosisDateStr = scanner.nextLine().trim();
            }

            System.out.print("On ART (true/false): ");
            onART = scanner.nextBoolean();
            scanner.nextLine(); // Consume newline left-over

            if (onART) {
                System.out.print("ART Start Date (YYYY-MM-DD): ");
                artStartDateStr = scanner.nextLine().trim();
                while (!isValidDate(artStartDateStr)) {
                    System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
                    artStartDateStr = scanner.nextLine().trim();
                }
            }
        }

        System.out.print("Country ISO Code: ");
        String countryISOCode = scanner.nextLine().trim();

        // Create Patient object
        Patient patient = new Patient(firstName, lastName, email, password,
                dateOfBirthStr, hasHIV, diagnosisDateStr,
                onART, artStartDateStr, countryISOCode);

        // Display patient details
        System.out.println("\nPatient Details:");
        System.out.println(patient);

        // Calculate and display survival rate
        double survivalRate = patient.computeSurvivalRate();
        System.out.println("\nSurvival Rate: " + survivalRate + " years");

        scanner.close();
    }

    private static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private static boolean isValidPassword(String password) {
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    private static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
