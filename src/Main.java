package src;

import src.ui.LifePrognosisUI;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static void main(String[] args) {
        // Example to call Bash script for login
        try {
            ProcessBuilder pb = new ProcessBuilder("./user-management.sh", "check_login", "jane@example.com", "password123");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Call the UI
        LifePrognosisUI.main(args);
    }
}
