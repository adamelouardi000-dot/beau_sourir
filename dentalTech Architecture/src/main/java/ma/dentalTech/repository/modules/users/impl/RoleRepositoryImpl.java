package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.RoleType;
import ma.dentalTech.entities.users.Role;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.RoleRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepositoryImpl implements RoleRepository {

    // ---------------- CRUD ----------------

    @Override
    public List<Role> findAll() {
        String sql = "SELECT * FROM Roles ORDER BY type, libelle";
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapRole(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Role findById(Long id) {
        String sql = "SELECT * FROM Roles WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRole(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Role r) {
        String sql = """
            INSERT INTO Roles(libelle, type, dateCreation, dateDerniereModification, creePar, modifiePar)
            VALUES (?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getLibelle());
            ps.setString(2, r.getType().name());

            LocalDate dc = r.getDateCreation() != null ? r.getDateCreation() : LocalDate.now();
            ps.setDate(3, Date.valueOf(dc));
            r.setDateCreation(dc);

            LocalDateTime dm = r.getDateDerniereModification() != null ? r.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dm));
            r.setDateDerniereModification(dm);

            ps.setString(5, r.getCreePar());
            ps.setString(6, r.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Role r) {
        String sql = """
            UPDATE Roles SET libelle=?, type=?, dateCreation=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, r.getLibelle());
            ps.setString(2, r.getType().name());

            LocalDate dc = r.getDateCreation() != null ? r.getDateCreation() : LocalDate.now();
            ps.setDate(3, Date.valueOf(dc));
            r.setDateCreation(dc);

            LocalDateTime dm = LocalDateTime.now();
            ps.setTimestamp(4, Timestamp.valueOf(dm));
            r.setDateDerniereModification(dm);

            ps.setString(5, r.getCreePar());
            ps.setString(6, r.getModifiePar());

            ps.setLong(7, r.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Role r) {
        if (r != null) deleteById(r.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Roles WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Queries ----------------

    @Override
    public Optional<Role> findByLibelle(String libelle) {
        String sql = "SELECT * FROM Roles WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRole(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Role> findByType(RoleType type) {
        String sql = "SELECT * FROM Roles WHERE type = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapRole(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<String> getPrivileges(Long roleId) {
        String sql = "SELECT privilege FROM Role_Privileges WHERE role_id = ? ORDER BY privilege";
        List<String> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(rs.getString("privilege"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public void addPrivilege(Long roleId, String privilege) {
        String sql = "INSERT INTO Role_Privileges(role_id, privilege) VALUES(?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ps.setString(2, privilege);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removePrivilege(Long roleId, String privilege) {
        String sql = "DELETE FROM Role_Privileges WHERE role_id=? AND privilege=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, roleId);
            ps.setString(2, privilege);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByLibelle(String libelle) {
        String sql = "SELECT 1 FROM Roles WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Role> findRolesByUtilisateurId(Long utilisateurId) {
        String sql = """
            SELECT r.*
            FROM Roles r
            JOIN Utilisateur_Roles ur ON ur.role_id = r.id
            WHERE ur.utilisateur_id = ?
            ORDER BY r.type, r.libelle
            """;
        List<Role> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRole(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public void assignRoleToUser(Long utilisateurId, Long roleId) {
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
}
