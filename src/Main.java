package src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import src.model.Patient;
import src.model.Admin;
import src.model.User;
import src.utils.Helper;

import java.util.InputMismatchException;
import java.io.Console;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Console console = System.console();

        System.out.println("\nWelcome to Life Prognosis Management System");
        System.out.println("\t1. Login");
        System.out.println("\t2. Register as a Patient");
        System.out.println("\t3. Quit");
        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Loop until a valid choice is made
        while (true) {
            try {
                if (choice < 1 || choice > 3) {
                    System.out.print("--> Invalid choice. Please try again: ");
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline
                } else {
                    break; // valid choice
                }
            } catch (InputMismatchException e) {
                System.out.print("--> Invalid input. Please enter a number (1-3): ");
                scanner.next(); // Consume the invalid input
            }
        }

        User user = null;

        if (choice == 1) {
            System.out.println("\n============================================================================");
            System.out.print("\nEnter Email: ");
            String email = scanner.nextLine().trim();
            while (!Helper.isValidEmail(email)) {
                System.out.println("--> Invalid email format. Please enter a valid email: ");
                email = scanner.nextLine().trim();
            }

            char[] passwordArray = console.readPassword("Enter Password: ");
            String password = new String(passwordArray);
            String loginResult = callBashFunction("check_login", email, password);
            String[] resultParts = loginResult.split(",");

            if ("Login successful".equals(resultParts[0].trim())) {
                System.out.println("--> Login successful. Welcome!");
                String role = resultParts[3].trim();

                // Proceed based on role
                if ("Admin".equals(role)) {
                    user = new Admin("FirstName", "LastName", email, password); // dummy admin object
                    user.displayOptions();
                } else if ("Patient".equals(role)) {
                    // Dummy patient data for instantiation
                    user = new Patient("FirstName", "LastName", email, password,
                            "1990-01-01", false, "", false, "", "US", "");
                    user.displayOptions();
                }
            } else {
                System.out.println("--> Login failed. " + loginResult);
                return; // Exit if login fails
            }
        }

        if (choice == 2) {
            System.out.println("\nWelcome to Patient Registration\n");
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            while (!Helper.isValidEmail(email)) {
                System.out.println("--> Invalid email format. Please enter a valid email: ");
                email = scanner.nextLine().trim();
            }
            System.out.print("Enter UUID you received: ");
            String uuid = scanner.nextLine().trim();

            // Call bash function to check if the email provided has already been registered
            String initialCheckResult = callBashFunction("check_pre_registration", email, uuid);
            String[] resultParts = initialCheckResult.split(",");

            if ("Email is not found".equals(resultParts[0].trim())) {
                System.out.println("--> Sorry, email is not pre-registered. Please admin to initiate your registration");
            } else if ("Login check failed".equals(resultParts[0].trim())) {
                System.out.println("--> Please enter your email and uuid correctly.");
            } else {
                user = new Patient("FirstName", "LastName", email, "",
                        "1990-01-01", false, "", false, "", "US", uuid);
                user.displayOptions();
            }

        } else if (choice == 3) {
            return;
        }
        scanner.close();
    }

    // Method to build the command for ProcessBuilder
    private static ProcessBuilder buildCommand(String functionName, String... args) {
        List<String> command = new ArrayList<>();
        command.add("./src/scripts/user-management.sh");
        command.add(functionName);

        for (String arg : args) {
            command.add(arg);
        }

        return new ProcessBuilder(command);
    }

    // Method to call bash functions
    public static String callBashFunction(String functionName, String... args) {
        String result = null;
        try {
            ProcessBuilder pb = buildCommand(functionName, args);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            result = output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
