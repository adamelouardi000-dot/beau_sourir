package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.mvc.dto.LoginRequest;
import ma.dentalTech.mvc.dto.RegisterRequest;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;

public class DefaultCredentialsValidator implements CredentialsValidator {

    private static final int PASSWORD_MIN = 6;

    @Override
    public void validateLogin(LoginRequest request) {
        if (request == null)
            throw new IllegalArgumentException("LoginRequest null.");

        if (request.getLogin() == null || request.getLogin().isBlank())
            throw new IllegalArgumentException("Login obligatoire.");

        if (request.getMotDePasse() == null || request.getMotDePasse().isBlank())
            throw new IllegalArgumentException("Mot de passe obligatoire.");
    }

    @Override
    public void validateRegister(RegisterRequest request) {
        if (request == null)
            throw new IllegalArgumentException("RegisterRequest null.");

        if (request.getNom() == null || request.getNom().isBlank())
            throw new IllegalArgumentException("Nom obligatoire.");

        if (request.getPrenom() == null || request.getPrenom().isBlank())
            throw new IllegalArgumentException("Prénom obligatoire.");

        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new IllegalArgumentException("Email obligatoire.");

        if (request.getMotDePasse() == null || request.getMotDePasse().length() < PASSWORD_MIN)
            throw new IllegalArgumentException("Mot de passe trop court (min " + PASSWORD_MIN + ").");
    }

    @Override
    public void validateNewPassword(String newPassword) {
        if (newPassword == null || newPassword.length() < PASSWORD_MIN)
            throw new IllegalArgumentException("Nouveau mot de passe invalide.");
    }
}
