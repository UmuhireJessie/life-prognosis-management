package src.ui;

import src.model.Patient;
import src.utils.Helper;

import java.util.Scanner;
import java.util.regex.Pattern;
import java.time.LocalDate;

public class LifePrognosisUI {

    public static void main(String[] args) {
        Patient patient = getUserData();
        //store them in the Patient_store.

        // Display patient details
        System.out.println("\nPatient Details:");
        System.out.println(patient);

        // Calculate and display survival rate
        double survivalRate = patient.computeSurvivalRate();
        System.out.println("\nSurvival Rate: " + survivalRate + " years");

        
    }
    public static Patient getUserData(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to Life Prognosis Management");
        System.out.println("Please enter patient details:");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        while (!Helper.isValidEmail(email)) {
            System.out.println("Invalid email format. Please enter a valid email:");
            email = scanner.nextLine().trim();
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
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
        boolean hasHIV = scanner.nextBoolean();

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
            onART = scanner.nextBoolean();
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
        scanner.close();
        Patient patient =new Patient(firstName, lastName, email, password,
        dateOfBirthStr, hasHIV, diagnosisDateStr,
        onART, artStartDateStr, countryISOCode);

        return  patient;
    }
}
