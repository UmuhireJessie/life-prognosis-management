package src.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Patient extends User {
    private LocalDate dateOfBirth;
    private boolean hasHIV;
    private LocalDate diagnosisDate; // Nullable
    private boolean onART;
    private LocalDate artStartDate; // Nullable
    private String countryISOCode;

    private static Map<String, Double> lifeExpectancyMap = new HashMap<>();

    static {
        // Load the data from the environment variable
        String lifeExpectancyData = System.getenv("LIFE_EXPECTANCY_DATA");
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

    @Override
    public void displayOptions() {
        System.out.println("Patient Options:");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Download Your Info");
        System.out.println("4. Logout");
    }

    public void viewProfile() {
        System.out.println(this.toString());
        System.out.println("Estimated Survival Rate: " + this.computeSurvivalRate() + " years");
    }

    public void updateProfile(Scanner scanner) {
        System.out.println("Updating profile. Press Enter to keep current values.");
        
        System.out.print("First Name [" + this.firstName + "]: ");
        String input = scanner.nextLine().trim();
        if (!input.isEmpty()) this.firstName = input;

        System.out.print("Last Name [" + this.lastName + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) this.lastName = input;

        System.out.print("Date of Birth [" + this.dateOfBirth + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) this.dateOfBirth = LocalDate.parse(input);

        System.out.print("Has HIV [" + this.hasHIV + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) this.hasHIV = Boolean.parseBoolean(input);

        if (this.hasHIV) {
            System.out.print("Diagnosis Date [" + this.diagnosisDate + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) this.diagnosisDate = LocalDate.parse(input);

            System.out.print("On ART [" + this.onART + "]: ");
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) this.onART = Boolean.parseBoolean(input);

            if (this.onART) {
                System.out.print("ART Start Date [" + this.artStartDate + "]: ");
                input = scanner.nextLine().trim();
                if (!input.isEmpty()) this.artStartDate = LocalDate.parse(input);
            }
        }

        System.out.print("Country ISO Code [" + this.countryISOCode + "]: ");
        input = scanner.nextLine().trim();
        if (!input.isEmpty()) this.countryISOCode = input;

        updateUserStore(this);
    }

    public void downloadInfo() {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "download_patient_info", this.email);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUserStore(Patient patient) {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "update_patient", 
                patient.email, patient.firstName, patient.lastName, patient.dateOfBirth.toString(),
                String.valueOf(patient.hasHIV), patient.diagnosisDate != null ? patient.diagnosisDate.toString() : "",
                String.valueOf(patient.onART), patient.artStartDate != null ? patient.artStartDate.toString() : "",
                patient.countryISOCode);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
