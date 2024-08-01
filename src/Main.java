package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import src.ui.LifePrognosisUI;


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
                    displayAdminOptions();
                } else if ("Patient".equals(role)) {
                    LifePrognosisUI.main(args); // Direct to patient registration or options
                }
            } else {
                System.out.println("Login failed. Please check your credentials.");
            }
        } else if (choice == 2) {
            System.out.println("Registration functionality is not implemented yet.");
        } else {
            System.out.println("Invalid choice. Exiting.");
        }

        scanner.close();
    }

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

    private static void displayAdminOptions() {
        System.out.println("Admin Options:");
        System.out.println("1. View All Users");
        System.out.println("2. Aggregate Data");
        System.out.println("3. Download All Users Info");
        System.out.println("4. Export Analytics");
        // Handle admin-specific actions here
    }
}
