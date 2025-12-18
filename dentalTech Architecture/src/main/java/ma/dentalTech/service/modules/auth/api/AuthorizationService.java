package ma.dentalTech.service.modules.auth.api;

import ma.dentalTech.entities.users.Utilisateur;

public interface AuthorizationService {
    /**
     * Vérifie si l'utilisateur possède le rôle nécessaire pour accéder à une ressource.
     */
    boolean isAuthorized(Utilisateur utilisateur, String requiredRole);
}