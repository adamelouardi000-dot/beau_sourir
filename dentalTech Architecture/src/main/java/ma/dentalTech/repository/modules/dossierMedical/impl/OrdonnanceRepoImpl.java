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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM Ordonnances WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOrdonnance(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Ordonnance o) {
        // ✅ Table: Ordonnances(consultation_id, dateOrdonnance, remarque, dateCreation, dateDerniereModification, creePar, modifiePar)
        String sql = """
            INSERT INTO Ordonnances(
                consultation_id,
                dateOrdonnance,
                remarque,
                dateCreation,
                dateDerniereModification,
                creePar,
                modifiePar
            )
            VALUES (?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long consultationId = extractConsultationId(o);
            if (consultationId != null) ps.setLong(1, consultationId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dt = extractDateOrdonnance(o);
            ps.setTimestamp(2, Timestamp.valueOf(dt));

            ps.setString(3, extractRemarque(o));

            LocalDate dc = (o.getDateCreation() != null) ? o.getDateCreation() : LocalDate.now();
            ps.setDate(4, Date.valueOf(dc));
            o.setDateCreation(dc);

            LocalDateTime ddm = (o.getDateDerniereModification() != null) ? o.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(ddm));
            o.setDateDerniereModification(ddm);

            ps.setString(6, o.getCreePar());
            ps.setString(7, o.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Ordonnance o) {
        String sql = """
            UPDATE Ordonnances SET
                consultation_id=?,
                dateOrdonnance=?,
                remarque=?,
                dateDerniereModification=?,
                creePar=?,
                modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long consultationId = extractConsultationId(o);
            if (consultationId != null) ps.setLong(1, consultationId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dt = extractDateOrdonnance(o);
            ps.setTimestamp(2, Timestamp.valueOf(dt));

            ps.setString(3, extractRemarque(o));

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, o.getCreePar());
            ps.setString(6, o.getModifiePar());

            ps.setLong(7, o.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Ordonnance o) {
        if (o != null) deleteById(o.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Ordonnances WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= QUERIES (selon ton interface OrdonnanceRepo) =================

    @Override
    public List<Ordonnance> findByConsultation(Long consultationId) {
        String sql = "SELECT * FROM Ordonnances WHERE consultation_id=? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> ps.setLong(1, consultationId));
    }

    // ✅ FIX: ton interface demande findByDate(LocalDate)
    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        // dateOrdonnance est DATETIME => on filtre par journée
        String sql = """
            SELECT * FROM Ordonnances
            WHERE dateOrdonnance >= ? AND dateOrdonnance < ?
            ORDER BY dateOrdonnance DESC
            """;
        return findList(sql, ps -> {
            ps.setTimestamp(1, Timestamp.valueOf(date.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(date.plusDays(1).atStartOfDay()));
        });
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findByMedecin(Long medecinId) {
        return List.of();
    }

    @Override
    public List<Ordonnance> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Ordonnances ORDER BY dateOrdonnance DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= HELPERS =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Ordonnance> findList(String sql, PsBinder binder) {
        List<Ordonnance> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapOrdonnance(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Ordonnance mapOrdonnance(ResultSet rs) throws SQLException {
        Ordonnance o = new Ordonnance();

        o.setId(rs.getLong("id"));

        Timestamp t = rs.getTimestamp("dateOrdonnance");
        if (t != null) {
            LocalDateTime ldt = t.toLocalDateTime();

            // Si entity a setDateOrdonnance(LocalDateTime)
            if (!tryInvoke(o, "setDateOrdonnance", LocalDateTime.class, ldt)) {
                // Sinon setDateOrdonnance(LocalDate)
                tryInvoke(o, "setDateOrdonnance", LocalDate.class, ldt.toLocalDate());
            }
        }

        tryInvoke(o, "setRemarque", String.class, rs.getString("remarque"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) o.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) o.setDateDerniereModification(dm.toLocalDateTime());

        o.setCreePar(rs.getString("creePar"));
        o.setModifiePar(rs.getString("modifiePar"));

        long cid = rs.getLong("consultation_id");
        if (!rs.wasNull()) {
            // si entity a setConsultationId(Long)
            tryInvoke(o, "setConsultationId", Long.class, cid);
        }

        return o;
    }

    private boolean tryInvoke(Object target, String method, Class<?> type, Object value) {
        try {
            var m = target.getClass().getMethod(method, type);
            m.invoke(target, value);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private Long extractConsultationId(Ordonnance o) {
        // 1) getConsultationId()
        try {
            var m = o.getClass().getMethod("getConsultationId");
            Object v = m.invoke(o);
            if (v instanceof Long) return (Long) v;
            if (v instanceof Number) return ((Number) v).longValue();
        } catch (Exception ignored) {}

        // 2) si tu as un champ consultation (mais sans getter typed), on tente "getConsultation" via reflection
        //    (ça ne cassera pas si la méthode n'existe pas)
        try {
            var m = o.getClass().getMethod("getConsultation");
            Object cons = m.invoke(o);
            if (cons != null) {
                var getId = cons.getClass().getMethod("getId");
                Object id = getId.invoke(cons);
                if (id instanceof Long) return (Long) id;
                if (id instanceof Number) return ((Number) id).longValue();
            }
        } catch (Exception ignored) {}

        return null;
    }

    private LocalDateTime extractDateOrdonnance(Ordonnance o) {
        try {
            var m = o.getClass().getMethod("getDateOrdonnance");
            Object v = m.invoke(o);
            if (v instanceof LocalDateTime) return (LocalDateTime) v;
            if (v instanceof LocalDate) return ((LocalDate) v).atStartOfDay();
        } catch (Exception ignored) {}
        return LocalDateTime.now();
    }

    private String extractRemarque(Ordonnance o) {
        try {
            var m = o.getClass().getMethod("getRemarque");
            Object v = m.invoke(o);
            return (String) v;
        } catch (Exception ignored) {}
        return null;
    }
}
