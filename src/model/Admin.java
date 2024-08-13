package src.model;

//---------------------------------------------------------------------------------------------------------------------------------
//import java.io.BufferedReader; // import BufferedReade class from java.io package to read the text from an input file or console
//import java.io.InputStreamReader; // import InputStreamReader class from java.io package  it reads bytes and decodes them into characters
import java.util.Scanner; //Scanner can read and interpret input data (like numbers and text) based on specific patterns defined by regular expressions.
import src.Main; // import the main class from src package
import src.ui.LifePrognosisUI;

//-------------------------------------------------------------------------------------
public class Admin extends User{

    public Admin(String firstName, String lastName, String email, String password) { // it is necessary to declare the constructor in the child class if you want to initialize the child class with specific values and ensure proper initialization of inherited fields. 
        super(firstName, lastName, email, password, Role.ADMIN);
    }
//---------------------------------------------------------------------------------------------------------------------------------
    @Override
    public void displayOptions() { //new implementation of the parent method of the dispayOptions
        Scanner scanner = new Scanner(System.in); //This allows the program to capture user input from the console.
        System.out.println("\nAdmin Options:");
        System.out.println("\t1. Initiate Patient Registration");
        System.out.println("\t2. View All Users");
        System.out.println("\t3. Aggregate Data");
        System.out.println("\t4. Download All Users Info");
        System.out.println("\t5. Export Analytics");
        System.out.println("\t6. Logout");

        System.out.print("Choose an option: ");
        int choice = scanner.nextInt();  //reads the next integer input from the user, which corresponds to their chosen option.
        scanner.nextLine(); // read the entire line from the use

        switch (choice) { //The switch statement evaluates the value of choice and executes the corresponding case block.
            case 1:
                registerPatient(scanner);
                break;
            case 2:
                viewAllUsers();
                break;
            //case 3:
              //   aggregateData();
                // break;
            case 4:
                downloadAllUsersInfo();
                break;
            case 5: 
                exportAnalytics();
                break;
            case 6:
                System.out.println("Logging out...");
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------
    private void registerPatient(Scanner scanner) { //the scanner will be used to read the input of the user
        try {
            System.out.print("Enter Patient Email: "); //if I chose 1, then there is Enter an eamil choice
            String email = scanner.nextLine().trim(); //read the line and trim any white space and store the value in the eamil variable

            // Call bash script to register the patient
            String result = Main.callBashFunction("register_patient", email);
            System.out.println(result);

            displayOptions();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // The method registers a patient by prompting the admin for an email, calling a bash script to handle the registration, and then displaying the result.
//---------------------------------------------------------------------------------------------------------------------------------

    private void viewAllUsers() {
        try {
            // Call bash script to view all users
            String result = Main.callBashFunction("view_all_users");
            
            // Split the result into lines
            String[] lines = result.split("\n");
    
            // Print table headers with vertical lines
            System.out.printf("| %-20s | %-36s | %-50s | %-10s |%n", "Email", "UUID", "Password", "Role");
            
            // Print a separator
            System.out.println("|----------------------|--------------------------------------|----------------------------------------------------|------------|");
            
            // Process and display each line of user data
            for (String line : lines) {
                // Assuming fields are separated by a comma
                String[] fields = line.split(",");
                if (fields.length >= 4) {
                    System.out.printf("| %-20s | %-36s | %-50s | %-10s |%n", fields[0], fields[1], fields[2], fields[3]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
//---------------------------------------------------------------------------------------------------------------------------------  
//Aggregate the user data function in week3

//---------------------------------------------------------------------------------------------------------------------------------  
// Donwmload all the user data in a csv file:

    private void downloadAllUsersInfo() {
        try {
            // Call bash script to download user info
            String result = Main.callBashFunction("download_all_users");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//---------------------------------------------------------------------------------------------------------------------------------

//export analytics function:
    private void exportAnalytics() {
        try {
            String result = Main.callBashFunction("export_analytics");
            System.out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }


}

//---------------------------------------------------------------------------------------------------------------------------------
