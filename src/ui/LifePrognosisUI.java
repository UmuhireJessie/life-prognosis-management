package src.ui;

import java.util.Scanner;
import src.Main;
import src.model.Patient;
import src.utils.Helper;
import java.io.Console;

public class LifePrognosisUI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        String email = args[0];
        String uuid = args[1];

        System.out.println("\nWelcome to Life Prognosis Management");
        System.out.println("Please enter patient details:\n");
        
        System.out.println("UUID: " + uuid);
        System.out.println("Email: " + email);

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        char[] passwordArray = console.readPassword("Password: ");
        String password = new String(passwordArray);

        while (!Helper.isValidPassword(password)) {
            System.out.println("Password must be at least 8 characters long, include a special character, a capital letter, a small letter, and a number. Please enter a strong password:");
            password = scanner.nextLine().trim();
        }

        System.out.print("Date of Birth (YYYY-MM-DD): ");
        String dateOfBirthStr = scanner.nextLine().trim();
        while (!Helper.isValidDate(dateOfBirthStr)) {
            System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
            dateOfBirthStr = scanner.nextLine().trim();
        }

        System.out.print("Has HIV (true/false): ");
        boolean hasHIV = Helper.getValidBooleanInput(scanner);

        String diagnosisDateStr = "";
        boolean onART = false;
        String artStartDateStr = "";
        scanner.nextLine(); // Consume newline left-over

        if (hasHIV) {
            System.out.print("Diagnosis Date (YYYY-MM-DD): ");
            diagnosisDateStr = scanner.nextLine().trim();
            while (!Helper.isValidDate(diagnosisDateStr)) {
                System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
                diagnosisDateStr = scanner.nextLine().trim();
            }

            System.out.print("On ART (true/false): ");
            onART = Helper.getValidBooleanInput(scanner);
            scanner.nextLine(); // Consume newline left-over

            if (onART) {
                System.out.print("ART Start Date (YYYY-MM-DD): ");
                artStartDateStr = scanner.nextLine().trim();
                while (!Helper.isValidDate(artStartDateStr)) {
                    System.out.println("Invalid date format. Please enter date in YYYY-MM-DD format:");
                    artStartDateStr = scanner.nextLine().trim();
                }
            }
        }

        System.out.print("Country ISO Code: ");
        String countryISOCode = scanner.nextLine().trim();
        String[] args1 = {uuid, firstName, lastName, password,
            dateOfBirthStr, String.valueOf(hasHIV), diagnosisDateStr,
            String.valueOf(onART), artStartDateStr, countryISOCode};
        String result = Main.callBashFunction("complete_registration", args1);
        System.out.println(result);
        
        // Create Patient object
        Patient patient = new Patient(firstName, lastName, email, password,
                dateOfBirthStr, hasHIV, diagnosisDateStr,
                onART, artStartDateStr, countryISOCode, uuid);

        // Display patient details
        System.out.println("\nPatient Details:");
        System.out.println(patient);

        // Calculate and display survival rate
        double survivalRate = patient.computeSurvivalRate();
        System.out.println("\nSurvival Rate: " + survivalRate + " years");

        // Display options for the patient
        patient.displayOptions();
    }
}
