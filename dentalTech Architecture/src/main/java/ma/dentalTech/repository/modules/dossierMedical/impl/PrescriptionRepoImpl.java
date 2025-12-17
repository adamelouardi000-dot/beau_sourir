package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PrescriptionRepoImpl implements PrescriptionRepo {

    // ================= CRUD =================

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM Prescriptions ORDER BY id DESC";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapPrescription(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM Prescriptions WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPrescription(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Prescription p) {
        String sql = """
            INSERT INTO Prescriptions(
                ordonnance_id, medicament_id, dosage, frequence, duree, note,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, extractLong(p, "getOrdonnanceId"));
            ps.setLong(2, extractLong(p, "getMedicamentId"));

            ps.setString(3, extractString(p, "getDosage"));
            ps.setString(4, extractString(p, "getFrequence"));

            Integer duree = extractInt(p, "getDuree");
            if (duree != null) ps.setInt(5, duree);
            else ps.setNull(5, Types.INTEGER);

            ps.setString(6, extractString(p, "getNote"));

            ps.setDate(7, Date.valueOf(p.getDateCreation() != null ? p.getDateCreation() : LocalDate.now()));
            ps.setTimestamp(8, Timestamp.valueOf(p.getDateDerniereModification() != null ? p.getDateDerniereModification() : LocalDateTime.now()));
            ps.setString(9, p.getCreePar());
            ps.setString(10, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Prescription p) {
        String sql = """
            UPDATE Prescriptions SET
                ordonnance_id=?, medicament_id=?, dosage=?, frequence=?, duree=?, note=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, extractLong(p, "getOrdonnanceId"));
            ps.setLong(2, extractLong(p, "getMedicamentId"));

            ps.setString(3, extractString(p, "getDosage"));
            ps.setString(4, extractString(p, "getFrequence"));

            Integer duree = extractInt(p, "getDuree");
            if (duree != null) ps.setInt(5, duree);
            else ps.setNull(5, Types.INTEGER);

            ps.setString(6, extractString(p, "getNote"));

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, p.getCreePar());
            ps.setString(9, p.getModifiePar());
            ps.setLong(10, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Prescription p) {
        if (p != null) deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Prescriptions WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ================= Queries =================

    @Override
    public List<Prescription> findByOrdonnance(Long ordonnanceId) {
        String sql = "SELECT * FROM Prescriptions WHERE ordonnance_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, ordonnanceId));
    }

    @Override
    public List<Prescription> findByMedicament(Long medicamentId) {
        String sql = "SELECT * FROM Prescriptions WHERE medicament_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, medicamentId));
    }

    @Override
    public List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId) {
        String sql = "SELECT * FROM Prescriptions WHERE ordonnance_id=? AND medicament_id=? ORDER BY id DESC";
        return findList(sql, ps -> {
            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);
        });
    }

    @Override
    public List<Prescription> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Prescriptions ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Prescription> findList(String sql, PsBinder binder) {
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapPrescription(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();

        p.setId(rs.getLong("id"));

        long ordonnanceId = rs.getLong("ordonnance_id");
        if (!rs.wasNull()) setIfExists(p, "setOrdonnanceId", Long.class, ordonnanceId);

        long medicamentId = rs.getLong("medicament_id");
        if (!rs.wasNull()) setIfExists(p, "setMedicamentId", Long.class, medicamentId);

        setIfExists(p, "setDosage", String.class, rs.getString("dosage"));
        setIfExists(p, "setFrequence", String.class, rs.getString("frequence"));

        int duree = rs.getInt("duree");
        if (!rs.wasNull()) setIfExists(p, "setDuree", Integer.class, duree);

        setIfExists(p, "setNote", String.class, rs.getString("note"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) p.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) p.setDateDerniereModification(dm.toLocalDateTime());

        p.setCreePar(rs.getString("creePar"));
        p.setModifiePar(rs.getString("modifiePar"));

        return p;
    }

    // ---- petits helpers reflection (tolérant aux entities du prof) ----

    private long extractLong(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            if (v == null) return 0L;
            return (Long) v;
        } catch (Exception e) { return 0L; }
    }

    private Integer extractInt(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (Integer) v;
        } catch (Exception e) { return null; }
    }

    private String extractString(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (String) v;
        } catch (Exception e) { return null; }
    }

    private void setIfExists(Object obj, String setter, Class<?> paramType, Object value) {
        try {
            obj.getClass().getMethod(setter, paramType).invoke(obj, value);
        } catch (Exception ignored) { }
    }
}
