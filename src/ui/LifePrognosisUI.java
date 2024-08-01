package src.ui;

import src.model.Patient;

import java.util.Scanner;

public class LifePrognosisUI {

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

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dateOfBirthStr = scanner.nextLine().trim();

        System.out.print("Has HIV (true/false): ");
        boolean hasHIV = scanner.nextBoolean();

        scanner.nextLine(); // Consume newline left-over
        System.out.print("Diagnosis Date (YYYY-MM-DD): ");
        String diagnosisDateStr = scanner.nextLine().trim();

        System.out.print("On ART (true/false): ");
        boolean onART = scanner.nextBoolean();

        scanner.nextLine(); // Consume newline left-over
        System.out.print("ART Start Date (YYYY-MM-DD): ");
        String artStartDateStr = scanner.nextLine().trim();

        System.out.print("Country ISO Code: ");
        String countryISOCode = scanner.nextLine().trim();

        // Create Patient object
        Patient patient = new Patient(firstName, lastName, email, password,
                dateOfBirthStr, hasHIV, diagnosisDateStr, onART, artStartDateStr, countryISOCode);

        // Display patient details
        System.out.println("\nPatient Details:");
        System.out.println(patient);

        // Calculate and display survival rate
        double survivalRate = patient.computeSurvivalRate();
        System.out.println("\nSurvival Rate: " + survivalRate + " years");

        scanner.close();
    }
}
