package ma.dentalTech.service.modules.auth.api;

public interface AuthorizationService {

    boolean hasRole(Long userId, String role);

    boolean hasPrivilege(Long userId, String privilege);

    void checkRole(Long userId, String role);

    void checkPrivilege(Long userId, String privilege);
}
