package ma.dentalTech.service.modules.users.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.enums.Sexe;

import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.*;

import java.time.LocalDate;

public class TestUserManagementService {

    public static void main(String[] args) {

        UserManagementService service = ApplicationContext.getBean(UserManagementService.class);

        Long userId = null;

        try {
            CreateAdminRequest req = new CreateAdminRequest(
                    "AdminTest",
                    "admin_" + System.currentTimeMillis() + "@mail.com",
                    "Adresse",
                    "CIN123",
                    "0600000000",
                    Sexe.Homme,
                    "login_" + System.currentTimeMillis(),
                    "pass123",
                    LocalDate.of(1999, 1, 1),
                    5000.0,
                    200.0,
                    LocalDate.now(),
                    10
            );

            UserAccountDto dto = service.createAdmin(req);
            userId = dto.id();
            System.out.println("✅ admin created id=" + userId);

            service.assignRoleToUser(userId, RoleType.ADMIN);
            System.out.println("✅ role assigned");

            UserAccountDto loaded = service.getUserById(userId);
            System.out.println("getUserById => " + loaded);

            UpdateUserProfileRequest upd = new UpdateUserProfileRequest(
                    userId,
                    "AdminTest Updated",
                    dto.email(),
                    "Adresse NEW",
                    "0600999999",
                    dto.sexe(),
                    dto.dateNaissance()
            );

            UserAccountDto after = service.updateUserProfile(upd);
            System.out.println("✅ profile updated => " + after.nom());

            System.out.println("search 'admin' size => " + service.searchUsersByKeyword("admin").size());

            // ⚠️ delete user: dépend si tu as une méthode delete user globale (pas dans interface)
            // Ici on ne peut pas nettoyer sans repo direct.
            // Si tu veux nettoyage complet, dis-moi si tu as delete dans UserManagementService.

        } finally {
            SessionFactory.getInstance().closeConnection();
        }
    }
}
