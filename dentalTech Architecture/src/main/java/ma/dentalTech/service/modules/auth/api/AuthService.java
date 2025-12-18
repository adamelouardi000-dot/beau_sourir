package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.service.modules.auth.dto.AuthRequest;

public interface AuthService {
    /**
     * Authentifie un utilisateur à partir de ses identifiants.
     */
    Utilisateur authenticate(AuthRequest request);
}