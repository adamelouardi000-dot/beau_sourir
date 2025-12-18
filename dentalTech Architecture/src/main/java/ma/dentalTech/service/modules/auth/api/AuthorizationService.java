package ma.dentalTech.service.modules.auth.api;

public interface AuthorizationService {
    boolean hasAccess(String username, String role);
}
