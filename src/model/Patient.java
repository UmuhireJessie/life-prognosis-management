package src.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

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
    private String uuid;

    private static Map<String, Double> lifeExpectancyMap = new HashMap<>();

    static {
        // Set the system property (acts like an environment variable within your Java
        // application
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
            boolean onART, String artStartDate, String countryISOCode, String uuid) {
        super(firstName, lastName, email, password, Role.PATIENT);
        this.dateOfBirth = LocalDate.parse(dateOfBirth);
        this.hasHIV = hasHIV;
        this.diagnosisDate = diagnosisDate.isEmpty() ? null : LocalDate.parse(diagnosisDate);
        this.onART = onART;
        this.artStartDate = artStartDate.isEmpty() ? null : LocalDate.parse(artStartDate);
        this.countryISOCode = countryISOCode;
        this.uuid = uuid;
    }

    // Options that are only available to patients
    @Override
    public void displayOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n============================================================================");
        System.out.println("\nPatients Options:");

        if (password == "") {
            System.out.println("\t1. Complete Registration");
            System.out.println("\t2. View Profile");
            System.out.println("\t3. Update Profile");
            System.out.println("\t4. Download Your Info");
            System.out.println("\t5. Get Life Prognosis");
            System.out.println("\t6. Logout");
        } else {
            System.out.println("\t1. View Profile");
            System.out.println("\t2. Update Profile");
            System.out.println("\t3. Download Your Info");
            System.out.println("\t4. Get Life Prognosis");
            System.out.println("\t5. Logout");
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
                case 3:
                    generateDemiseSchedule();
                    break;
                case 4:
                    getLifePrognosis();
                    break;
                case 5:
                    System.out.println("--> Logging out...");
                    break;
                default:
                    System.out.println("--> Invalid option.");
            }
        } else {
            switch (choice) {
                case 1:
                    completeRegistration(scanner, email, uuid);
                    break;
                case 2:
                    viewPatientProfile(scanner);
                    break;
                case 3:
                    updatePatientProfile(scanner);
                    break;
                case 4:
                    generateDemiseSchedule();
                    break;
                case 5:
                    getLifePrognosis();
                    break;
                case 6:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid option.");
            }

        }

    }

    private void completeRegistration(Scanner scanner, String email, String uuid) {
        LifePrognosisUI.main(new String[] { email, uuid });
    }

    private void viewPatientProfile(Scanner scanner) {
        try {
            // Call bash script to view patient profile
            String result = Main.callBashFunction("get_patient", email);
            System.out.println("\n--> Here is your information: \n");
            System.out.println(result);

            // Display options again
            displayOptions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePatientProfile(Scanner scanner) {
        try {
            // The email is already stored in the object
            String email = this.email;

            System.out.println("\nLeave fields empty if you do not want to update them.\n");

            System.out.print("First Name: ");
            String firstName = scanner.nextLine().trim();

            System.out.print("Last Name: ");
            String lastName = scanner.nextLine().trim();

            System.out.print("Date of Birth (YYYY-MM-DD): ");
            String dob = scanner.nextLine().trim();
            while (!Helper.isValidDate(dob) && !dob.isEmpty()) {
                System.out.println("\n--> Invalid date format. Please enter date of birth in YYYY-MM-DD format:");
                dob = scanner.nextLine().trim();
            }

            System.out.print("HIV Status (true/false): ");
            String hasHIV = scanner.nextLine().trim();

            if (!hasHIV.isEmpty()) {
                while (!Helper.isValidBooleanInput(hasHIV)) {
                    System.out.println("--> Invalid input. Please enter 'true' or 'false':");
                    hasHIV = scanner.nextLine().trim();
                }
            }

            System.out.print("Diagnosis Date (YYYY-MM-DD, optional): ");
            String diagnosisDate = scanner.nextLine().trim();
            while (!Helper.isValidDate(diagnosisDate) && !diagnosisDate.isEmpty()) {
                System.out.println("\n--> Invalid date format. Please enter diagnosis date in YYYY-MM-DD format:");
                diagnosisDate = scanner.nextLine().trim();
            }

            System.out.print("On ART (true/false): ");
            String onART = scanner.nextLine().trim();

            if (!onART.isEmpty()) {
                while (!Helper.isValidBooleanInput(onART)) {
                    System.out.println("--> Invalid input. Please enter 'true' or 'false':");
                    onART = scanner.nextLine().trim();
                }
            }

            System.out.print("ART Start Date (YYYY-MM-DD, optional): ");
            String artStartDate = scanner.nextLine().trim();
            while (!Helper.isValidDate(artStartDate) && !artStartDate.isEmpty()) {
                System.out.println("\n--> Invalid date format. Please enter ART start date in YYYY-MM-DD format:");
                artStartDate = scanner.nextLine().trim();
            }

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
            String result = Main.callBashFunction("update_patient_data", argsArray);
            System.out.println("\n--> " + result);

            // Recompute survival rate after updating patient data
            double newSurvivalRate = computeSurvivalRate();
            System.out.println("--> Updated Survival Rate: " + newSurvivalRate);

            // Display options again
            displayOptions();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double computeSurvivalRate() {
        String[] args = { email, countryISOCode, String.valueOf(hasHIV), String.valueOf(diagnosisDate),
                String.valueOf(onART), String.valueOf(artStartDate) };
        String result = Main.callBashFunction("compute_survival_rate", args);

        try {
            return Double.parseDouble(result.trim());
        } catch (NumberFormatException e) {
            System.err.println("\n--> Error parsing survival rate: " + e.getMessage());
            return -1; // or handle error appropriately
        }
    }

    public void generateDemiseSchedule() {

        LocalDate demiseDate = LocalDate.now().plusYears((long) computeSurvivalRate());
        String fileName = "./src/data/" + email + "_demise_schedule.ics";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("BEGIN:VCALENDAR\n");
            writer.write("VERSION:2.0\n");
            writer.write("PRODID:-//hacksw/handcal//NONSGML v1.0//EN\n");
            writer.write("BEGIN:VEVENT\n");
            writer.write("UID:" + UUID.randomUUID().toString() + "\n");
            writer.write("DTSTAMP:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"))
                    + "\n");
            writer.write("DTSTART:" + demiseDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "\n");
            writer.write("DTEND:" + demiseDate.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE) + "\n");
            writer.write("SUMMARY:Estimated Demise Date for " + email + "\n");
            writer.write("DESCRIPTION:Based on the computed survival rate of "
                    + String.format("%.2f", computeSurvivalRate()) + " years.\n");
            writer.write("END:VEVENT\n");
            writer.write("END:VCALENDAR\n");

            System.out.println("\n--> Demise schedule has been generated and saved at " + fileName);

            // Display options again
            displayOptions();
        } catch (IOException e) {
            System.err.println("\n--> Error generating demise schedule: " + e.getMessage());

            // Display options again
            displayOptions();
        }
    }

    private void getLifePrognosis() {
        try {
            // Call the bash function to get life prognosis
            String result = Main.callBashFunction("get_life_prognosis", email);
            System.out.println("\n--> " + result);
    
            // Display options again
            displayOptions();
    
        } catch (Exception e) {
            e.printStackTrace();
        }
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
