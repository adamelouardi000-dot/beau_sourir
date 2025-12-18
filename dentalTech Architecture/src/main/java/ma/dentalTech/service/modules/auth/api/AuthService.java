package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.service.modules.auth.dto.AuthRequest;

public interface AuthService {
    boolean authenticate(AuthRequest request);
}
