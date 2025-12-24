package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.mvc.dto.AuthResponse;
import ma.dentalTech.mvc.dto.LoginRequest;
import ma.dentalTech.mvc.dto.RegisterRequest;
import ma.dentalTech.mvc.dto.UserResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    UserResponse register(RegisterRequest request);

    void changePassword(Long userId, String oldPassword, String newPassword);
}
