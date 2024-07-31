package src;

import src.model.Admin;
import src.model.Patient;
import src.ui.LifePrognosisUI;

public class Main {

    public static void main(String[] args) {
        /*
            // Example usage of Admin and Patient classes
            Admin admin = new Admin("John", "Doe", "admin@example.com", "admin@123");
            System.out.println("Admin: " + admin.getFirstName() + " " + admin.getLastName());

            Patient patient = new Patient("Jane", "Smith", "jane@example.com", "jane@123",
                    "1990-01-01", true, "2010-01-01", true, "2010-01-15", "RW");
            System.out.println("Patient: " + patient.getFirstName() + " " + patient.getLastName());

            double survivalRate = patient.computeSurvivalRate();
            System.out.println("Survival Rate: " + survivalRate);
        */ 

        // Calling the UI class
        LifePrognosisUI.main(args);
    }
}
