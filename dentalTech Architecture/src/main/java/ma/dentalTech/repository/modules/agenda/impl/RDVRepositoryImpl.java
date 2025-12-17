package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.RDV;
import ma.dentalTech.repository.modules.agenda.api.RDVRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RDVRepositoryImpl implements RDVRepository {

    // ================= CRUD =================

    @Override
    public List<RDV> findAll() {
        String sql = "SELECT * FROM RDV ORDER BY id DESC";
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRDV(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public RDV findById(Long id) {
        String sql = "SELECT * FROM RDV WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRDV(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(RDV r) {
        // minimal et safe (adapter si ton schema a plus/moins de colonnes)
        String sql = """
            INSERT INTO RDV(medecin_id, patient_id, agenda_id, dateRdv)
            VALUES (?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long medecinId = extractLong(r, "getMedecinId");
            if (medecinId == null) medecinId = extractNestedId(r, "getMedecin");
            if (medecinId != null) ps.setLong(1, medecinId);
            else ps.setNull(1, Types.BIGINT);

            Long patientId = extractLong(r, "getPatientId");
            if (patientId == null) patientId = extractNestedId(r, "getPatient");
            if (patientId != null) ps.setLong(2, patientId);
            else ps.setNull(2, Types.BIGINT);

            Long agendaId = extractLong(r, "getAgendaId");
            if (agendaId == null) agendaId = extractNestedId(r, "getAgenda");
            if (agendaId != null) ps.setLong(3, agendaId);
            else ps.setNull(3, Types.BIGINT);

            LocalDateTime dateRdv = extractLocalDateTime(r, "getDateRdv");
            if (dateRdv == null) {
                LocalDate onlyDate = extractLocalDate(r, "getDate");
                if (onlyDate != null) dateRdv = onlyDate.atStartOfDay();
            }
            if (dateRdv != null) ps.setTimestamp(4, Timestamp.valueOf(dateRdv));
            else ps.setNull(4, Types.TIMESTAMP);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(RDV r) {
        String sql = """
            UPDATE RDV SET
                medecin_id=?, patient_id=?, agenda_id=?, dateRdv=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long medecinId = extractLong(r, "getMedecinId");
            if (medecinId == null) medecinId = extractNestedId(r, "getMedecin");
            if (medecinId != null) ps.setLong(1, medecinId);
            else ps.setNull(1, Types.BIGINT);

            Long patientId = extractLong(r, "getPatientId");
            if (patientId == null) patientId = extractNestedId(r, "getPatient");
            if (patientId != null) ps.setLong(2, patientId);
            else ps.setNull(2, Types.BIGINT);

            Long agendaId = extractLong(r, "getAgendaId");
            if (agendaId == null) agendaId = extractNestedId(r, "getAgenda");
            if (agendaId != null) ps.setLong(3, agendaId);
            else ps.setNull(3, Types.BIGINT);

            LocalDateTime dateRdv = extractLocalDateTime(r, "getDateRdv");
            if (dateRdv == null) {
                LocalDate onlyDate = extractLocalDate(r, "getDate");
                if (onlyDate != null) dateRdv = onlyDate.atStartOfDay();
            }
            if (dateRdv != null) ps.setTimestamp(4, Timestamp.valueOf(dateRdv));
            else ps.setNull(4, Types.TIMESTAMP);

            ps.setLong(5, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(RDV r) {
        if (r != null) deleteById(r.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM RDV WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Queries demandées =================

    @Override
    public List<RDV> findByMedecinAndDate(Long medecinId, LocalDate date) {
        String sql = "SELECT * FROM RDV WHERE medecin_id=? AND DATE(dateRdv)=? ORDER BY dateRdv";
        return findList(sql, ps -> {
            ps.setLong(1, medecinId);
            ps.setDate(2, Date.valueOf(date));
        });
    }

    @Override
    public List<RDV> findByMedecinAndDateRange(Long medecinId, LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM RDV WHERE medecin_id=? AND DATE(dateRdv) BETWEEN ? AND ? ORDER BY dateRdv";
        return findList(sql, ps -> {
            ps.setLong(1, medecinId);
            ps.setDate(2, Date.valueOf(start));
            ps.setDate(3, Date.valueOf(end));
        });
    }

    @Override
    public List<RDV> findByPatient(Long patientId) {
        String sql = "SELECT * FROM RDV WHERE patient_id=? ORDER BY dateRdv DESC";
        return findList(sql, ps -> ps.setLong(1, patientId));
    }

    @Override
    public List<RDV> findByAgenda(Long agendaId) {
        String sql = "SELECT * FROM RDV WHERE agenda_id=? ORDER BY dateRdv";
        return findList(sql, ps -> ps.setLong(1, agendaId));
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<RDV> findList(String sql, PsBinder binder) {
        List<RDV> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRDV(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private RDV mapRDV(ResultSet rs) throws SQLException {
        RDV r = new RDV();
        r.setId(rs.getLong("id"));

        Long medecinId = safeGetLong(rs, "medecin_id");
        if (medecinId != null) setIfExists(r, "setMedecinId", Long.class, medecinId);

        Long patientId = safeGetLong(rs, "patient_id");
        if (patientId != null) setIfExists(r, "setPatientId", Long.class, patientId);

        Long agendaId = safeGetLong(rs, "agenda_id");
        if (agendaId != null) setIfExists(r, "setAgendaId", Long.class, agendaId);

        LocalDateTime dt = safeGetDateTime(rs, "dateRdv");
        if (dt != null) setIfExists(r, "setDateRdv", LocalDateTime.class, dt);

        return r;
    }

    // ---- Reflection helpers (compilation safe) ----

    private Long extractLong(Object obj, String getter) {
        try { return (Long) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private LocalDate extractLocalDate(Object obj, String getter) {
        try { return (LocalDate) obj.getClass().getMethod(getter).invoke(obj); }
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
            Object id = nested.getClass().getMethod("getId").invoke(nested);
            return (Long) id;
        } catch (Exception e) {
            return null;
        }
    }

    private void setIfExists(Object obj, String setter, Class<?> paramType, Object value) {
        try { obj.getClass().getMethod(setter, paramType).invoke(obj, value); }
        catch (Exception ignored) { }
    }

    // ---- ResultSet safe getters ----

    private Long safeGetLong(ResultSet rs, String col) {
        try {
            long v = rs.getLong(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException e) { return null; }
    }

    private Timestamp safeGetTimestamp(ResultSet rs, String col) {
        try { return rs.getTimestamp(col); } catch (SQLException e) { return null; }
    }

    private LocalDateTime safeGetDateTime(ResultSet rs, String col) {
        Timestamp ts = safeGetTimestamp(rs, col);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
