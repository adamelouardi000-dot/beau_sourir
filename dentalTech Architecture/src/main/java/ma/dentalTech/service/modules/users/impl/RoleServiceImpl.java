package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.RoleService;
import ma.dentalTech.mvc.dto.*;

import java.util.List;

public class RoleServiceImpl implements RoleService {

    private final RoleRepository repo;

    public RoleServiceImpl(RoleRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<RoleDto> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public RoleDto findById(Long id) {
        Role r = repo.findById(id);
        if (r == null) throw new RuntimeException("Role introuvable id=" + id);
        return toDto(r);
    }

    @Override
    public RoleDto create(RoleCreateRequest r) {
        validateCreate(r);

        Role role = new Role();
        role.setLibelle(r.libelle());
        role.setType(r.type());

        repo.create(role);

        if (r.privileges() != null) {
            for (String p : r.privileges()) {
                if (p != null && !p.isBlank()) repo.addPrivilege(role.getId(), p.trim());
            }
        }

        return toDto(role);
    }

    @Override
    public RoleDto update(RoleUpdateRequest r) {
        validateUpdate(r);

        Role existing = repo.findById(r.id());
        if (existing == null) throw new RuntimeException("Role introuvable id=" + r.id());

        existing.setLibelle(r.libelle());
        existing.setType(r.type());
        repo.update(existing);

        // stratégie simple : reset privileges puis re-add
        repo.clearPrivileges(r.id());
        if (r.privileges() != null) {
            for (String p : r.privileges()) {
                if (p != null && !p.isBlank()) repo.addPrivilege(r.id(), p.trim());
            }
        }

        return toDto(existing);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public void grantPrivilege(GrantPrivilegeRequest r) {
        if (r == null) throw new IllegalArgumentException("GrantPrivilegeRequest null");
        if (r.roleId() == null) throw new IllegalArgumentException("roleId obligatoire");
        if (r.privilege() == null || r.privilege().isBlank()) throw new IllegalArgumentException("privilege obligatoire");
        repo.addPrivilege(r.roleId(), r.privilege().trim());
    }

    @Override
    public void revokePrivilege(RevokePrivilegeRequest r) {
        if (r == null) throw new IllegalArgumentException("RevokePrivilegeRequest null");
        if (r.roleId() == null) throw new IllegalArgumentException("roleId obligatoire");
        if (r.privilege() == null || r.privilege().isBlank()) throw new IllegalArgumentException("privilege obligatoire");
        repo.removePrivilege(r.roleId(), r.privilege().trim());
    }

    private RoleDto toDto(Role r) {
        List<String> privileges = repo.findPrivilegesByRoleId(r.getId());
        return new RoleDto(r.getId(), r.getLibelle(), r.getType(), privileges);
    }

    private void validateCreate(RoleCreateRequest r) {
        if (r == null) throw new IllegalArgumentException("RoleCreateRequest null");
        if (r.libelle() == null || r.libelle().isBlank()) throw new IllegalArgumentException("libelle obligatoire");
        if (r.type() == null) throw new IllegalArgumentException("type obligatoire");
    }

    private void validateUpdate(RoleUpdateRequest r) {
        if (r == null) throw new IllegalArgumentException("RoleUpdateRequest null");
        if (r.id() == null) throw new IllegalArgumentException("id obligatoire");
        validateCreate(new RoleCreateRequest(r.libelle(), r.type(), r.privileges()));
    }
}
