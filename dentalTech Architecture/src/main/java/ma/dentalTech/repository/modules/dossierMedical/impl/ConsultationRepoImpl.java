package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ConsultationRepoImpl implements ConsultationRepo {

    // ================= CRUD =================

    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM Consultations ORDER BY dateConsultation DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapConsultation(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM Consultations WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapConsultation(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Consultation cst) {
        String sql = """
            INSERT INTO Consultations(
                dateConsultation, statut, observationMedecin,
                dossierMedical_id, medecin_id, facturee,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, Date.valueOf(cst.getDateConsultation()));
            ps.setString(2, cst.getStatut().name());
            ps.setString(3, cst.getObservationMedecin());

            if (cst.getDossierMedical() != null && cst.getDossierMedical().getId() != null)
                ps.setLong(4, cst.getDossierMedical().getId());
            else
                ps.setNull(4, Types.BIGINT);

            if (cst.getMedecin() != null && cst.getMedecin().getId() != null)
                ps.setLong(5, cst.getMedecin().getId());
            else
                ps.setNull(5, Types.BIGINT);

            ps.setBoolean(6, false); // par défaut non facturée

            ps.setDate(7, Date.valueOf(LocalDate.now()));
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, cst.getCreePar());
            ps.setString(10, cst.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cst.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Consultation cst) {
        String sql = """
            UPDATE Consultations SET
                dateConsultation=?, statut=?, observationMedecin=?,
                dossierMedical_id=?, medecin_id=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(cst.getDateConsultation()));
            ps.setString(2, cst.getStatut().name());
            ps.setString(3, cst.getObservationMedecin());

            if (cst.getDossierMedical() != null && cst.getDossierMedical().getId() != null)
                ps.setLong(4, cst.getDossierMedical().getId());
            else
                ps.setNull(4, Types.BIGINT);

            if (cst.getMedecin() != null && cst.getMedecin().getId() != null)
                ps.setLong(5, cst.getMedecin().getId());
            else
                ps.setNull(5, Types.BIGINT);

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, cst.getCreePar());
            ps.setString(8, cst.getModifiePar());
            ps.setLong(9, cst.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Consultation cst) {
        if (cst != null) deleteById(cst.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Consultations WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= QUERIES =================

    @Override
    public List<Consultation> findByDossierMedical(Long dossierMedicalId) {
        String sql = "SELECT * FROM Consultations WHERE dossierMedical_id=?";
        return findList(sql, ps -> ps.setLong(1, dossierMedicalId));
    }

    @Override
    public List<Consultation> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM Consultations WHERE medecin_id=?";
        return findList(sql, ps -> ps.setLong(1, medecinId));
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Consultations WHERE dateConsultation=?";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Consultations WHERE dateConsultation BETWEEN ? AND ?";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        String sql = "SELECT * FROM Consultations WHERE statut=?";
        return findList(sql, ps -> ps.setString(1, statut.name()));
    }

    @Override
    public List<Consultation> findByFacturee(boolean facturee) {
        String sql = "SELECT * FROM Consultations WHERE facturee=?";
        return findList(sql, ps -> ps.setBoolean(1, facturee));
    }

    @Override
    public List<Consultation> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Consultations ORDER BY dateConsultation DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= HELPERS =================

    private interface PsBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Consultation> findList(String sql, PsBinder binder) {
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();

        c.setId(rs.getLong("id"));

        Date d = rs.getDate("dateConsultation");
        if (d != null) c.setDateConsultation(d.toLocalDate());

        String s = rs.getString("statut");
        if (s != null) c.setStatut(StatutConsultation.valueOf(s));

        c.setObservationMedecin(rs.getString("observationMedecin"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());

        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        return c;
    }
}
