package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import src.model.Admin;
import src.model.Patient;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Welcome to Life Prognosis Management System");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    loginUser(scanner);
                    break;
                case 2:
                    registerUser(scanner);
                    break;
                case 3:
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void loginUser(Scanner scanner) {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        String loginResult = checkLogin(email, password);
        if (loginResult != null) {
            String[] parts = loginResult.split(",");
            String role = parts[3].trim();
            if ("Admin".equals(role)) {
                handleAdminMenu(scanner, email);
            } else if ("Patient".equals(role)) {
                handlePatientMenu(scanner, email);
            }
        } else {
            System.out.println("Login failed. Please check your credentials.");
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.print("Enter Email for new user: ");
        String email = scanner.nextLine().trim();
        initiateRegistration(email);
    }

    private static void handleAdminMenu(Scanner scanner, String email) {
        Admin admin = new Admin("FirstName", "LastName", email, "password");
        while (true) {
            admin.displayOptions();
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    admin.viewAllUsers();
                    break;
                case 2:
                    admin.aggregateData();
                    break;
                case 3:
                    admin.downloadAllUsersInfo();
                    break;
                case 4:
                    admin.exportAnalytics();
                    break;
                case 5:
                    System.out.println("Logging out.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void handlePatientMenu(Scanner scanner, String email) {
        Patient patient = getPatientByEmail(email);
        while (true) {
            patient.displayOptions();
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    patient.viewProfile();
                    break;
                case 2:
                    patient.updateProfile(scanner);
                    break;
                case 3:
                    patient.downloadInfo();
                    break;
                case 4:
                    System.out.println("Logging out.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static String checkLogin(String email, String password) {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "check_login", email, password);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Login successful")) {
                    return line;
                }
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void initiateRegistration(String email) {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "initiate_registration", email);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Patient getPatientByEmail(String email) {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "get_patient", email);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = reader.readLine();
            if (line != null) {
                String[] parts = line.split(",");
                return new Patient(parts[0], parts[1], parts[2], parts[3], parts[4], 
                                   Boolean.parseBoolean(parts[5]), parts[6], 
                                   Boolean.parseBoolean(parts[7]), parts[8], parts[9]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
