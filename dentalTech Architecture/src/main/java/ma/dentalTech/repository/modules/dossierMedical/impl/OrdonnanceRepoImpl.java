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
        if (o.getConsultationId() == null) {
            throw new IllegalArgumentException("consultationId obligatoire (NULL trouvé dans Ordonnance)");
        }

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

            ps.setLong(1, o.getConsultationId());

            // dateOrdonnance DATETIME
            LocalDateTime dOrd = extractDateOrdonnance(o);
            if (dOrd == null) dOrd = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dOrd));
            setDateOrdonnance(o, dOrd);

            ps.setString(3, extractRemarque(o));

            LocalDate dc = (o.getDateCreation() != null) ? o.getDateCreation() : LocalDate.now();
            ps.setDate(4, Date.valueOf(dc));
            o.setDateCreation(dc);

            LocalDateTime dm = (o.getDateDerniereModification() != null) ? o.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));
            o.setDateDerniereModification(dm);

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
        if (o.getConsultationId() == null) throw new IllegalArgumentException("consultationId obligatoire");

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

            ps.setLong(1, o.getConsultationId());

            LocalDateTime dOrd = extractDateOrdonnance(o);
            if (dOrd == null) dOrd = LocalDateTime.now();
            ps.setTimestamp(2, Timestamp.valueOf(dOrd));

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

    // ================= QUERIES =================

    @Override
    public List<Ordonnance> findByConsultation(Long consultationId) {
        String sql = "SELECT * FROM Ordonnances WHERE consultation_id=? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> ps.setLong(1, consultationId));
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM Ordonnances WHERE DATE(dateOrdonnance)=? ORDER BY dateOrdonnance DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<Ordonnance> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = """
            SELECT * FROM Ordonnances
            WHERE DATE(dateOrdonnance) BETWEEN ? AND ?
            ORDER BY dateOrdonnance DESC
            """;
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Ordonnance> findByMedecin(Long medecinId) {
        // join via consultations -> dossiersMedicaux (qui contient medecin_id)
        String sql = """
            SELECT o.*
            FROM Ordonnances o
            JOIN Consultations c ON c.id = o.consultation_id
            JOIN DossiersMedicaux dm ON dm.id = c.dossier_medical_id
            WHERE dm.medecin_id = ?
            ORDER BY o.dateOrdonnance DESC
            """;
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
        o.setConsultationId(rs.getLong("consultation_id"));

        Timestamp ts = rs.getTimestamp("dateOrdonnance");
        if (ts != null) setDateOrdonnance(o, ts.toLocalDateTime());

        tryInvoke(o, "setRemarque", String.class, rs.getString("remarque"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) o.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) o.setDateDerniereModification(dm.toLocalDateTime());

        o.setCreePar(rs.getString("creePar"));
        o.setModifiePar(rs.getString("modifiePar"));

        return o;
    }

    private LocalDateTime extractDateOrdonnance(Ordonnance o) {
        try {
            Object v = o.getClass().getMethod("getDateOrdonnance").invoke(o);
            if (v instanceof LocalDateTime) return (LocalDateTime) v;
            if (v instanceof LocalDate) return ((LocalDate) v).atStartOfDay();
        } catch (Exception ignored) {}
        return null;
    }

    private void setDateOrdonnance(Ordonnance o, LocalDateTime dt) {
        if (tryInvoke(o, "setDateOrdonnance", LocalDateTime.class, dt)) return;
        tryInvoke(o, "setDateOrdonnance", LocalDate.class, dt.toLocalDate());
    }

    private String extractRemarque(Ordonnance o) {
        try {
            Object v = o.getClass().getMethod("getRemarque").invoke(o);
            return (v instanceof String) ? (String) v : null;
        } catch (Exception ignored) {
            return null;
        }
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
}
