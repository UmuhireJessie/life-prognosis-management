package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import src.ui.LifePrognosisUI;
import src.model.Patient;
import src.model.Admin;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Life Prognosis Management System");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();

            String role = checkLogin(email, password);
            if (role != null) {
                System.out.println("Login successful.");
                if ("Admin".equals(role)) {
                    Admin admin = new Admin("FirstName", "LastName", email, password); // Example details
                    admin.displayOptions();
                } else if ("Patient".equals(role)) {
                    // Using a dummy patient data so instanciating the class
                    Patient patient = new Patient("FirstName", "LastName", email, password, 
                            "1990-01-01", false, "", false, "", "US");
                    patient.displayOptions();
                }
            } else {
                System.out.println("Login failed. Please check your credentials.");
            }
        } else if (choice == 2) {
            LifePrognosisUI.main(args); // Direct to patient registration or options
        } else {
            System.out.println("Invalid choice. Exiting.");
        }

        scanner.close();
    }

    // Funstion to check login before accessing anything on the system
    private static String checkLogin(String email, String password) {
        String role = null;
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "check_login", email, password);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Login successful")) {
                    String[] parts = line.split(",");
                    role = parts[3].trim();
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return role;
    }
}
