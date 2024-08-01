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
        super(firstName, lastName, email, password, Role.PATIENT);
        this.dateOfBirth = LocalDate.parse(dateOfBirth);
        this.hasHIV = hasHIV;
        this.diagnosisDate = LocalDate.parse(diagnosisDate);
        this.onART = onART;
        this.artStartDate = LocalDate.parse(artStartDate);
        this.countryISOCode = countryISOCode;
    }

    public double computeSurvivalRate() {
        if (!hasHIV) {
            return Double.MAX_VALUE;
        }

        int averageLifespan = getAverageLifespanByCountry(countryISOCode);
        long ageAtDiagnosis = ChronoUnit.YEARS.between(dateOfBirth, diagnosisDate);
        long yearsSinceDiagnosis = ChronoUnit.YEARS.between(diagnosisDate, LocalDate.now());

        if (!onART) {
            return ageAtDiagnosis + 5;
        }

        // long yearsOnART = ChronoUnit.YEARS.between(artStartDate, LocalDate.now());
        double remainingLifespan = (averageLifespan - ageAtDiagnosis) * 0.9;

        for (int i = 1; i <= yearsSinceDiagnosis; i++) {
            if (i > 1) {
                remainingLifespan *= 0.9;
            }
        }

        return ageAtDiagnosis + remainingLifespan;
    }

    private int getAverageLifespanByCountry(String countryISOCode) {
        if ("RW".equals(countryISOCode)) {
            return 70;
        } else {
            return 75;
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
