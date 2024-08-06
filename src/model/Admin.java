package src.model;

import java.util.Scanner;
import src.Main;
import src.ui.LifePrognosisUI;

public class Admin extends User {

    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Role.ADMIN);
    }

    @Override
    public void displayOptions() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nAdmin Options:");
        System.out.println("\t1. Initiate Patient Registration");
        System.out.println("\t2. Complete Patient Registration");
        System.out.println("\t3. View All Users");
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
                completeRegistration(scanner);
                break;
            case 3:
                viewAllUsers();
                break;
            case 4:
                downloadAllUsersInfo();
                break;
            case 5:
                System.out.println("Export Analytics");
                break;
            case 6:
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid option.");
        }
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

    private void completeRegistration(Scanner scanner) {
        try {
                LifePrognosisUI.main(null);
            displayOptions();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void viewAllUsers() {
        try {
            // Call bash script to view all users
            String result = Main.callBashFunction("view_all_users");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void downloadAllUsersInfo() {
        try {
            // Call bash script to download user info
            String result = Main.callBashFunction("download_all_users");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}