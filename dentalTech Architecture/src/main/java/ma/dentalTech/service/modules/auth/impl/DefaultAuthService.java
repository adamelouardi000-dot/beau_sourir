package ma.dentalTech.service.modules.auth.impl;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.entities.enums.Sexe;              // ✅ AJOUT
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.mvc.dto.AuthResponse;
import ma.dentalTech.mvc.dto.LoginRequest;
import ma.dentalTech.mvc.dto.RegisterRequest;
import ma.dentalTech.mvc.dto.UserResponse;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;
import ma.dentalTech.service.modules.auth.api.AuthService;
import ma.dentalTech.service.modules.auth.api.CredentialsValidator;
import ma.dentalTech.service.modules.auth.api.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

public class DefaultAuthService implements AuthService {

    private UtilisateurRepository utilisateurRepository;
    private PasswordEncoder passwordEncoder;
    private CredentialsValidator validator;

    // ✅ CONSTRUCTEUR VIDE – FIX FINAL
    public DefaultAuthService() {
        this.utilisateurRepository =
                ApplicationContext.getBean(UtilisateurRepository.class);

        this.validator = new DefaultCredentialsValidator();
        this.passwordEncoder = new DefaultPasswordEncoder();
    }

    // constructeur injection (optionnel)
    public DefaultAuthService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              CredentialsValidator validator) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.validator = validator;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        validator.validateLogin(request);

        Utilisateur user = utilisateurRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new SecurityException("Login incorrect"));

        PasswordEncoder.VerifyResult vr =
                passwordEncoder.verify(request.getMotDePasse(), user.getMotDePasse());

        if (!vr.isOk()) throw new SecurityException("Mot de passe incorrect");

        if (vr.isNeedsRehash()) {
            user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
            utilisateurRepository.update(user);
        }

        user.setLastLoginDate(LocalDate.now());
        utilisateurRepository.update(user);

        return AuthResponse.builder()
                .userId(user.getId())
                .nomComplet(user.getNom())
                .email(user.getEmail())
                .roles(Set.of())
                .privileges(Set.of())
                .message("AUTH_OK")
                .build();
    }

    @Override
    public UserResponse register(RegisterRequest request) {
        validator.validateRegister(request);

        if (utilisateurRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");

        String login = request.getEmail();
        if (utilisateurRepository.existsByLogin(login))
            throw new IllegalArgumentException("Login déjà utilisé");

        Utilisateur user = new Utilisateur();
        user.setNom(request.getNom());
        user.setEmail(request.getEmail());
        user.setLogin(login);
        user.setTel(request.getTelephone());
        user.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        user.setLastLoginDate(LocalDate.now());

        // ✅ AJOUT OBLIGATOIRE (évite getSexe().name() == null)
        user.setSexe(Sexe.Homme); // ou Sexe.FEMME

        utilisateurRepository.create(user);

        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(request.getTelephone())
                .actif(true)
                .lastLoginDate(user.getLastLoginDate())
                .build();
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        validator.validateNewPassword(newPassword);

        Utilisateur user = utilisateurRepository.findById(userId);
        if (user == null) throw new IllegalArgumentException("Utilisateur introuvable");

        PasswordEncoder.VerifyResult vr =
                passwordEncoder.verify(oldPassword, user.getMotDePasse());

        if (!vr.isOk()) throw new SecurityException("Ancien mot de passe incorrect");

        user.setMotDePasse(passwordEncoder.encode(newPassword));
        utilisateurRepository.update(user);
    }
}
