package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Certificat;
import ma.dentalTech.repository.modules.dossierMedical.api.CertificatRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepoImpl implements CertificatRepo {

    // ---------------- CRUD ----------------

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM Certificats ORDER BY dateDebut DESC";
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapCertificat(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM Certificats WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCertificat(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Certificat cert) {
        String sql = """
            INSERT INTO Certificats(
                dateDebut, dateFin, duree, noteMedecin,
                dossierMedical_id, medecin_id,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(cert.getDateDebut()));
            ps.setDate(2, Date.valueOf(cert.getDateFin()));

            if (cert.getDuree() != null) ps.setInt(3, cert.getDuree());
            else ps.setNull(3, Types.INTEGER);

            ps.setString(4, cert.getNoteMedecin());

            // FK dossierMedical
            if (cert.getDossierMedical() != null && cert.getDossierMedical().getId() != null)
                ps.setLong(5, cert.getDossierMedical().getId());
            else
                ps.setNull(5, Types.BIGINT);

            // FK medecin
            if (cert.getMedecin() != null && cert.getMedecin().getId() != null)
                ps.setLong(6, cert.getMedecin().getId());
            else
                ps.setNull(6, Types.BIGINT);

            // BaseEntity
            LocalDate dc = cert.getDateCreation() != null ? cert.getDateCreation() : LocalDate.now();
            ps.setDate(7, Date.valueOf(dc));
            cert.setDateCreation(dc);

            LocalDateTime dm = cert.getDateDerniereModification() != null ? cert.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(dm));
            cert.setDateDerniereModification(dm);

            ps.setString(9, cert.getCreePar());
            ps.setString(10, cert.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cert.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Certificat cert) {
        String sql = """
            UPDATE Certificats SET
                dateDebut=?, dateFin=?, duree=?, noteMedecin=?,
                dossierMedical_id=?, medecin_id=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(cert.getDateDebut()));
            ps.setDate(2, Date.valueOf(cert.getDateFin()));

            if (cert.getDuree() != null) ps.setInt(3, cert.getDuree());
            else ps.setNull(3, Types.INTEGER);

            ps.setString(4, cert.getNoteMedecin());

            if (cert.getDossierMedical() != null && cert.getDossierMedical().getId() != null)
                ps.setLong(5, cert.getDossierMedical().getId());
            else
                ps.setNull(5, Types.BIGINT);

            if (cert.getMedecin() != null && cert.getMedecin().getId() != null)
                ps.setLong(6, cert.getMedecin().getId());
            else
                ps.setNull(6, Types.BIGINT);

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, cert.getCreePar());
            ps.setString(9, cert.getModifiePar());
            ps.setLong(10, cert.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Certificat cert) {
        if (cert != null) deleteById(cert.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Certificats WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Queries ----------------

    @Override
    public List<Certificat> findByDossierMedical(Long dossierMedicalId) {
        String sql = "SELECT * FROM Certificats WHERE dossierMedical_id=? ORDER BY dateDebut DESC";
        return findList(sql, ps -> ps.setLong(1, dossierMedicalId));
    }

    @Override
    public List<Certificat> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM Certificats WHERE medecin_id=? ORDER BY dateDebut DESC";
        return findList(sql, ps -> ps.setLong(1, medecinId));
    }

    @Override
    public List<Certificat> findByDateDebutBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Certificats WHERE dateDebut BETWEEN ? AND ? ORDER BY dateDebut DESC";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Certificat> findByDateFinBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Certificats WHERE dateFin BETWEEN ? AND ? ORDER BY dateFin DESC";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Certificat> findByDureeMin(Integer minDuree) {
        String sql = "SELECT * FROM Certificats WHERE duree >= ? ORDER BY duree DESC";
        return findList(sql, ps -> ps.setInt(1, minDuree));
    }

    @Override
    public List<Certificat> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Certificats ORDER BY dateDebut DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ---------------- Helpers ----------------

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Certificat> findList(String sql, PsBinder binder) {
        List<Certificat> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCertificat(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private Certificat mapCertificat(ResultSet rs) throws SQLException {
        Certificat c = new Certificat();

        c.setId(rs.getLong("id"));

        Date dd = rs.getDate("dateDebut");
        if (dd != null) c.setDateDebut(dd.toLocalDate());

        Date df = rs.getDate("dateFin");
        if (df != null) c.setDateFin(df.toLocalDate());

        int duree = rs.getInt("duree");
        if (!rs.wasNull()) c.setDuree(duree);

        c.setNoteMedecin(rs.getString("noteMedecin"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());

        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        // on ne hydrate pas dossierMedical/medecin ici (pas de framework) -> le service peut le faire si besoin
        return c;
    }
}
