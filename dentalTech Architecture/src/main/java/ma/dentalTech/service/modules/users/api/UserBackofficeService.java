package ma.dentalTech.service.modules.users.api;

import ma.dentalTech.mvc.dto.*;

import java.util.List;

public interface UserBackofficeService {

    List<UserDto> findAllUsers();
    UserDto findUserById(Long id);

    UserDto createUser(UserCreateRequest request);
    UserDto updateUser(UserUpdateRequest request);

    void deleteUser(Long id);

    // Back-office (use cases)
    void resetPassword(Long userId, String newPassword);
    void changePassword(Long userId, String oldPassword, String newPassword);

    List<RoleDto> getUserRoles(Long userId);
    void assignRole(AssignRoleRequest request);
    void removeRole(AssignRoleRequest request);
}
