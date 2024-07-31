// User class and constructor to initialize objects:

import java.time.LocalDateTime;

public abstract class User{
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;

    public User(String email, String passwordHash, String firstName, String lastName){
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String getEmail(){
        return email;
    }
    public String getPasswordHash(){
        return passwordHash;
    }
    public String getFirstNAme(){
        return firstName;
    }
    public String getLastName(){
        return lastName;
    }
    public abstract login(); // must be implemented by any subclass but not the user class
}
// Admin class and initializing the attributes that defined in the superclass:
public class Admin extends User{
    public Admin(String email, String passwordHash, String firstName, String lastName){
        super(email, passwordHash, firstName, lastName);
    }
    public void initiateRegistration(){

    }
    public void generateUUID(){

    }
    public void export() {

    }
    public void aggregate(){

    }
    public void downloadCsv(){

    }
    @Override
    public String login(){
        return "Admin" + getFirstname() + "Logged in.";
    }
}
public class Patient extends User {
    private LocalDate dateOfBirth;
    private boolean hasHIV;
    private LocalDate diagnosisData;
    private String countryISOCode;

    public Patient(String email, String passwordHash, String firstName, String lastName, LocalDate DateOfBirth, boolean hasHIV, LocalDate diagnosisDate, boolean onART, LocalDate artStartDate, String countryISOCode){
        super(email, passwordHash, firstName, lastName);
        this.dateOfBirth = dateOfBirth;
        this.hasHIV = hasHIV;
        this.diagnosisData = diagnosisDate;
        this.onART = onART;
        this.artStartDate = artStartDate;
        this.countryISOCode = countryISOCode;
    }
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    public boolean hasHIV(){
        return hasHIV;
    }
    public LocalDateTime getDiagnLocalDateTime(){
        return getDiagnLocalDateTIme;
    }
    public boolean onART() {
        return onART;
    }
    public LocalDateTime getArtStartDate(){
        return getArtStartDate();
    }
    public String getCountryISOCode() {
        return getCountryISOCode();
    }
    public double computeSurvivalRate(){
        return 0.0;
    }
    @Override
    public String login(){
        return "Patient" + getFirstName() + "logged in.";
    }
}