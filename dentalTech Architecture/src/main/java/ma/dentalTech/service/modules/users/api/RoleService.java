package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.mvc.dto.*;

import java.util.List;

public interface RoleService {

    List<RoleDto> findAll();
    RoleDto findById(Long id);

    RoleDto create(RoleCreateRequest request);
    RoleDto update(RoleUpdateRequest request);
    void deleteById(Long id);

    void grantPrivilege(GrantPrivilegeRequest request);
    void revokePrivilege(RevokePrivilegeRequest request);
}
