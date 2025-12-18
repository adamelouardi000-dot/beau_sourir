package ma.dentalTech.service.modules.users.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;

import ma.dentalTech.service.modules.users.api.RoleManagementService;

import java.util.List;

public class TestRoleManagementService {

    public static void main(String[] args) {
        RoleManagementService service = ApplicationContext.getBean(RoleManagementService.class);

        Long roleId = null;

        try {
            Role r = Role.builder()
                    .libelle("ROLE_TEST_" + System.currentTimeMillis())
                    .type(RoleType.ADMIN) // change si besoin
                    .build();

            service.createRole(r);
            roleId = r.getId();
            System.out.println("✅ role created id=" + roleId);

            service.updateRolePrivileges(roleId, List.of("P1", "P2", "P3"));
            System.out.println("✅ privileges updated");

            System.out.println("getById => " + service.getRoleById(roleId));
            System.out.println("allRoles size => " + service.getAllRoles().size());

            service.deleteRole(roleId);
            roleId = null;
            System.out.println("✅ role deleted");

        } finally {
            if (roleId != null) {
                try { service.deleteRole(roleId); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
