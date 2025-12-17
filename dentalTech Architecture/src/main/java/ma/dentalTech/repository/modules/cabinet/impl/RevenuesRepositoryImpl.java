package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Revenues;
import ma.dentalTech.repository.modules.cabinet.api.RevenuesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class RevenuesRepositoryImpl implements RevenuesRepository {

    // ================= CRUD =================

    @Override
    public List<Revenues> findAll() {
        String sql = "SELECT * FROM Revenues ORDER BY id DESC";
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRevenues(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Revenues findById(Long id) {
        String sql = "SELECT * FROM Revenues WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRevenues(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Revenues r) {
        // Colonnes "standard" (adapter si schema différent)
        String sql = """
            INSERT INTO Revenues(
                cabinet_id, dateRevenue, montant, libelle, description,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long cabinetId = extractLong(r, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(r, "getCabinet"); // getCabinet().getId()
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dateRevenue = extractLocalDateTime(r, "getDateRevenue");
            if (dateRevenue != null) ps.setTimestamp(2, Timestamp.valueOf(dateRevenue));
            else ps.setNull(2, Types.TIMESTAMP);

            Double montant = extractDouble(r, "getMontant");
            if (montant != null) ps.setDouble(3, montant);
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, extractString(r, "getLibelle"));
            ps.setString(5, extractString(r, "getDescription"));

            // BaseEntity
            LocalDate dc = r.getDateCreation() != null ? r.getDateCreation() : LocalDate.now();
            ps.setDate(6, Date.valueOf(dc));
            r.setDateCreation(dc);

            LocalDateTime dm = r.getDateDerniereModification() != null ? r.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(dm));
            r.setDateDerniereModification(dm);

            ps.setString(8, r.getCreePar());
            ps.setString(9, r.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Revenues r) {
        String sql = """
            UPDATE Revenues SET
                cabinet_id=?, dateRevenue=?, montant=?, libelle=?, description=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long cabinetId = extractLong(r, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(r, "getCabinet");
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dateRevenue = extractLocalDateTime(r, "getDateRevenue");
            if (dateRevenue != null) ps.setTimestamp(2, Timestamp.valueOf(dateRevenue));
            else ps.setNull(2, Types.TIMESTAMP);

            Double montant = extractDouble(r, "getMontant");
            if (montant != null) ps.setDouble(3, montant);
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, extractString(r, "getLibelle"));
            ps.setString(5, extractString(r, "getDescription"));

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, r.getCreePar());
            ps.setString(8, r.getModifiePar());
            ps.setLong(9, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Revenues r) {
        if (r != null) deleteById(r.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Revenues WHERE id = ?";
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
    public List<Revenues> findByCabinet(Long cabinetId) {
        String sql = "SELECT * FROM Revenues WHERE cabinet_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, cabinetId));
    }

    @Override
    public List<Revenues> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM Revenues WHERE cabinet_id=? AND dateRevenue BETWEEN ? AND ? ORDER BY dateRevenue DESC";
        return findList(sql, ps -> {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));
        });
    }

    @Override
    public List<Revenues> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM Revenues WHERE dateRevenue BETWEEN ? AND ? ORDER BY dateRevenue DESC";
        return findList(sql, ps -> {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
        });
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Revenues WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Revenues";
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
    public List<Revenues> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Revenues ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Revenues> findList(String sql, PsBinder binder) {
        List<Revenues> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRevenues(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Revenues mapRevenues(ResultSet rs) throws SQLException {
        Revenues r = new Revenues();

        r.setId(rs.getLong("id"));

        setIfExists(r, "setLibelle", String.class, safeGetString(rs, "libelle"));
        setIfExists(r, "setDescription", String.class, safeGetString(rs, "description"));

        Double montant = safeGetDouble(rs, "montant");
        if (montant != null) setIfExists(r, "setMontant", Double.class, montant);

        LocalDateTime dateRevenue = safeGetDateTime(rs, "dateRevenue");
        if (dateRevenue != null) setIfExists(r, "setDateRevenue", LocalDateTime.class, dateRevenue);

        Long cabinetId = safeGetLong(rs, "cabinet_id");
        if (cabinetId != null) setIfExists(r, "setCabinetId", Long.class, cabinetId);

        // BaseEntity
        Date dc = safeGetDate(rs, "dateCreation");
        if (dc != null) r.setDateCreation(dc.toLocalDate());

        Timestamp dm = safeGetTimestamp(rs, "dateDerniereModification");
        if (dm != null) r.setDateDerniereModification(dm.toLocalDateTime());

        r.setCreePar(safeGetString(rs, "creePar"));
        r.setModifiePar(safeGetString(rs, "modifiePar"));

        return r;
    }

    // ---- reflection safe getters ----

    private Long extractLong(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (Long) v;
        } catch (Exception e) {
            return null;
        }
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

    private Double extractDouble(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (Double) v;
        } catch (Exception e) {
            return null;
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

    private LocalDateTime extractLocalDateTime(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (LocalDateTime) v;
        } catch (Exception e) {
            return null;
        }
    }

    private void setIfExists(Object obj, String setter, Class<?> paramType, Object value) {
        try {
            obj.getClass().getMethod(setter, paramType).invoke(obj, value);
        } catch (Exception ignored) {
        }
    }

    // ---- ResultSet safe getters ----

    private String safeGetString(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }

    private Double safeGetDouble(ResultSet rs, String col) {
        try {
            double v = rs.getDouble(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException e) { return null; }
    }

    private Long safeGetLong(ResultSet rs, String col) {
        try {
            long v = rs.getLong(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException e) { return null; }
    }

    private Date safeGetDate(ResultSet rs, String col) {
        try { return rs.getDate(col); } catch (SQLException e) { return null; }
    }

    private Timestamp safeGetTimestamp(ResultSet rs, String col) {
        try { return rs.getTimestamp(col); } catch (SQLException e) { return null; }
    }

    private LocalDateTime safeGetDateTime(ResultSet rs, String col) {
        Timestamp ts = safeGetTimestamp(rs, col);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
