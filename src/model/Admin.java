package src.model;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.ADMIN);
    }

    // Admin-specific methods for data aggregation
}
