package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Charges;
import ma.dentalTech.repository.modules.cabinet.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ChargesRepositoryImpl implements ChargesRepository {

    // ================= CRUD =================

    @Override
    public List<Charges> findAll() {
        String sql = "SELECT * FROM Charges ORDER BY id DESC";
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapCharges(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Charges findById(Long id) {
        String sql = "SELECT * FROM Charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCharges(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Charges ch) {
        // Colonnes "standard" (si ton schema diffère, on adaptera après)
        String sql = """
            INSERT INTO Charges(
                cabinet_id, dateCharge, montant, libelle, description,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long cabinetId = extractLong(ch, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(ch, "getCabinet"); // si entity a getCabinet().getId()
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dateCharge = extractLocalDateTime(ch, "getDateCharge");
            if (dateCharge != null) ps.setTimestamp(2, Timestamp.valueOf(dateCharge));
            else ps.setNull(2, Types.TIMESTAMP);

            Double montant = extractDouble(ch, "getMontant");
            if (montant != null) ps.setDouble(3, montant);
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, extractString(ch, "getLibelle"));
            ps.setString(5, extractString(ch, "getDescription"));

            // BaseEntity (safe: methods exist via BaseEntity/Lombok)
            LocalDate dc = ch.getDateCreation() != null ? ch.getDateCreation() : LocalDate.now();
            ps.setDate(6, Date.valueOf(dc));
            ch.setDateCreation(dc);

            LocalDateTime dm = ch.getDateDerniereModification() != null ? ch.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(dm));
            ch.setDateDerniereModification(dm);

            ps.setString(8, ch.getCreePar());
            ps.setString(9, ch.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) ch.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Charges ch) {
        String sql = """
            UPDATE Charges SET
                cabinet_id=?, dateCharge=?, montant=?, libelle=?, description=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long cabinetId = extractLong(ch, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(ch, "getCabinet");
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            LocalDateTime dateCharge = extractLocalDateTime(ch, "getDateCharge");
            if (dateCharge != null) ps.setTimestamp(2, Timestamp.valueOf(dateCharge));
            else ps.setNull(2, Types.TIMESTAMP);

            Double montant = extractDouble(ch, "getMontant");
            if (montant != null) ps.setDouble(3, montant);
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, extractString(ch, "getLibelle"));
            ps.setString(5, extractString(ch, "getDescription"));

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, ch.getCreePar());
            ps.setString(8, ch.getModifiePar());
            ps.setLong(9, ch.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Charges ch) {
        if (ch != null) deleteById(ch.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Charges WHERE id = ?";
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
    public List<Charges> findByCabinet(Long cabinetId) {
        String sql = "SELECT * FROM Charges WHERE cabinet_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, cabinetId));
    }

    @Override
    public List<Charges> findByCabinetAndDateBetween(Long cabinetId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM Charges WHERE cabinet_id=? AND dateCharge BETWEEN ? AND ? ORDER BY dateCharge DESC";
        return findList(sql, ps -> {
            ps.setLong(1, cabinetId);
            ps.setTimestamp(2, Timestamp.valueOf(start));
            ps.setTimestamp(3, Timestamp.valueOf(end));
        });
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM Charges WHERE dateCharge BETWEEN ? AND ? ORDER BY dateCharge DESC";
        return findList(sql, ps -> {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));
        });
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Charges WHERE id=?";
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
        String sql = "SELECT COUNT(*) FROM Charges";
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
    public List<Charges> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Charges ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Charges> findList(String sql, PsBinder binder) {
        List<Charges> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCharges(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Charges mapCharges(ResultSet rs) throws SQLException {
        Charges ch = new Charges();

        ch.setId(rs.getLong("id"));

        // champs possibles (si setters existent, on les appelle; sinon on ignore)
        setIfExists(ch, "setLibelle", String.class, safeGetString(rs, "libelle"));
        setIfExists(ch, "setDescription", String.class, safeGetString(rs, "description"));

        Double montant = safeGetDouble(rs, "montant");
        if (montant != null) setIfExists(ch, "setMontant", Double.class, montant);

        LocalDateTime dateCharge = safeGetDateTime(rs, "dateCharge");
        if (dateCharge != null) setIfExists(ch, "setDateCharge", LocalDateTime.class, dateCharge);

        Long cabinetId = safeGetLong(rs, "cabinet_id");
        if (cabinetId != null) setIfExists(ch, "setCabinetId", Long.class, cabinetId);

        // BaseEntity
        Date dc = safeGetDate(rs, "dateCreation");
        if (dc != null) ch.setDateCreation(dc.toLocalDate());

        Timestamp dm = safeGetTimestamp(rs, "dateDerniereModification");
        if (dm != null) ch.setDateDerniereModification(dm.toLocalDateTime());

        ch.setCreePar(safeGetString(rs, "creePar"));
        ch.setModifiePar(safeGetString(rs, "modifiePar"));

        return ch;
    }

    // ---- reflection safe getters (compilation-proof) ----

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
