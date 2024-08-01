package src.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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

    // Options that are only available to patients
    @Override
    public void displayOptions() {
        System.out.println("Patient Options:");
        System.out.println("1. View Profile");
        System.out.println("2. Update Profile");
        System.out.println("3. Download Your Info");
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
