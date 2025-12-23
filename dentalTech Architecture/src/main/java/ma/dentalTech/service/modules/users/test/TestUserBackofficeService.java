package ma.dentalTech.service.modules.users.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.enums.Sexe;
import ma.dentalTech.mvc.dto.*;
import ma.dentalTech.service.modules.users.api.RoleService;
import ma.dentalTech.service.modules.users.api.UserBackofficeService;

import java.time.LocalDate;
import java.util.List;

public class TestUserBackofficeService {

    public static void main(String[] args) {

        UserBackofficeService userService = ApplicationContext.getBean(UserBackofficeService.class);
        RoleService roleService = ApplicationContext.getBean(RoleService.class);

        Long userId = null;
        Long roleId = null;

        try {
            // 1) CREATE ROLE (pour pouvoir assigner)
            RoleDto role = roleService.create(new RoleCreateRequest(
                    "ROLE_TEST_ASSIGN_" + System.currentTimeMillis(),
                    RoleType.ADMIN,
                    List.of("MANAGE_USERS", "MANAGE_ROLES")
            ));
            roleId = role.id();
            System.out.println("✅ role created id=" + roleId);

            // 2) CREATE USER
            UserCreateRequest createUser = new UserCreateRequest(
                    "UserTest",
                    "user_" + System.currentTimeMillis() + "@mail.com",
                    "Adresse",
                    "CIN" + System.currentTimeMillis(),
                    "0600000000",
                    Sexe.Homme,
                    "login_" + System.currentTimeMillis(),
                    "pass123",
                    LocalDate.of(1999, 1, 1)
            );

            UserDto created = userService.createUser(createUser);
            userId = created.id();
            System.out.println("✅ user created id=" + userId);

            // 3) ASSIGN ROLE
            userService.assignRole(new AssignRoleRequest(userId, roleId));
            System.out.println("✅ role assigned to user");

            // 4) GET USER ROLES
            System.out.println("user roles => " + userService.getUserRoles(userId));

            // 5) UPDATE USER
            UserUpdateRequest updateUser = new UserUpdateRequest(
                    userId,
                    "UserTest Updated",
                    created.email(),
                    "Adresse NEW",
                    created.cin(),
                    "0600999999",
                    created.sexe(),
                    created.login(),
                    created.dateNaissance()
            );

            UserDto updated = userService.updateUser(updateUser);
            System.out.println("✅ user updated => " + updated.nom());

            // 6) RESET PASSWORD (back office)
            userService.resetPassword(userId, "newPass456");
            System.out.println("✅ password reset");

            // 7) REMOVE ROLE
            userService.removeRole(new AssignRoleRequest(userId, roleId));
            System.out.println("✅ role removed");

            // 8) DELETE USER
            userService.deleteUser(userId);
            userId = null;
            System.out.println("✅ user deleted");

            // 9) DELETE ROLE
            roleService.deleteById(roleId);
            roleId = null;
            System.out.println("✅ role deleted");

        } finally {
            if (userId != null) {
                try { userService.deleteUser(userId); } catch (Exception ignored) {}
            }
            if (roleId != null) {
                try { roleService.deleteById(roleId); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
