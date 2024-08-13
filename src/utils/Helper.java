package src.utils;
//--------------------------------

import java.time.LocalDate;
import java.util.regex.Pattern;
//------------------------------------
//static indicates the class can only have static members and you cannot create an instance of it. This is used for stateless functionality (for example a type that just defines extension methods, or utility methods)

public class Helper {
    private static final Pattern EMAIL_PATTERN = Pattern.compile( //This declares a constant EMAIL_PATTERN of type Pattern
        "^[A-Za-z0-9+_.-]+@(.+)$" //compile a regular expression into a pattern
    );

    private static final Pattern STRONG_PASSWORD_PATTERN = Pattern.compile( //This declares a constant STRONG_PASSWORD_PATTERN of type Pattern.
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,}$"
    );

    public static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPassword(String password) {
        return STRONG_PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean isValidDate(String dateStr) {
        try {
            LocalDate.parse(dateStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
