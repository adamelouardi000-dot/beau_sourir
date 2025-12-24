package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.mvc.dto.LoginRequest;
import ma.dentalTech.mvc.dto.RegisterRequest;

public interface CredentialsValidator {

    void validateLogin(LoginRequest request);

    void validateRegister(RegisterRequest request);

    void validateNewPassword(String newPassword);
}
