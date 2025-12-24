package ma.dentalTech.service.modules.auth.tests;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.mvc.dto.AuthResponse;
import ma.dentalTech.mvc.dto.LoginRequest;
import ma.dentalTech.mvc.dto.RegisterRequest;
import ma.dentalTech.mvc.dto.UserResponse;
import ma.dentalTech.service.modules.auth.api.AuthService;

public class AuthModuleTest {

    public static void main(String[] args) {

        System.out.println("========== TEST MODULE AUTH ==========");

        // 1) Récupération du service via ApplicationContext
        AuthService authService = ApplicationContext.getBean(AuthService.class);
        if (authService == null) {
            System.err.println("❌ AuthService introuvable dans ApplicationContext.");
            return;
        }
        System.out.println("✅ AuthService chargé: " + authService.getClass().getSimpleName());

        // 2) Données de test (EMAIL UNIQUE)
        String email = "test.auth." + System.currentTimeMillis() + "@dentaltech.ma";
        String nom = "TEST_AUTH";
        String prenom = "USER";
        String telephone = "0600000000";
        String password1 = "Pass1234";
        String password2 = "NewPass1234";

        // 3) REGISTER
        Long userId;
        try {
            RegisterRequest registerRequest = RegisterRequest.builder()
                    .nom(nom)
                    .prenom(prenom)
                    .email(email)
                    .telephone(telephone)
                    .motDePasse(password1)
                    .build();

            UserResponse user = authService.register(registerRequest);
            userId = user.getId();

            System.out.println("✅ REGISTER OK -> userId=" + userId + ", email=" + user.getEmail());

        } catch (Exception e) {
            System.err.println("❌ REGISTER FAILED -> " + e.getMessage());
            return;
        }

        // 4) LOGIN
        try {
            LoginRequest loginRequest = LoginRequest.builder()
                    .login(email)
                    .motDePasse(password1)
                    .build();

            AuthResponse auth = authService.login(loginRequest);

            System.out.println("✅ LOGIN OK -> userId=" + auth.getUserId()
                    + ", nomComplet=" + auth.getNomComplet()
                    + ", email=" + auth.getEmail()
                    + ", message=" + auth.getMessage());

        } catch (Exception e) {
            System.err.println("❌ LOGIN FAILED -> " + e.getMessage());
            return;
        }

        // 5) CHANGE PASSWORD
        try {
            authService.changePassword(userId, password1, password2);
            System.out.println("✅ CHANGE PASSWORD OK");

        } catch (Exception e) {
            System.err.println("❌ CHANGE PASSWORD FAILED -> " + e.getMessage());
            return;
        }

        // 6) LOGIN avec nouveau mot de passe
        try {
            LoginRequest loginRequest2 = LoginRequest.builder()
                    .login(email)
                    .motDePasse(password2)
                    .build();

            AuthResponse auth2 = authService.login(loginRequest2);

            System.out.println("✅ LOGIN (NEW PASS) OK -> userId=" + auth2.getUserId()
                    + ", message=" + auth2.getMessage());

        } catch (Exception e) {
            System.err.println("❌ LOGIN (NEW PASS) FAILED -> " + e.getMessage());
        }

        System.out.println("========== FIN TEST AUTH ==========");
    }
}
