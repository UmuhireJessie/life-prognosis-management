package src.model;

public abstract class User {
    protected String firstName;
    protected String lastName;
    protected String email;
    protected String password;
    protected Role role;
    //protected attribute is accessible to all classes in the same package.
    //protected attribute is accessible to any subclasses (even if they are in different packages).

    // Constructor
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Display user details
    public abstract void displayOptions();

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Convert user data to a CSV-like format
    public String toCSV() {
        return String.join(",", firstName, lastName, email, password, role.toString());
    }
}
