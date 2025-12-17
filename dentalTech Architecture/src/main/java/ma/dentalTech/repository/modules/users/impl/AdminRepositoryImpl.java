package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Admin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.AdminRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements AdminRepository {

    private static final String SELECT_BASE = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge
        FROM Utilisateurs u
        JOIN Staffs s ON s.id = u.id
        JOIN Admins a ON a.id = s.id
        """;

    @Override
    public List<Admin> findAll() {
        return findAllOrderByNom();
    }

    @Override
    public Admin findById(Long id) {
        String sql = SELECT_BASE + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapAdmin(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Admin admin) {
        String insertUser = """
            INSERT INTO Utilisateurs(nom,email,adresse,cin,tel,sexe,login,motDePasse,lastLoginDate,dateNaissance,
                                     dateCreation,dateDerniereModification,creePar,modifiePar)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        String insertStaff = """
            INSERT INTO Staffs(id,salaire,prime,dateRecrutement,soldeConge)
            VALUES (?,?,?,?,?)
            """;
        String insertAdmin = "INSERT INTO Admins(id) VALUES (?)";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psS = conn.prepareStatement(insertStaff);
                 PreparedStatement psA = conn.prepareStatement(insertAdmin)) {

                psU.setString(1, admin.getNom());
                psU.setString(2, admin.getEmail());
                psU.setString(3, admin.getAdresse());
                psU.setString(4, admin.getCin());
                psU.setString(5, admin.getTel());
                psU.setString(6, admin.getSexe().name());
                psU.setString(7, admin.getLogin());
                psU.setString(8, admin.getMotDePasse());

                if (admin.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(admin.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (admin.getDateNaissance() != null) psU.setDate(10, Date.valueOf(admin.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                // dateCreation: laisser DEFAULT si null -> on met une valeur seulement si elle existe
                if (admin.getDateCreation() != null) psU.setDate(11, Date.valueOf(admin.getDateCreation()));
                else psU.setDate(11, Date.valueOf(java.time.LocalDate.now()));

                psU.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(13, admin.getCreePar());
                psU.setString(14, admin.getModifiePar());

                psU.executeUpdate();
                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) admin.setId(keys.getLong(1));
                }

                psS.setLong(1, admin.getId());
                if (admin.getSalaire() != null) psS.setDouble(2, admin.getSalaire()); else psS.setNull(2, Types.DECIMAL);
                if (admin.getPrime() != null) psS.setDouble(3, admin.getPrime()); else psS.setNull(3, Types.DECIMAL);
                if (admin.getDateRecrutement() != null) psS.setDate(4, Date.valueOf(admin.getDateRecrutement())); else psS.setNull(4, Types.DATE);
                if (admin.getSoldeCongé() != null) psS.setInt(5, admin.getSoldeCongé()); else psS.setNull(5, Types.INTEGER);
                psS.executeUpdate();

                psA.setLong(1, admin.getId());
                psA.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Admin admin) {
        String updateUser = """
            UPDATE Utilisateurs SET nom=?, email=?, adresse=?, cin=?, tel=?, sexe=?, login=?, motDePasse=?,
                                   lastLoginDate=?, dateNaissance=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;
        String updateStaff = """
            UPDATE Staffs SET salaire=?, prime=?, dateRecrutement=?, soldeConge=?
            WHERE id=?
            """;

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(updateUser);
                 PreparedStatement psS = conn.prepareStatement(updateStaff)) {

                psU.setString(1, admin.getNom());
                psU.setString(2, admin.getEmail());
                psU.setString(3, admin.getAdresse());
                psU.setString(4, admin.getCin());
                psU.setString(5, admin.getTel());
                psU.setString(6, admin.getSexe().name());
                psU.setString(7, admin.getLogin());
                psU.setString(8, admin.getMotDePasse());

                if (admin.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(admin.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (admin.getDateNaissance() != null) psU.setDate(10, Date.valueOf(admin.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(12, admin.getCreePar());
                psU.setString(13, admin.getModifiePar());
                psU.setLong(14, admin.getId());
                psU.executeUpdate();

                if (admin.getSalaire() != null) psS.setDouble(1, admin.getSalaire()); else psS.setNull(1, Types.DECIMAL);
                if (admin.getPrime() != null) psS.setDouble(2, admin.getPrime()); else psS.setNull(2, Types.DECIMAL);
                if (admin.getDateRecrutement() != null) psS.setDate(3, Date.valueOf(admin.getDateRecrutement())); else psS.setNull(3, Types.DATE);
                if (admin.getSoldeCongé() != null) psS.setInt(4, admin.getSoldeCongé()); else psS.setNull(4, Types.INTEGER);
                psS.setLong(5, admin.getId());
                psS.executeUpdate();

                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Admin admin) {
        if (admin != null) deleteById(admin.getId());
    }

    @Override
    public void deleteById(Long id) {
        // ON DELETE CASCADE => supprimer de Utilisateurs suffit
        String sql = "DELETE FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Extras --------

    @Override
    public List<Admin> findAllOrderByNom() {
        String sql = SELECT_BASE + " ORDER BY u.nom";
        List<Admin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapAdmin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        String sql = SELECT_BASE + " WHERE u.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapAdmin(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
