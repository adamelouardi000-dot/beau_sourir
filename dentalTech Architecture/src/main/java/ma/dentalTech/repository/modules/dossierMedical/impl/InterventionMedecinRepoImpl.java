package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.InterventionMedecin;
import ma.dentalTech.repository.modules.dossierMedical.api.InterventionMedecinRepo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class InterventionMedecinRepoImpl implements InterventionMedecinRepo {

    // ================= CRUD =================

    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM InterventionMedecins ORDER BY id DESC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapIntervention(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM InterventionMedecins WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapIntervention(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(InterventionMedecin i) {
        String sql = """
            INSERT INTO InterventionMedecins(
                prixPatient, numDent, consultation_id, acte_id,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (i.getPrixPatient() != null) ps.setDouble(1, i.getPrixPatient());
            else ps.setNull(1, Types.DECIMAL);

            if (i.getNumDent() != null) ps.setInt(2, i.getNumDent());
            else ps.setNull(2, Types.INTEGER);

            if (i.getConsultationId() != null) ps.setLong(3, i.getConsultationId());
            else ps.setNull(3, Types.BIGINT);

            if (i.getActeId() != null) ps.setLong(4, i.getActeId());
            else ps.setNull(4, Types.BIGINT);

            // BaseEntity
            ps.setDate(5, Date.valueOf(i.getDateCreation() != null ? i.getDateCreation() : java.time.LocalDate.now()));
            ps.setTimestamp(6, Timestamp.valueOf(i.getDateDerniereModification() != null ? i.getDateDerniereModification() : java.time.LocalDateTime.now()));
            ps.setString(7, i.getCreePar());
            ps.setString(8, i.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) i.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(InterventionMedecin i) {
        String sql = """
            UPDATE InterventionMedecins SET
                prixPatient=?, numDent=?, consultation_id=?, acte_id=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (i.getPrixPatient() != null) ps.setDouble(1, i.getPrixPatient());
            else ps.setNull(1, Types.DECIMAL);

            if (i.getNumDent() != null) ps.setInt(2, i.getNumDent());
            else ps.setNull(2, Types.INTEGER);

            if (i.getConsultationId() != null) ps.setLong(3, i.getConsultationId());
            else ps.setNull(3, Types.BIGINT);

            if (i.getActeId() != null) ps.setLong(4, i.getActeId());
            else ps.setNull(4, Types.BIGINT);

            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(6, i.getCreePar());
            ps.setString(7, i.getModifiePar());
            ps.setLong(8, i.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(InterventionMedecin i) {
        if (i != null) deleteById(i.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM InterventionMedecins WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ================= QUERIES =================

    @Override
    public List<InterventionMedecin> findByConsultation(Long consultationId) {
        String sql = "SELECT * FROM InterventionMedecins WHERE consultation_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, consultationId));
    }

    @Override
    public List<InterventionMedecin> findByActe(Long acteId) {
        String sql = "SELECT * FROM InterventionMedecins WHERE acte_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, acteId));
    }

    @Override
    public List<InterventionMedecin> findByNumDent(Integer numDent) {
        String sql = "SELECT * FROM InterventionMedecins WHERE numDent=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setInt(1, numDent));
    }

    @Override
    public List<InterventionMedecin> findByPrixPatientBetween(Double min, Double max) {
        String sql = "SELECT * FROM InterventionMedecins WHERE prixPatient BETWEEN ? AND ? ORDER BY prixPatient";
        return findList(sql, ps -> {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
        });
    }

    @Override
    public List<InterventionMedecin> findPage(int limit, int offset) {
        String sql = "SELECT * FROM InterventionMedecins ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= HELPERS =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<InterventionMedecin> findList(String sql, PsBinder binder) {
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapIntervention(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private InterventionMedecin mapIntervention(ResultSet rs) throws SQLException {
        InterventionMedecin i = new InterventionMedecin();

        i.setId(rs.getLong("id"));

        double prix = rs.getDouble("prixPatient");
        if (!rs.wasNull()) i.setPrixPatient(prix);

        int dent = rs.getInt("numDent");
        if (!rs.wasNull()) i.setNumDent(dent);

        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) i.setConsultationId(consultationId);

        long acteId = rs.getLong("acte_id");
        if (!rs.wasNull()) i.setActeId(acteId);

        Date dc = rs.getDate("dateCreation");
        if (dc != null) i.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) i.setDateDerniereModification(dm.toLocalDateTime());

        i.setCreePar(rs.getString("creePar"));
        i.setModifiePar(rs.getString("modifiePar"));

        return i;
    }
}
