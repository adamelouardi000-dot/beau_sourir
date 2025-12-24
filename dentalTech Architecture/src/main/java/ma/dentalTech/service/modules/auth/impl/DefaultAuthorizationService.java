package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.AuthorizationService;

import java.util.Set;

public class DefaultAuthorizationService implements AuthorizationService {

    @Override
    public boolean hasRole(Long userId, String role) {
        // ⚠️ À implémenter plus tard si besoin (backoffice)
        return false;
    }

    @Override
    public boolean hasPrivilege(Long userId, String privilege) {
        // ⚠️ À implémenter plus tard si besoin (backoffice)
        return false;
    }

    @Override
    public void checkRole(Long userId, String role) {
        if (!hasRole(userId, role)) {
            throw new SecurityException("Accès refusé – rôle requis : " + role);
        }
    }

    @Override
    public void checkPrivilege(Long userId, String privilege) {
        if (!hasPrivilege(userId, privilege)) {
            throw new SecurityException("Accès refusé – privilège requis : " + privilege);
        }
    }
}
