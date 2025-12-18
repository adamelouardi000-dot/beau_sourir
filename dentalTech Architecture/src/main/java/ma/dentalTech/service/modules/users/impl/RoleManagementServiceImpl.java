package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.RoleManagementService;

import java.util.ArrayList;
import java.util.List;

public class RoleManagementServiceImpl implements RoleManagementService {

    private final RoleRepository roleRepo;

    public RoleManagementServiceImpl(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Override
    public Role createRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role ne doit pas être null");
        if (isBlank(role.getLibelle())) throw new IllegalArgumentException("libelle obligatoire");
        if (role.getType() == null) throw new IllegalArgumentException("type obligatoire");

        if (roleRepo.existsByLibelle(role.getLibelle())) {
            throw new IllegalArgumentException("Role déjà existant (libelle=" + role.getLibelle() + ")");
        }

        roleRepo.create(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        if (role == null) throw new IllegalArgumentException("role ne doit pas être null");
        if (role.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");

        Role old = roleRepo.findById(role.getId());
        if (old == null) throw new IllegalArgumentException("Role introuvable (id=" + role.getId() + ")");

        roleRepo.update(role);
        return role;
    }

    @Override
    public void deleteRole(Long roleId) {
        if (roleId == null) throw new IllegalArgumentException("roleId ne doit pas être null");
        roleRepo.deleteById(roleId);
    }

    @Override
    public Role getRoleById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        Role r = roleRepo.findById(id);
        if (r == null) throw new IllegalArgumentException("Role introuvable (id=" + id + ")");
        return r;
    }

    @Override
    public Role getRoleByType(RoleType type) {
        if (type == null) throw new IllegalArgumentException("type ne doit pas être null");
        return roleRepo.findByType(type)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable (type=" + type + ")"));
    }

    @Override
    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    @Override
    public Role updateRolePrivileges(Long roleId, List<String> privileges) {
        if (roleId == null) throw new IllegalArgumentException("roleId ne doit pas être null");

        Role r = getRoleById(roleId);

        // Nettoyage: null -> liste vide, suppression blanks, unique
        List<String> cleaned = new ArrayList<>();
        if (privileges != null) {
            for (String p : privileges) {
                if (p != null && !p.isBlank() && !cleaned.contains(p.trim())) cleaned.add(p.trim());
            }
        }

        // stratégie simple: supprimer ceux qui n’existent plus, ajouter les nouveaux
        List<String> current = roleRepo.getPrivileges(roleId);
        if (current != null) {
            for (String old : current) {
                if (!cleaned.contains(old)) roleRepo.removePrivilege(roleId, old);
            }
        }

        for (String p : cleaned) {
            // ajouter si pas présent
            if (current == null || !current.contains(p)) roleRepo.addPrivilege(roleId, p);
        }

        // optionnel : refléter dans l'objet (utile pour toString/retour)
        r.setPrivileges(cleaned);
        return r;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
