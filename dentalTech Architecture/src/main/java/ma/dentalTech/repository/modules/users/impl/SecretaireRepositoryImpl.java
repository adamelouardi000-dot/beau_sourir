package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Secretaire;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    private static final String SELECT_BASE = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge,
               sec.numCNSS, sec.commission
        FROM Utilisateurs u
        JOIN Staffs s ON s.id = u.id
        JOIN Secretaires sec ON sec.id = s.id
        """;

    @Override
    public List<Secretaire> findAll() {
        return findAllOrderByNom();
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = SELECT_BASE + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapSecretaire(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Secretaire sct) {
        String insertUser = """
            INSERT INTO Utilisateurs(nom,email,adresse,cin,tel,sexe,login,motDePasse,lastLoginDate,dateNaissance,
                                     dateCreation,dateDerniereModification,creePar,modifiePar)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        String insertStaff = """
            INSERT INTO Staffs(id,salaire,prime,dateRecrutement,soldeConge)
            VALUES (?,?,?,?,?)
            """;
        String insertSec = "INSERT INTO Secretaires(id, numCNSS, commission) VALUES (?,?,?)";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psS = conn.prepareStatement(insertStaff);
                 PreparedStatement psC = conn.prepareStatement(insertSec)) {

                psU.setString(1, sct.getNom());
                psU.setString(2, sct.getEmail());
                psU.setString(3, sct.getAdresse());
                psU.setString(4, sct.getCin());
                psU.setString(5, sct.getTel());
                psU.setString(6, sct.getSexe().name());
                psU.setString(7, sct.getLogin());
                psU.setString(8, sct.getMotDePasse());

                if (sct.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(sct.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (sct.getDateNaissance() != null) psU.setDate(10, Date.valueOf(sct.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setDate(11, Date.valueOf(java.time.LocalDate.now()));
                psU.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(13, sct.getCreePar());
                psU.setString(14, sct.getModifiePar());

                psU.executeUpdate();
                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) sct.setId(keys.getLong(1));
                }

                psS.setLong(1, sct.getId());
                if (sct.getSalaire() != null) psS.setDouble(2, sct.getSalaire()); else psS.setNull(2, Types.DECIMAL);
                if (sct.getPrime() != null) psS.setDouble(3, sct.getPrime()); else psS.setNull(3, Types.DECIMAL);
                if (sct.getDateRecrutement() != null) psS.setDate(4, Date.valueOf(sct.getDateRecrutement())); else psS.setNull(4, Types.DATE);
                if (sct.getSoldeCongé() != null) psS.setInt(5, sct.getSoldeCongé()); else psS.setNull(5, Types.INTEGER);
                psS.executeUpdate();

                psC.setLong(1, sct.getId());
                psC.setString(2, sct.getNumCNSS());
                if (sct.getCommission() != null) psC.setDouble(3, sct.getCommission()); else psC.setNull(3, Types.DECIMAL);
                psC.executeUpdate();

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
    public void update(Secretaire sct) {
        String updateUser = """
            UPDATE Utilisateurs SET nom=?, email=?, adresse=?, cin=?, tel=?, sexe=?, login=?, motDePasse=?,
                                   lastLoginDate=?, dateNaissance=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;
        String updateStaff = """
            UPDATE Staffs SET salaire=?, prime=?, dateRecrutement=?, soldeConge=?
            WHERE id=?
            """;
        String updateSec = "UPDATE Secretaires SET numCNSS=?, commission=? WHERE id=?";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(updateUser);
                 PreparedStatement psS = conn.prepareStatement(updateStaff);
                 PreparedStatement psC = conn.prepareStatement(updateSec)) {

                psU.setString(1, sct.getNom());
                psU.setString(2, sct.getEmail());
                psU.setString(3, sct.getAdresse());
                psU.setString(4, sct.getCin());
                psU.setString(5, sct.getTel());
                psU.setString(6, sct.getSexe().name());
                psU.setString(7, sct.getLogin());
                psU.setString(8, sct.getMotDePasse());

                if (sct.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(sct.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (sct.getDateNaissance() != null) psU.setDate(10, Date.valueOf(sct.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(12, sct.getCreePar());
                psU.setString(13, sct.getModifiePar());
                psU.setLong(14, sct.getId());
                psU.executeUpdate();

                if (sct.getSalaire() != null) psS.setDouble(1, sct.getSalaire()); else psS.setNull(1, Types.DECIMAL);
                if (sct.getPrime() != null) psS.setDouble(2, sct.getPrime()); else psS.setNull(2, Types.DECIMAL);
                if (sct.getDateRecrutement() != null) psS.setDate(3, Date.valueOf(sct.getDateRecrutement())); else psS.setNull(3, Types.DATE);
                if (sct.getSoldeCongé() != null) psS.setInt(4, sct.getSoldeCongé()); else psS.setNull(4, Types.INTEGER);
                psS.setLong(5, sct.getId());
                psS.executeUpdate();

                psC.setString(1, sct.getNumCNSS());
                if (sct.getCommission() != null) psC.setDouble(2, sct.getCommission()); else psC.setNull(2, Types.DECIMAL);
                psC.setLong(3, sct.getId());
                psC.executeUpdate();

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
    public void delete(Secretaire sct) {
        if (sct != null) deleteById(sct.getId());
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

    // -------- Extras --------

    @Override
    public List<Secretaire> findAllOrderByNom() {
        String sql = SELECT_BASE + " ORDER BY u.nom";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Secretaire> findByNumCNSS(String numCNSS) {
        String sql = SELECT_BASE + " WHERE sec.numCNSS = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numCNSS);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapSecretaire(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Secretaire> findByCommissionMin(Double minCommission) {
        String sql = SELECT_BASE + " WHERE sec.commission >= ? ORDER BY u.nom";
        List<Secretaire> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, minCommission);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapSecretaire(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
