package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

public class DefaultAuthService implements AuthService {

    private final CredentialsValidator validator;
    private final PasswordEncoder passwordEncoder;

    private final String storedUsername = "admin";
    private final String storedPassword;

    public DefaultAuthService(
            CredentialsValidator validator,
            PasswordEncoder passwordEncoder
    ) {
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
        this.storedPassword = passwordEncoder.encode("1234");
    }

    @Override
    public boolean authenticate(AuthRequest request) {
        validator.validate(request);

        if (!storedUsername.equals(request.getUsername())) {
            return false;
        }

        return passwordEncoder.matches(
                request.getPassword(),
                storedPassword
        );
    }
}
