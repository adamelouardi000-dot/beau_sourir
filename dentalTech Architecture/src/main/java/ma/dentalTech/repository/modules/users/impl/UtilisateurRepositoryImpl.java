package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.UtilisateurRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilisateurRepositoryImpl implements UtilisateurRepository {

    // ---------------- CRUD ----------------

    @Override
    public List<Utilisateur> findAll() {
        String sql = "SELECT * FROM Utilisateurs ORDER BY nom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Utilisateur findById(Long id) {
        String sql = "SELECT * FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapUtilisateur(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Utilisateur u) {
        String sql = """
            INSERT INTO Utilisateurs(
                nom, email, adresse, cin, tel, sexe,
                login, motDePasse, lastLoginDate, dateNaissance,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getAdresse());
            ps.setString(4, u.getCin());
            ps.setString(5, u.getTel());
            ps.setString(6, u.getSexe().name());

            ps.setString(7, u.getLogin());
            ps.setString(8, u.getMotDePasse());

            if (u.getLastLoginDate() != null) ps.setDate(9, Date.valueOf(u.getLastLoginDate()));
            else ps.setNull(9, Types.DATE);

            if (u.getDateNaissance() != null) ps.setDate(10, Date.valueOf(u.getDateNaissance()));
            else ps.setNull(10, Types.DATE);

            LocalDate dc = u.getDateCreation() != null ? u.getDateCreation() : LocalDate.now();
            ps.setDate(11, Date.valueOf(dc));
            u.setDateCreation(dc);

            LocalDateTime dm = u.getDateDerniereModification() != null ? u.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(12, Timestamp.valueOf(dm));
            u.setDateDerniereModification(dm);

            ps.setString(13, u.getCreePar());
            ps.setString(14, u.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Utilisateur u) {
        String sql = """
            UPDATE Utilisateurs SET
                nom=?, email=?, adresse=?, cin=?, tel=?, sexe=?,
                login=?, motDePasse=?, lastLoginDate=?, dateNaissance=?,
                dateCreation=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getAdresse());
            ps.setString(4, u.getCin());
            ps.setString(5, u.getTel());
            ps.setString(6, u.getSexe().name());

            ps.setString(7, u.getLogin());
            ps.setString(8, u.getMotDePasse());

            if (u.getLastLoginDate() != null) ps.setDate(9, Date.valueOf(u.getLastLoginDate()));
            else ps.setNull(9, Types.DATE);

            if (u.getDateNaissance() != null) ps.setDate(10, Date.valueOf(u.getDateNaissance()));
            else ps.setNull(10, Types.DATE);

            LocalDate dc = u.getDateCreation() != null ? u.getDateCreation() : LocalDate.now();
            ps.setDate(11, Date.valueOf(dc));
            u.setDateCreation(dc);

            LocalDateTime dm = LocalDateTime.now();
            ps.setTimestamp(12, Timestamp.valueOf(dm));
            u.setDateDerniereModification(dm);

            ps.setString(13, u.getCreePar());
            ps.setString(14, u.getModifiePar());

            ps.setLong(15, u.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Utilisateur u) {
        if (u != null) deleteById(u.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Extras ----------------

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        String sql = "SELECT * FROM Utilisateurs WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Utilisateur> findByLogin(String login) {
        String sql = "SELECT * FROM Utilisateurs WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapUtilisateur(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM Utilisateurs WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByLogin(String login) {
        String sql = "SELECT 1 FROM Utilisateurs WHERE login = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Utilisateur> searchByNom(String keyword) {
        String sql = "SELECT * FROM Utilisateurs WHERE nom LIKE ? ORDER BY nom";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Utilisateur> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Utilisateurs ORDER BY nom LIMIT ? OFFSET ?";
        List<Utilisateur> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapUtilisateur(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // ---------------- Rôles ----------------

    @Override
    public List<String> getRoleLibellesOfUser(Long utilisateurId) {
        String sql = """
            SELECT r.libelle
            FROM Roles r
            JOIN Utilisateur_Roles ur ON ur.role_id = r.id
            WHERE ur.utilisateur_id = ?
            ORDER BY r.libelle
            """;
        List<String> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString("libelle"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public void addRoleToUser(Long utilisateurId, Long roleId) {
        String sql = "INSERT INTO Utilisateur_Roles(utilisateur_id, role_id) VALUES(?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeRoleFromUser(Long utilisateurId, Long roleId) {
        String sql = "DELETE FROM Utilisateur_Roles WHERE utilisateur_id=? AND role_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
    @Override
    public List<Role> findRolesByUserId(Long userId) {
        String sql = """
        SELECT r.*
        FROM Roles r
        INNER JOIN Utilisateur_Roles ur ON ur.role_id = r.id
        WHERE ur.utilisateur_id = ?
        """;
        List<Role> out = new java.util.ArrayList<>();
        try (java.sql.Connection c = SessionFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // ✅ remplace mapRole(rs) si tu as déjà un RowMapper Role
                    Role r = new Role();
                    r.setId(rs.getLong("id"));
                    r.setLibelle(rs.getString("libelle"));
                    r.setType(ma.dentalTech.entities.enums.RoleType.valueOf(rs.getString("type")));
                    out.add(r);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur findRolesByUserId userId=" + userId, e);
        }
        return out;
    }



}
