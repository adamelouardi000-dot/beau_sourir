package ma.dentalTech.service.modules.users.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.mvc.dto.*;
import ma.dentalTech.service.modules.users.api.RoleService;

import java.util.List;

public class TestRoleService {

    public static void main(String[] args) {
        RoleService service = ApplicationContext.getBean(RoleService.class);

        Long roleId = null;

        try {
            // CREATE
            RoleCreateRequest create = new RoleCreateRequest(
                    "ROLE_TEST_" + System.currentTimeMillis(),
                    RoleType.ADMIN,
                    List.of("P1", "P2")
            );

            RoleDto created = service.create(create);
            roleId = created.id();
            System.out.println("✅ Role created id=" + roleId);

            // UPDATE (change libelle + privileges)
            RoleUpdateRequest update = new RoleUpdateRequest(
                    roleId,
                    created.libelle() + "_UPDATED",
                    created.type(),
                    List.of("P3", "P4")
            );

            RoleDto updated = service.update(update);
            System.out.println("✅ Role updated => " + updated.libelle() + " priv=" + updated.privileges());

            // GRANT / REVOKE
            service.grantPrivilege(new GrantPrivilegeRequest(roleId, "P5"));
            System.out.println("✅ privilege granted P5");

            service.revokePrivilege(new RevokePrivilegeRequest(roleId, "P3"));
            System.out.println("✅ privilege revoked P3");

            // READ
            RoleDto byId = service.findById(roleId);
            System.out.println("findById => " + byId);

            System.out.println("all roles size => " + service.findAll().size());

            // DELETE
            service.deleteById(roleId);
            roleId = null;
            System.out.println("✅ Role deleted");

        } finally {
            if (roleId != null) {
                try { service.deleteById(roleId); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
