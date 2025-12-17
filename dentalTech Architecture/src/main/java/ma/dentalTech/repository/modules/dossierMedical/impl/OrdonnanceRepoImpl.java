package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.repository.modules.dossierMedical.api.OrdonnanceRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class OrdonnanceRepoImpl implements OrdonnanceRepo {

    // ================= CRUD =================

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM Ordonnances ORDER BY dateOrdonnance DESC";
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapOrdonnance(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM Ordonnances WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOrdonnance(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Ordonnance o) {
        String sql = """
            INSERT INTO Ordonnances(
                consultation_id, medecin_id, dateOrdonnance, noteMedecin,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, extractLong(o, "getConsultationId"));
            ps.setLong(2, extractLong(o, "getMedecinId"));

            ps.setDate(3, Date.valueOf(extractDate(o, "getDateOrdonnance")));
            ps.setString(4, extractString(o, "getNoteMedecin"));

            ps.setDate(5, Date.valueOf(o.getDateCreation() != null ? o.getDateCreation() : LocalDate.now()));
            ps.setTimestamp(6, Timestamp.valueOf(o.getDateDerniereModification() != null ? o.getDateDerniereModification() : LocalDateTime.now()));
            ps.setString(7, o.getCreePar());
            ps.setString(8, o.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Ordonnance o) {
        String sql = """
            UPDATE Ordonnances SET
                consultation_id=?, medecin_id=?, dateOrdonnance=?, noteMedecin=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, extractLong(o, "getConsultationId"));
            ps.setLong(2, extractLong(o, "getMedecinId"));

            ps.setDate(3, Date.valueOf(extractDate(o, "getDateOrdonnance")));
            ps.setString(4, extractString(o, "getNoteMedecin"));

            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(6, o.getCreePar());
            ps.setString(7, o.getModifiePar());
            ps.setLong(8, o.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Ordonnance o) {
        if (o != null) deleteById(o.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Ordonnances WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ================= Queries =================

    @Override
    public List<Ordonnance> findByConsultation(Long consultationId) {
        String sql = "SELECT * FROM Ordonnances WHERE consultation_id=? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> ps.setLong(1, consultationId));
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Ordonnances WHERE dateOrdonnance=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Ordonnances WHERE dateOrdonnance BETWEEN ? AND ? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Ordonnance> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM Ordonnances WHERE medecin_id=? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> ps.setLong(1, medecinId));
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Ordonnances ORDER BY dateOrdonnance DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Ordonnance> findList(String sql, PsBinder binder) {
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapOrdonnance(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();

        o.setId(rs.getLong("id"));

        Date d = rs.getDate("dateOrdonnance");
        if (d != null) setIfExists(o, "setDateOrdonnance", LocalDate.class, d.toLocalDate());

        setIfExists(o, "setNoteMedecin", String.class, rs.getString("noteMedecin"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) o.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) o.setDateDerniereModification(dm.toLocalDateTime());

        o.setCreePar(rs.getString("creePar"));
        o.setModifiePar(rs.getString("modifiePar"));

        long consultationId = rs.getLong("consultation_id");
        if (!rs.wasNull()) setIfExists(o, "setConsultationId", Long.class, consultationId);

        long medecinId = rs.getLong("medecin_id");
        if (!rs.wasNull()) setIfExists(o, "setMedecinId", Long.class, medecinId);

        return o;
    }

    // -------- mini helpers reflection (pour matcher ton entity si noms diffèrent) --------

    private long extractLong(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            if (v == null) return 0L;
            return (Long) v;
        } catch (Exception e) {
            return 0L;
        }
    }

    private LocalDate extractDate(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (LocalDate) v;
        } catch (Exception e) {
            return LocalDate.now();
        }
    }

    private String extractString(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (String) v;
        } catch (Exception e) {
            return null;
        }
    }

    private void setIfExists(Object obj, String setter, Class<?> paramType, Object value) {
        try {
            obj.getClass().getMethod(setter, paramType).invoke(obj, value);
        } catch (Exception ignored) { }
    }
}
