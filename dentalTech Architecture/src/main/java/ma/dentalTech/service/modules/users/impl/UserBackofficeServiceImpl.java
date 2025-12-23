package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.service.modules.users.api.UserBackofficeService;
import ma.dentalTech.mvc.dto.*;

import java.util.List;

public class UserBackofficeServiceImpl implements UserBackofficeService {

    private final UtilisateurRepository userRepo;
    private final RoleRepository roleRepo;

    public UserBackofficeServiceImpl(UtilisateurRepository userRepo, RoleRepository roleRepo) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userRepo.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserDto findUserById(Long id) {
        Utilisateur u = userRepo.findById(id);
        if (u == null) throw new RuntimeException("Utilisateur introuvable id=" + id);
        return toDto(u);
    }

    @Override
    public UserDto createUser(UserCreateRequest r) {
        validateCreate(r);

        Utilisateur u = new Utilisateur();
        u.setNom(r.nom());
        u.setEmail(r.email());
        u.setAdresse(r.adresse());
        u.setCin(r.cin());
        u.setTel(r.tel());
        u.setSexe(r.sexe());
        u.setLogin(r.login());
        u.setMotDePasse(r.motDePasse()); // (si tu as hash, tu hashes ici)
        u.setDateNaissance(r.dateNaissance());

        userRepo.create(u);
        return toDto(u);
    }

    @Override
    public UserDto updateUser(UserUpdateRequest r) {
        validateUpdate(r);

        Utilisateur existing = userRepo.findById(r.id());
        if (existing == null) throw new RuntimeException("Utilisateur introuvable id=" + r.id());

        existing.setNom(r.nom());
        existing.setEmail(r.email());
        existing.setAdresse(r.adresse());
        existing.setCin(r.cin());
        existing.setTel(r.tel());
        existing.setSexe(r.sexe());
        existing.setLogin(r.login());
        existing.setDateNaissance(r.dateNaissance());

        userRepo.update(existing);
        return toDto(existing);
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");
        if (newPassword == null || newPassword.isBlank()) throw new IllegalArgumentException("newPassword obligatoire");

        Utilisateur u = userRepo.findById(userId);
        if (u == null) throw new RuntimeException("Utilisateur introuvable id=" + userId);

        u.setMotDePasse(newPassword); // hash si nécessaire
        userRepo.update(u);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");
        if (oldPassword == null || oldPassword.isBlank()) throw new IllegalArgumentException("oldPassword obligatoire");
        if (newPassword == null || newPassword.isBlank()) throw new IllegalArgumentException("newPassword obligatoire");

        Utilisateur u = userRepo.findById(userId);
        if (u == null) throw new RuntimeException("Utilisateur introuvable id=" + userId);

        if (!oldPassword.equals(u.getMotDePasse())) {
            throw new IllegalArgumentException("Ancien mot de passe incorrect");
        }

        u.setMotDePasse(newPassword);
        userRepo.update(u);
    }

    @Override
    public List<RoleDto> getUserRoles(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");
        return userRepo.findRolesByUserId(userId).stream().map(this::toRoleDto).toList();
    }

    @Override
    public void assignRole(AssignRoleRequest r) {
        if (r == null) throw new IllegalArgumentException("AssignRoleRequest null");
        if (r.utilisateurId() == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (r.roleId() == null) throw new IllegalArgumentException("roleId obligatoire");

        Utilisateur u = userRepo.findById(r.utilisateurId());
        if (u == null) throw new RuntimeException("Utilisateur introuvable id=" + r.utilisateurId());

        Role role = roleRepo.findById(r.roleId());
        if (role == null) throw new RuntimeException("Role introuvable id=" + r.roleId());

        userRepo.addRoleToUser(r.utilisateurId(), r.roleId());
    }

    @Override
    public void removeRole(AssignRoleRequest r) {
        if (r == null) throw new IllegalArgumentException("AssignRoleRequest null");
        if (r.utilisateurId() == null) throw new IllegalArgumentException("utilisateurId obligatoire");
        if (r.roleId() == null) throw new IllegalArgumentException("roleId obligatoire");

        userRepo.removeRoleFromUser(r.utilisateurId(), r.roleId());
    }

    private UserDto toDto(Utilisateur u) {
        return new UserDto(
                u.getId(),
                u.getNom(),
                u.getEmail(),
                u.getAdresse(),
                u.getCin(),
                u.getTel(),
                u.getSexe(),
                u.getLogin(),
                u.getLastLoginDate(),
                u.getDateNaissance()
        );
    }

    private RoleDto toRoleDto(Role r) {
        // Si ton RoleRepository expose findPrivileges(roleId) : utilise ça.
        List<String> privileges = roleRepo.findPrivilegesByRoleId(r.getId());
        return new RoleDto(r.getId(), r.getLibelle(), r.getType(), privileges);
    }

    private void validateCreate(UserCreateRequest r) {
        if (r == null) throw new IllegalArgumentException("UserCreateRequest null");
        if (r.nom() == null || r.nom().isBlank()) throw new IllegalArgumentException("nom obligatoire");
        if (r.email() == null || r.email().isBlank()) throw new IllegalArgumentException("email obligatoire");
        if (r.login() == null || r.login().isBlank()) throw new IllegalArgumentException("login obligatoire");
        if (r.motDePasse() == null || r.motDePasse().isBlank()) throw new IllegalArgumentException("motDePasse obligatoire");
        if (r.sexe() == null) throw new IllegalArgumentException("sexe obligatoire");
    }

    private void validateUpdate(UserUpdateRequest r) {
        if (r == null) throw new IllegalArgumentException("UserUpdateRequest null");
        if (r.id() == null) throw new IllegalArgumentException("id obligatoire");
        if (r.nom() == null || r.nom().isBlank()) throw new IllegalArgumentException("nom obligatoire");
        if (r.email() == null || r.email().isBlank()) throw new IllegalArgumentException("email obligatoire");
        if (r.login() == null || r.login().isBlank()) throw new IllegalArgumentException("login obligatoire");
        if (r.sexe() == null) throw new IllegalArgumentException("sexe obligatoire");
    }
}
