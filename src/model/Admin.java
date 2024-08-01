package src.model;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.ADMIN);
    }

    // Admin-specific methods for data aggregation
    @Override
    public void displayOptions() {
        System.out.println("Admin Options:");
        System.out.println("1. View All Users");
        System.out.println("2. Aggregate Data");
        System.out.println("3. Download All Users Info");
        System.out.println("4. Export Analytics");
    }
}
