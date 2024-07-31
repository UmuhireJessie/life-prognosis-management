package src.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Patient extends User {
    private LocalDate dateOfBirth;
    private boolean hasHIV;
    private LocalDate diagnosisDate;
    private boolean onART;
    private LocalDate artStartDate;
    private String countryISOCode;

    public Patient(String firstName, String lastName, String email, String password,
                   String dateOfBirth, boolean hasHIV, String diagnosisDate,
                   boolean onART, String artStartDate, String countryISOCode) {
        super(firstName, lastName, email, password);
        this.dateOfBirth = LocalDate.parse(dateOfBirth);
        this.hasHIV = hasHIV;
        this.diagnosisDate = LocalDate.parse(diagnosisDate);
        this.onART = onART;
        this.artStartDate = LocalDate.parse(artStartDate);
        this.countryISOCode = countryISOCode;
    }

    public double computeSurvivalRate() {
        if (!hasHIV) {
            return Double.MAX_VALUE; // No impact on lifespan if not HIV positive
        }

        int averageLifespan = getAverageLifespanByCountry(countryISOCode);

        long ageAtDiagnosis = ChronoUnit.YEARS.between(dateOfBirth, diagnosisDate);
        long yearsSinceDiagnosis = ChronoUnit.YEARS.between(diagnosisDate, LocalDate.now());

        if (!onART) {
            return ageAtDiagnosis + 5; // Survival rate without ART is 5 years post-diagnosis
        }

        long yearsOnART = ChronoUnit.YEARS.between(artStartDate, LocalDate.now());
        double remainingLifespan = (averageLifespan - ageAtDiagnosis) * 0.9;

        // Apply reduction for delay in starting ART
        for (int i = 1; i <= yearsSinceDiagnosis; i++) {
            if (i > 1) {
                remainingLifespan *= 0.9;
            }
        }

        return ageAtDiagnosis + remainingLifespan;
    }

    // Method to fetch average lifespan by country (placeholder)
    private int getAverageLifespanByCountry(String countryISOCode) {
        // Placeholder implementation
        // Replace with actual logic to fetch from data source or configuration
        // For simplicity, returning hardcoded values based on example
        if ("RW".equals(countryISOCode)) {
            return 70; // Example for Rwanda
        } else {
            return 75; // Example for other countries
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
