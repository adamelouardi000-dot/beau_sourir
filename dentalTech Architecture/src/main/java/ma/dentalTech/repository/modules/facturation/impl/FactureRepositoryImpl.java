package ma.dentalTech.repository.modules.facturation.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Facture;
import ma.dentalTech.repository.modules.facturation.api.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class FactureRepositoryImpl implements FactureRepository {

    // ================= CRUD =================

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT * FROM Factures ORDER BY id DESC";
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapFacture(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM Factures WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapFacture(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Facture f) {
        // Colonnes SQL "standard" (adapter si ton schema diffère)
        String sql = """
            INSERT INTO Factures(patient_id, consultation_id, dateFacture, total, statut)
            VALUES (?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long patientId = extractLong(f, "getPatientId");
            if (patientId == null) patientId = extractNestedId(f, "getPatient");
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            Long consultationId = extractLong(f, "getConsultationId");
            if (consultationId == null) consultationId = extractNestedId(f, "getConsultation");
            if (consultationId != null) ps.setLong(2, consultationId);
            else ps.setNull(2, Types.BIGINT);

            LocalDateTime dateFacture = extractLocalDateTime(f, "getDateFacture");
            if (dateFacture != null) ps.setTimestamp(3, Timestamp.valueOf(dateFacture));
            else ps.setNull(3, Types.TIMESTAMP);

            Double total = extractDouble(f, "getTotal");
            if (total != null) ps.setDouble(4, total);
            else ps.setNull(4, Types.DECIMAL);

            String statut = extractString(f, "getStatut");
            ps.setString(5, statut);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) f.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Facture f) {
        String sql = """
            UPDATE Factures SET
                patient_id=?, consultation_id=?, dateFacture=?, total=?, statut=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long patientId = extractLong(f, "getPatientId");
            if (patientId == null) patientId = extractNestedId(f, "getPatient");
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            Long consultationId = extractLong(f, "getConsultationId");
            if (consultationId == null) consultationId = extractNestedId(f, "getConsultation");
            if (consultationId != null) ps.setLong(2, consultationId);
            else ps.setNull(2, Types.BIGINT);

            LocalDateTime dateFacture = extractLocalDateTime(f, "getDateFacture");
            if (dateFacture != null) ps.setTimestamp(3, Timestamp.valueOf(dateFacture));
            else ps.setNull(3, Types.TIMESTAMP);

            Double total = extractDouble(f, "getTotal");
            if (total != null) ps.setDouble(4, total);
            else ps.setNull(4, Types.DECIMAL);

            String statut = extractString(f, "getStatut");
            ps.setString(5, statut);

            ps.setLong(6, f.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Facture f) {
        if (f != null) deleteById(f.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Factures WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Queries =================

    @Override
    public List<Facture> findByPatient(Long patientId) {
        String sql = "SELECT * FROM Factures WHERE patient_id=? ORDER BY dateFacture DESC";
        return findList(sql, ps -> ps.setLong(1, patientId));
    }

    @Override
    public List<Facture> findByConsultation(Long consultationId) {
        String sql = "SELECT * FROM Factures WHERE consultation_id=? ORDER BY dateFacture DESC";
        return findList(sql, ps -> ps.setLong(1, consultationId));
    }

    @Override
    public List<Facture> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Factures WHERE DATE(dateFacture) BETWEEN ? AND ? ORDER BY dateFacture DESC";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Factures WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Factures";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Facture> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Factures ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Internals =================

    private interface Binder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Facture> findList(String sql, Binder b) {
        List<Facture> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            b.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapFacture(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Facture mapFacture(ResultSet rs) throws SQLException {
        Facture f = new Facture();
        f.setId(rs.getLong("id"));

        Long patientId = safeGetLong(rs, "patient_id");
        if (patientId != null) setIfExists(f, "setPatientId", Long.class, patientId);

        Long consultationId = safeGetLong(rs, "consultation_id");
        if (consultationId != null) setIfExists(f, "setConsultationId", Long.class, consultationId);

        Timestamp ts = safeGetTimestamp(rs, "dateFacture");
        if (ts != null) setIfExists(f, "setDateFacture", LocalDateTime.class, ts.toLocalDateTime());

        Double total = safeGetDouble(rs, "total");
        if (total != null) setIfExists(f, "setTotal", Double.class, total);

        String statut = safeGetString(rs, "statut");
        if (statut != null) setIfExists(f, "setStatut", String.class, statut);

        return f;
    }

    // ---- reflection safe ----
    private Long extractLong(Object obj, String getter) {
        try { return (Long) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private Double extractDouble(Object obj, String getter) {
        try { return (Double) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private String extractString(Object obj, String getter) {
        try { return (String) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private LocalDateTime extractLocalDateTime(Object obj, String getter) {
        try { return (LocalDateTime) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private Long extractNestedId(Object obj, String getter) {
        try {
            Object nested = obj.getClass().getMethod(getter).invoke(obj);
            if (nested == null) return null;
            return (Long) nested.getClass().getMethod("getId").invoke(nested);
        } catch (Exception e) {
            return null;
        }
    }

    private void setIfExists(Object obj, String setter, Class<?> type, Object value) {
        try { obj.getClass().getMethod(setter, type).invoke(obj, value); }
        catch (Exception ignored) {}
    }

    // ---- ResultSet safe ----
    private String safeGetString(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }

    private Timestamp safeGetTimestamp(ResultSet rs, String col) {
        try { return rs.getTimestamp(col); } catch (SQLException e) { return null; }
    }

    private Double safeGetDouble(ResultSet rs, String col) {
        try { double v = rs.getDouble(col); return rs.wasNull() ? null : v; }
        catch (SQLException e) { return null; }
    }

    private Long safeGetLong(ResultSet rs, String col) {
        try { long v = rs.getLong(col); return rs.wasNull() ? null : v; }
        catch (SQLException e) { return null; }
    }
}
