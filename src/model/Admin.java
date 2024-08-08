package src.model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import src.Main;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.ADMIN);
    }

    @Override
    public void displayOptions() {
        // System.out.println("Admin Options:");
        // System.out.println("1. View All Users");
        // System.out.println("2. Aggregate Data");
        // System.out.println("3. Download All Users Info");
        // System.out.println("4. Export Analytics");
        // System.out.println("5. Initiate Patient Registration");
        // System.out.println("6. Logout");

        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAdmin Options:");
        System.out.println("\t1. Initiate Patient Registration");
        System.out.println("\t2. View All Users");
        System.out.println("\t3. Aggregate Data");
        System.out.println("\t4. Download All Users Info");
        System.out.println("\t5. Export Analytics");
        System.out.println("\t6. Logout");

        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                registerPatient(scanner);
                break;
            case 2:
                viewAllUsers();
                break;
            case 4:
                downloadAllUsersInfo();
                break;
            case 6:
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid option.");}
    }

     private void registerPatient(Scanner scanner) {
        try {
            System.out.print("Enter Patient Email: ");
            String email = scanner.nextLine().trim();

            // Call bash script to register the patient
            String result = Main.callBashFunction("initiate_registration", email);
            System.out.println(result);

            displayOptions();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewAllUsers() {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "view_all_users");
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

    public void aggregateData() {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "aggregate_data");
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

    public void downloadAllUsersInfo() {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "download_all_users");
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

    public void exportAnalytics() {
        try {
            ProcessBuilder pb = new ProcessBuilder("./src/scripts/user-management.sh", "export_analytics");
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

    public void initiateRegistration(Scanner scanner) {
        System.out.print("Enter Email for new patient: ");
        String email = scanner.nextLine().trim();
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
}
