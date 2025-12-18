package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Secretaire;

import ma.dentalTech.repository.modules.users.api.AdminRepository;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;
import ma.dentalTech.repository.modules.users.api.RoleRepository;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import ma.dentalTech.service.modules.users.api.UserManagementService;
import ma.dentalTech.service.modules.users.dto.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class UserManagementServiceImpl implements UserManagementService {

    private final AdminRepository adminRepo;
    private final MedecinRepository medecinRepo;
    private final SecretaireRepository secretaireRepo;
    private final RoleRepository roleRepo;

    public UserManagementServiceImpl(AdminRepository adminRepo,
                                     MedecinRepository medecinRepo,
                                     SecretaireRepository secretaireRepo,
                                     RoleRepository roleRepo) {
        this.adminRepo = adminRepo;
        this.medecinRepo = medecinRepo;
        this.secretaireRepo = secretaireRepo;
        this.roleRepo = roleRepo;
    }

    // ===== Création comptes =====

    @Override
    public UserAccountDto createAdmin(CreateAdminRequest request) {
        validateCreateCommon(request.nom(), request.email(), request.login(), request.motDePasse(), request.dateNaissance());

        Admin a = Admin.adminBuilder()
                .nom(request.nom())
                .email(request.email())
                .adresse(request.adresse())
                .cin(request.cin())
                .tel(request.tel())
                .sexe(request.sexe())
                .login(request.login())
                .motDePasse(request.motDePasse())
                .lastLoginDate(null)
                .dateNaissance(request.dateNaissance())
                .salaire(request.salaire())
                .prime(request.prime())
                .dateRecrutement(request.dateRecrutement())
                .soldeCongé(request.soldeConge())
                .build();

        adminRepo.create(a);

        // option: assigner rôle ADMIN automatiquement si existant
        safeAssignDefaultRole(a.getId(), RoleType.ADMIN);

        return toDto(a.getId(), a.getNom(), a.getEmail(), a.getLogin(), a.getSexe(), a.getDateNaissance());
    }

    @Override
    public UserAccountDto createMedecin(CreateMedecinRequest request) {
        validateCreateCommon(request.nom(), request.email(), request.login(), request.motDePasse(), request.dateNaissance());
        if (isBlank(request.specialite())) throw new IllegalArgumentException("specialite obligatoire");

        Medecin m = Medecin.medecinBuilder()
                .nom(request.nom())
                .email(request.email())
                .adresse(request.adresse())
                .cin(request.cin())
                .tel(request.tel())
                .sexe(request.sexe())
                .login(request.login())
                .motDePasse(request.motDePasse())
                .lastLoginDate(null)
                .dateNaissance(request.dateNaissance())
                .salaire(request.salaire())
                .prime(request.prime())
                .dateRecrutement(request.dateRecrutement())
                .soldeCongé(request.soldeConge())
                .specialite(request.specialite())
                .agendaMensuel(null)
                .build();

        medecinRepo.create(m);
        safeAssignDefaultRole(m.getId(), RoleType.MEDECIN);

        return toDto(m.getId(), m.getNom(), m.getEmail(), m.getLogin(), m.getSexe(), m.getDateNaissance());
    }

    @Override
    public UserAccountDto createSecretaire(CreateSecretaireRequest request) {
        validateCreateCommon(request.nom(), request.email(), request.login(), request.motDePasse(), request.dateNaissance());
        if (isBlank(request.numCNSS())) throw new IllegalArgumentException("numCNSS obligatoire");

        Secretaire s = Secretaire.secretaireBuilder()
                .nom(request.nom())
                .email(request.email())
                .adresse(request.adresse())
                .cin(request.cin())
                .tel(request.tel())
                .sexe(request.sexe())
                .login(request.login())
                .motDePasse(request.motDePasse())
                .lastLoginDate(null)
                .dateNaissance(request.dateNaissance())
                .salaire(request.salaire())
                .prime(request.prime())
                .dateRecrutement(request.dateRecrutement())
                .soldeCongé(request.soldeConge())
                .numCNSS(request.numCNSS())
                .commission(request.commission())
                .build();

        secretaireRepo.create(s);
        safeAssignDefaultRole(s.getId(), RoleType.SECRETAIRE);

        return toDto(s.getId(), s.getNom(), s.getEmail(), s.getLogin(), s.getSexe(), s.getDateNaissance());
    }

    // ===== Consultation & recherche =====

    @Override
    public UserAccountDto getUserById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");

        // Chercher dans chaque repo (car pas de UserRepository global)
        Admin a = adminRepo.findById(id);
        if (a != null) return toDto(a.getId(), a.getNom(), a.getEmail(), a.getLogin(), a.getSexe(), a.getDateNaissance());

        Medecin m = medecinRepo.findById(id);
        if (m != null) return toDto(m.getId(), m.getNom(), m.getEmail(), m.getLogin(), m.getSexe(), m.getDateNaissance());

        Secretaire s = secretaireRepo.findById(id);
        if (s != null) return toDto(s.getId(), s.getNom(), s.getEmail(), s.getLogin(), s.getSexe(), s.getDateNaissance());

        throw new IllegalArgumentException("Utilisateur introuvable (id=" + id + ")");
    }

    @Override
    public List<UserAccountDto> getAllUsers() {
        List<UserAccountDto> out = new ArrayList<>();

        for (Admin a : safeList(adminRepo.findAll())) {
            out.add(toDto(a.getId(), a.getNom(), a.getEmail(), a.getLogin(), a.getSexe(), a.getDateNaissance()));
        }
        for (Medecin m : safeList(medecinRepo.findAll())) {
            out.add(toDto(m.getId(), m.getNom(), m.getEmail(), m.getLogin(), m.getSexe(), m.getDateNaissance()));
        }
        for (Secretaire s : safeList(secretaireRepo.findAll())) {
            out.add(toDto(s.getId(), s.getNom(), s.getEmail(), s.getLogin(), s.getSexe(), s.getDateNaissance()));
        }

        return out;
    }

    @Override
    public List<UserAccountDto> searchUsersByKeyword(String keyword) {
        if (isBlank(keyword)) return getAllUsers();
        String k = keyword.trim().toLowerCase(Locale.ROOT);

        return getAllUsers().stream()
                .filter(u ->
                        (u.nom() != null && u.nom().toLowerCase(Locale.ROOT).contains(k)) ||
                                (u.email() != null && u.email().toLowerCase(Locale.ROOT).contains(k)) ||
                                (u.login() != null && u.login().toLowerCase(Locale.ROOT).contains(k))
                )
                .collect(Collectors.toList());
    }

    // ===== Mise à jour profil =====

    @Override
    public UserAccountDto updateUserProfile(UpdateUserProfileRequest request) {
        if (request == null) throw new IllegalArgumentException("request ne doit pas être null");
        if (request.id() == null) throw new IllegalArgumentException("id obligatoire");

        // On update dans le repo où on trouve l'utilisateur
        Admin a = adminRepo.findById(request.id());
        if (a != null) {
            applyProfile(a, request.nom(), request.email(), request.adresse(), request.tel(), request.sexe(), request.dateNaissance());
            adminRepo.update(a);
            return toDto(a.getId(), a.getNom(), a.getEmail(), a.getLogin(), a.getSexe(), a.getDateNaissance());
        }

        Medecin m = medecinRepo.findById(request.id());
        if (m != null) {
            applyProfile(m, request.nom(), request.email(), request.adresse(), request.tel(), request.sexe(), request.dateNaissance());
            medecinRepo.update(m);
            return toDto(m.getId(), m.getNom(), m.getEmail(), m.getLogin(), m.getSexe(), m.getDateNaissance());
        }

        Secretaire s = secretaireRepo.findById(request.id());
        if (s != null) {
            applyProfile(s, request.nom(), request.email(), request.adresse(), request.tel(), request.sexe(), request.dateNaissance());
            secretaireRepo.update(s);
            return toDto(s.getId(), s.getNom(), s.getEmail(), s.getLogin(), s.getSexe(), s.getDateNaissance());
        }

        throw new IllegalArgumentException("Utilisateur introuvable (id=" + request.id() + ")");
    }

    // ===== Gestion des rôles =====

    @Override
    public void assignRoleToUser(Long utilisateurId, RoleType roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (roleType == null) throw new IllegalArgumentException("roleType ne doit pas être null");

        Role role = roleRepo.findByType(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable (type=" + roleType + ")"));

        roleRepo.assignRoleToUser(utilisateurId, role.getId());
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, RoleType roleType) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (roleType == null) throw new IllegalArgumentException("roleType ne doit pas être null");

        Role role = roleRepo.findByType(roleType)
                .orElseThrow(() -> new IllegalArgumentException("Role introuvable (type=" + roleType + ")"));

        roleRepo.removeRoleFromUser(utilisateurId, role.getId());
    }

    // ===== Helpers =====

    private void safeAssignDefaultRole(Long userId, RoleType type) {
        try {
            Role r = roleRepo.findByType(type).orElse(null);
            if (r != null) roleRepo.assignRoleToUser(userId, r.getId());
        } catch (Exception ignored) {}
    }

    private UserAccountDto toDto(Long id, String nom, String email, String login,
                                 ma.dentalTech.entities.enums.Sexe sexe, LocalDate dateNaissance) {

        Set<RoleType> roles = new HashSet<>();
        Set<String> privileges = new HashSet<>();

        List<Role> rs = roleRepo.findRolesByUtilisateurId(id);
        if (rs != null) {
            for (Role r : rs) {
                if (r.getType() != null) roles.add(r.getType());
                List<String> privs = roleRepo.getPrivileges(r.getId());
                if (privs != null) privileges.addAll(privs);
            }
        }

        return new UserAccountDto(id, nom, email, login, sexe, dateNaissance, roles, privileges);
    }

    private static <T> List<T> safeList(List<T> l) {
        return l == null ? List.of() : l;
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static void validateCreateCommon(String nom, String email, String login, String mdp, LocalDate dn) {
        if (isBlank(nom)) throw new IllegalArgumentException("nom obligatoire");
        if (isBlank(email)) throw new IllegalArgumentException("email obligatoire");
        if (isBlank(login)) throw new IllegalArgumentException("login obligatoire");
        if (isBlank(mdp)) throw new IllegalArgumentException("motDePasse obligatoire");
        if (dn == null) throw new IllegalArgumentException("dateNaissance obligatoire");
    }

    // Staff est une super-classe de Admin/Medecin/Secretaire (dans ton projet)
    private static void applyProfile(Object user, String nom, String email, String adresse, String tel,
                                     ma.dentalTech.entities.enums.Sexe sexe, LocalDate dateNaissance) {
        // On utilise les setters présents sur Staff (hérité)
        // -> pour éviter de dépendre d’une classe Utilisateur inexistante ici.
        tryInvoke(user, "setNom", String.class, nom);
        tryInvoke(user, "setEmail", String.class, email);
        tryInvoke(user, "setAdresse", String.class, adresse);
        tryInvoke(user, "setTel", String.class, tel);
        tryInvoke(user, "setSexe", ma.dentalTech.entities.enums.Sexe.class, sexe);
        tryInvoke(user, "setDateNaissance", LocalDate.class, dateNaissance);
    }

    private static void tryInvoke(Object obj, String method, Class<?> type, Object value) {
        try {
            if (value == null) return;
            obj.getClass().getMethod(method, type).invoke(obj, value);
        } catch (Exception ignored) {}
    }
}
