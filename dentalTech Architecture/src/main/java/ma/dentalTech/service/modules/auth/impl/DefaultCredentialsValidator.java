package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

public class DefaultCredentialsValidator implements CredentialsValidator {

    @Override
    public void validate(AuthRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("AuthRequest est null");
        }

        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username obligatoire");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password obligatoire");
        }
    }
}
