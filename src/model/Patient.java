package src.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import src.Main;
import src.ui.LifePrognosisUI;
import src.utils.Helper;

public class Patient extends User {
    private LocalDate dateOfBirth;
    private boolean hasHIV;
    private LocalDate diagnosisDate; // Nullable
    private boolean onART;
    private LocalDate artStartDate; // Nullable
    private String countryISOCode;

    private static Map<String, Double> lifeExpectancyMap = new HashMap<>();

    static {
        // Set the system property (acts like an environment variable within your Java application
        System.setProperty("LIFE_EXPECTANCY_DATA", "./src/data/life-expectancy.csv");

        // Now retrieve it using System.getProperty()
        String lifeExpectancyData = System.getProperty("LIFE_EXPECTANCY_DATA");
        if (lifeExpectancyData != null) {
            for (String entry : lifeExpectancyData.split(",")) {
                String[] keyValue = entry.split(":");
                if (keyValue.length == 2) {
                    lifeExpectancyMap.put(keyValue[0], Double.parseDouble(keyValue[1]));
                }
            }
        } else {
            System.out.println("No life expectancy data available.");
        }
    }

    public Patient(String firstName, String lastName, String email, String password,
            String dateOfBirth, boolean hasHIV, String diagnosisDate,
            boolean onART, String artStartDate, String countryISOCode) {
        super(firstName, lastName, email, password, Role.PATIENT);
        this.dateOfBirth = LocalDate.parse(dateOfBirth);
        this.hasHIV = hasHIV;
        this.diagnosisDate = diagnosisDate.isEmpty() ? null : LocalDate.parse(diagnosisDate);
        this.onART = onART;
        this.artStartDate = artStartDate.isEmpty() ? null : LocalDate.parse(artStartDate);
        this.countryISOCode = countryISOCode;
    }

    // Options that are only available to patients
    @Override
    public void displayOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPatients Options:");

        if (password == "") {
            System.out.println("1. Complete Registration");
            System.out.println("2. View Profile");
            System.out.println("3. Update Profile");
            System.out.println("4. Download Your Info");
            System.out.println("5. Get Life Prognosis");
            System.out.println("6. Logout");
        } else {
            System.out.println("1. View Profile");
            System.out.println("2. Update Profile");
            System.out.println("3. Download Your  Info");
            System.out.println("4. Get Life Prognosis");
            System.out.println("5. Logout");
        }

        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (password != "") {
            switch (choice) {
                case 1:
                    viewPatientProfile(scanner);
                    break;
                case 2:
                    updatePatientProfile(scanner);
                    break;
                case 5:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } else {
            switch (choice) {
                case 1:
                    completeRegistration(scanner);
                    break;
                case 2:
                    viewPatientProfile(scanner);
                    break;
                case 3:
                    updatePatientProfile(scanner);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }

        }

    }

    private void completeRegistration(Scanner scanner) {
        LifePrognosisUI.main(null);
    }

    private void viewPatientProfile(Scanner scanner) {
        try {
            // Call bash script to view patient profile
            String result = Main.callBashFunction("get_patient", email);
            System.out.println("Here is your information: " + result);

            displayOptions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePatientProfile(Scanner scanner) {
        try {
            // Assuming the email is already stored or retrieved earlier
            String email = this.email;

            System.out.println("Leave fields empty if you do not want to update them.");

            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine().trim();

            System.out.print("HIV Status (true/false): ");
            String hasHIV = scanner.nextLine().trim();

            System.out.print("Diagnosis Date (YYYY-MM-DD, optional): ");
            String diagnosisDate = scanner.nextLine().trim();

            System.out.print("On ART (true/false): ");
            String onART = scanner.nextLine().trim();

            System.out.print("ART Start Date (YYYY-MM-DD, optional): ");
            String artStartDate = scanner.nextLine().trim();

            System.out.print("Country ISO Code: ");
            String countryCode = scanner.nextLine().trim();

            // Prepare arguments
            List<String> bashArgs = new ArrayList<>();
            bashArgs.add(email);
            if (!firstName.isEmpty())
                bashArgs.add("first_name=" + firstName);
            if (!lastName.isEmpty())
                bashArgs.add("last_name=" + lastName);
            if (!dob.isEmpty())
                bashArgs.add("dob=" + dob);
            if (!hasHIV.isEmpty())
                bashArgs.add("has_hiv=" + hasHIV);
            if (!diagnosisDate.isEmpty())
                bashArgs.add("diagnosis_date=" + diagnosisDate);
            if (!onART.isEmpty())
                bashArgs.add("on_art=" + onART);
            if (!artStartDate.isEmpty())
                bashArgs.add("art_start_date=" + artStartDate);
            if (!countryCode.isEmpty())
                bashArgs.add("country_code=" + countryCode);

            // Convert the list to an array and pass it to the Bash function
            String[] argsArray = bashArgs.toArray(new String[0]);
            System.out.println(argsArray[0] + argsArray[1]);
            String result = Main.callBashFunction("update_patient_data", argsArray);
            System.out.println(result);

            displayOptions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // private void updatePatientProfile(Scanner scanner) {
    // try {
    // System.out.print("Email: ");
    // String email = scanner.nextLine().trim();
    // while (!Helper.isValidEmail(email)) {
    // System.out.println("Invalid email format. Please enter a valid email:");
    // email = scanner.nextLine().trim();
    // }

    // // Call bash script to view patient profile
    // String result = Main.callBashFunction("update_patient_data", email);
    // System.out.println(result);

    // displayOptions();

    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    public double computeSurvivalRate() {
        if (!hasHIV) {
            return getAverageLifespanByCountry(countryISOCode);
        }

        double averageLifespan = getAverageLifespanByCountry(countryISOCode);
        long ageAtDiagnosis = ChronoUnit.YEARS.between(dateOfBirth, diagnosisDate);
        long yearsSinceDiagnosis = ChronoUnit.YEARS.between(diagnosisDate, LocalDate.now());

        if (!onART) {
            return ageAtDiagnosis + 5;
        }

        double remainingLifespan = (averageLifespan - ageAtDiagnosis) * 0.9;
        for (int i = 1; i <= yearsSinceDiagnosis; i++) {
            if (i > 1) {
                remainingLifespan *= 0.9;
            }
        }

        return ageAtDiagnosis + remainingLifespan;
    }

    public static double getAverageLifespanByCountry(String countryISOCode) {
        return lifeExpectancyMap.getOrDefault(countryISOCode, 75.0);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", hasHIV=" + hasHIV +
                ", diagnosisDate=" + diagnosisDate +
                ", onART=" + onART +
                ", artStartDate=" + artStartDate +
                ", countryISOCode='" + countryISOCode + '\'' +
                '}';
    }
}
