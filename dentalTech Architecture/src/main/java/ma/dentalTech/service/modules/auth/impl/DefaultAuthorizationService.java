package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.service.modules.auth.api.AuthorizationService;

public class DefaultAuthorizationService implements AuthorizationService {

    @Override
    public boolean hasAccess(String username, String role) {
        return "admin".equals(username) && "ADMIN".equals(role);
    }
}
