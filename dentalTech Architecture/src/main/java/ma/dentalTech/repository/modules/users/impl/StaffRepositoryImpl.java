package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.StaffRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

    private static final String SELECT_BASE = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge
        FROM Utilisateurs u
        JOIN Staffs s ON s.id = u.id
        """;

    // ---------------- CRUD ----------------

    @Override
    public List<Staff> findAll() {
        return findAllOrderByNom();
    }

    @Override
    public Staff findById(Long id) {
        String sql = SELECT_BASE + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStaff(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Staff staff) {
        // Staff = Utilisateurs + Staffs => transaction (2 inserts liés)
        String insertUser = """
            INSERT INTO Utilisateurs(
                nom,email,adresse,cin,tel,sexe,
                login,motDePasse,lastLoginDate,dateNaissance,
                dateCreation,dateDerniereModification,creePar,modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;

        String insertStaff = """
            INSERT INTO Staffs(id, salaire, prime, dateRecrutement, soldeConge)
            VALUES (?,?,?,?,?)
            """;

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psS = conn.prepareStatement(insertStaff)) {

                // --- Utilisateurs ---
                psU.setString(1, staff.getNom());
                psU.setString(2, staff.getEmail());
                psU.setString(3, staff.getAdresse());
                psU.setString(4, staff.getCin());
                psU.setString(5, staff.getTel());
                psU.setString(6, staff.getSexe().name());

                psU.setString(7, staff.getLogin());
                psU.setString(8, staff.getMotDePasse());

                if (staff.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(staff.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (staff.getDateNaissance() != null) psU.setDate(10, Date.valueOf(staff.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setDate(11, Date.valueOf(staff.getDateCreation() != null ? staff.getDateCreation() : LocalDate.now()));
                psU.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(13, staff.getCreePar());
                psU.setString(14, staff.getModifiePar());

                psU.executeUpdate();
                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) staff.setId(keys.getLong(1));
                }

                // --- Staffs ---
                psS.setLong(1, staff.getId());
                if (staff.getSalaire() != null) psS.setDouble(2, staff.getSalaire()); else psS.setNull(2, Types.DECIMAL);
                if (staff.getPrime() != null) psS.setDouble(3, staff.getPrime()); else psS.setNull(3, Types.DECIMAL);
                if (staff.getDateRecrutement() != null) psS.setDate(4, Date.valueOf(staff.getDateRecrutement())); else psS.setNull(4, Types.DATE);
                if (staff.getSoldeCongé() != null) psS.setInt(5, staff.getSoldeCongé()); else psS.setNull(5, Types.INTEGER);

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
    public void update(Staff staff) {
        String updateUser = """
            UPDATE Utilisateurs SET
                nom=?, email=?, adresse=?, cin=?, tel=?, sexe=?,
                login=?, motDePasse=?, lastLoginDate=?, dateNaissance=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
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

                // --- Utilisateurs ---
                psU.setString(1, staff.getNom());
                psU.setString(2, staff.getEmail());
                psU.setString(3, staff.getAdresse());
                psU.setString(4, staff.getCin());
                psU.setString(5, staff.getTel());
                psU.setString(6, staff.getSexe().name());

                psU.setString(7, staff.getLogin());
                psU.setString(8, staff.getMotDePasse());

                if (staff.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(staff.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (staff.getDateNaissance() != null) psU.setDate(10, Date.valueOf(staff.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(12, staff.getCreePar());
                psU.setString(13, staff.getModifiePar());
                psU.setLong(14, staff.getId());

                psU.executeUpdate();

                // --- Staffs ---
                if (staff.getSalaire() != null) psS.setDouble(1, staff.getSalaire()); else psS.setNull(1, Types.DECIMAL);
                if (staff.getPrime() != null) psS.setDouble(2, staff.getPrime()); else psS.setNull(2, Types.DECIMAL);
                if (staff.getDateRecrutement() != null) psS.setDate(3, Date.valueOf(staff.getDateRecrutement())); else psS.setNull(3, Types.DATE);
                if (staff.getSoldeCongé() != null) psS.setInt(4, staff.getSoldeCongé()); else psS.setNull(4, Types.INTEGER);
                psS.setLong(5, staff.getId());

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
    public void delete(Staff staff) {
        if (staff != null) deleteById(staff.getId());
    }

    @Override
    public void deleteById(Long id) {
        // ON DELETE CASCADE sur Staffs/Admins/Medecins/Secretaires (normalement) => supprimer Utilisateurs suffit
        String sql = "DELETE FROM Utilisateurs WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Extras ----------------

    @Override
    public List<Staff> findAllOrderByNom() {
        String sql = SELECT_BASE + " ORDER BY u.nom";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapStaff(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Staff> findBySalaireBetween(Double min, Double max) {
        String sql = SELECT_BASE + " WHERE s.salaire BETWEEN ? AND ? ORDER BY u.nom";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Staff> findByDateRecrutementAfter(LocalDate date) {
        String sql = SELECT_BASE + " WHERE s.dateRecrutement > ? ORDER BY s.dateRecrutement DESC";
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
