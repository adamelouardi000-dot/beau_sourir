package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.users.Medecin;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MedecinRepositoryImpl implements MedecinRepository {

    private static final String SELECT_BASE = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge,
               m.specialite
        FROM Utilisateurs u
        JOIN Staffs s ON s.id = u.id
        JOIN Medecins m ON m.id = s.id
        """;

    @Override
    public List<Medecin> findAll() {
        return findAllOrderByNom();
    }

    @Override
    public Medecin findById(Long id) {
        String sql = SELECT_BASE + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedecin(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Medecin medecin) {
        String insertUser = """
            INSERT INTO Utilisateurs(nom,email,adresse,cin,tel,sexe,login,motDePasse,lastLoginDate,dateNaissance,
                                     dateCreation,dateDerniereModification,creePar,modifiePar)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        String insertStaff = """
            INSERT INTO Staffs(id,salaire,prime,dateRecrutement,soldeConge)
            VALUES (?,?,?,?,?)
            """;
        String insertMed = "INSERT INTO Medecins(id, specialite, agenda_id) VALUES (?,?,NULL)";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement psS = conn.prepareStatement(insertStaff);
                 PreparedStatement psM = conn.prepareStatement(insertMed)) {

                psU.setString(1, medecin.getNom());
                psU.setString(2, medecin.getEmail());
                psU.setString(3, medecin.getAdresse());
                psU.setString(4, medecin.getCin());
                psU.setString(5, medecin.getTel());
                psU.setString(6, medecin.getSexe().name());
                psU.setString(7, medecin.getLogin());
                psU.setString(8, medecin.getMotDePasse());

                if (medecin.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(medecin.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (medecin.getDateNaissance() != null) psU.setDate(10, Date.valueOf(medecin.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setDate(11, Date.valueOf(java.time.LocalDate.now()));
                psU.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(13, medecin.getCreePar());
                psU.setString(14, medecin.getModifiePar());

                psU.executeUpdate();
                try (ResultSet keys = psU.getGeneratedKeys()) {
                    if (keys.next()) medecin.setId(keys.getLong(1));
                }

                psS.setLong(1, medecin.getId());
                if (medecin.getSalaire() != null) psS.setDouble(2, medecin.getSalaire()); else psS.setNull(2, Types.DECIMAL);
                if (medecin.getPrime() != null) psS.setDouble(3, medecin.getPrime()); else psS.setNull(3, Types.DECIMAL);
                if (medecin.getDateRecrutement() != null) psS.setDate(4, Date.valueOf(medecin.getDateRecrutement())); else psS.setNull(4, Types.DATE);
                if (medecin.getSoldeCongé() != null) psS.setInt(5, medecin.getSoldeCongé()); else psS.setNull(5, Types.INTEGER);
                psS.executeUpdate();

                psM.setLong(1, medecin.getId());
                psM.setString(2, medecin.getSpecialite());
                psM.executeUpdate();

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
    public void update(Medecin medecin) {
        String updateUser = """
            UPDATE Utilisateurs SET nom=?, email=?, adresse=?, cin=?, tel=?, sexe=?, login=?, motDePasse=?,
                                   lastLoginDate=?, dateNaissance=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;
        String updateStaff = """
            UPDATE Staffs SET salaire=?, prime=?, dateRecrutement=?, soldeConge=?
            WHERE id=?
            """;
        String updateMed = "UPDATE Medecins SET specialite=? WHERE id=?";

        try (Connection conn = SessionFactory.getInstance().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement psU = conn.prepareStatement(updateUser);
                 PreparedStatement psS = conn.prepareStatement(updateStaff);
                 PreparedStatement psM = conn.prepareStatement(updateMed)) {

                psU.setString(1, medecin.getNom());
                psU.setString(2, medecin.getEmail());
                psU.setString(3, medecin.getAdresse());
                psU.setString(4, medecin.getCin());
                psU.setString(5, medecin.getTel());
                psU.setString(6, medecin.getSexe().name());
                psU.setString(7, medecin.getLogin());
                psU.setString(8, medecin.getMotDePasse());

                if (medecin.getLastLoginDate() != null) psU.setDate(9, Date.valueOf(medecin.getLastLoginDate()));
                else psU.setNull(9, Types.DATE);

                if (medecin.getDateNaissance() != null) psU.setDate(10, Date.valueOf(medecin.getDateNaissance()));
                else psU.setNull(10, Types.DATE);

                psU.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                psU.setString(12, medecin.getCreePar());
                psU.setString(13, medecin.getModifiePar());
                psU.setLong(14, medecin.getId());
                psU.executeUpdate();

                if (medecin.getSalaire() != null) psS.setDouble(1, medecin.getSalaire()); else psS.setNull(1, Types.DECIMAL);
                if (medecin.getPrime() != null) psS.setDouble(2, medecin.getPrime()); else psS.setNull(2, Types.DECIMAL);
                if (medecin.getDateRecrutement() != null) psS.setDate(3, Date.valueOf(medecin.getDateRecrutement())); else psS.setNull(3, Types.DATE);
                if (medecin.getSoldeCongé() != null) psS.setInt(4, medecin.getSoldeCongé()); else psS.setNull(4, Types.INTEGER);
                psS.setLong(5, medecin.getId());
                psS.executeUpdate();

                psM.setString(1, medecin.getSpecialite());
                psM.setLong(2, medecin.getId());
                psM.executeUpdate();

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
    public void delete(Medecin medecin) {
        if (medecin != null) deleteById(medecin.getId());
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
    public List<Medecin> findAllOrderByNom() {
        String sql = SELECT_BASE + " ORDER BY u.nom";
        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapMedecin(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Medecin> findBySpecialite(String specialiteLike) {
        String sql = SELECT_BASE + " WHERE m.specialite LIKE ? ORDER BY u.nom";
        List<Medecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + specialiteLike + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
