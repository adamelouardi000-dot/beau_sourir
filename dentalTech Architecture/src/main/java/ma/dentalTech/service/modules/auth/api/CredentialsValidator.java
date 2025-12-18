package ma.dentalTech.service.modules.auth.api;

public class CredentialsValidator {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isStrongPassword(String password) {
        // Minimum 6 caractères pour le test
        return password != null && password.length() >= 6;
    }
}