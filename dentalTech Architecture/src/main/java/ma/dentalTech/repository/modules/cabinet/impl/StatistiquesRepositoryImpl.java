package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.Statistiques;
import ma.dentalTech.entities.enums.CategorieStatistique;
import ma.dentalTech.repository.modules.cabinet.api.StatistiquesRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    // ================= CRUD =================

    @Override
    public List<Statistiques> findAll() {
        String sql = "SELECT * FROM Statistiques ORDER BY id DESC";
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapStatistiques(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = "SELECT * FROM Statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapStatistiques(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Statistiques s) {
        // Colonnes "standard" : adapter si ton schema diffère
        String sql = """
            INSERT INTO Statistiques(
                cabinet_id, categorie, dateCalcul, valeur, description,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long cabinetId = extractLong(s, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(s, "getCabinet");
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            CategorieStatistique cat = extractCategorie(s);
            ps.setString(2, cat != null ? cat.name() : null);

            LocalDate dateCalcul = extractLocalDate(s, "getDateCalcul");
            if (dateCalcul != null) ps.setDate(3, Date.valueOf(dateCalcul));
            else ps.setNull(3, Types.DATE);

            Double valeur = extractDouble(s, "getValeur");
            if (valeur != null) ps.setDouble(4, valeur);
            else ps.setNull(4, Types.DECIMAL);

            ps.setString(5, extractString(s, "getDescription"));

            // BaseEntity
            LocalDate dc = s.getDateCreation() != null ? s.getDateCreation() : LocalDate.now();
            ps.setDate(6, Date.valueOf(dc));
            s.setDateCreation(dc);

            LocalDateTime dm = s.getDateDerniereModification() != null ? s.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(dm));
            s.setDateDerniereModification(dm);

            ps.setString(8, s.getCreePar());
            ps.setString(9, s.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Statistiques s) {
        String sql = """
            UPDATE Statistiques SET
                cabinet_id=?, categorie=?, dateCalcul=?, valeur=?, description=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long cabinetId = extractLong(s, "getCabinetId");
            if (cabinetId == null) cabinetId = extractNestedId(s, "getCabinet");
            if (cabinetId != null) ps.setLong(1, cabinetId);
            else ps.setNull(1, Types.BIGINT);

            CategorieStatistique cat = extractCategorie(s);
            ps.setString(2, cat != null ? cat.name() : null);

            LocalDate dateCalcul = extractLocalDate(s, "getDateCalcul");
            if (dateCalcul != null) ps.setDate(3, Date.valueOf(dateCalcul));
            else ps.setNull(3, Types.DATE);

            Double valeur = extractDouble(s, "getValeur");
            if (valeur != null) ps.setDouble(4, valeur);
            else ps.setNull(4, Types.DECIMAL);

            ps.setString(5, extractString(s, "getDescription"));

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, s.getCreePar());
            ps.setString(8, s.getModifiePar());
            ps.setLong(9, s.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Statistiques s) {
        if (s != null) deleteById(s.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Statistiques WHERE id = ?";
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
    public List<Statistiques> findByCabinet(Long cabinetId) {
        String sql = "SELECT * FROM Statistiques WHERE cabinet_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, cabinetId));
    }

    @Override
    public List<Statistiques> findByCategorie(CategorieStatistique categorie) {
        String sql = "SELECT * FROM Statistiques WHERE categorie=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setString(1, categorie.name()));
    }

    @Override
    public List<Statistiques> findByCabinetAndCategorie(Long cabinetId, CategorieStatistique categorie) {
        String sql = "SELECT * FROM Statistiques WHERE cabinet_id=? AND categorie=? ORDER BY id DESC";
        return findList(sql, ps -> {
            ps.setLong(1, cabinetId);
            ps.setString(2, categorie.name());
        });
    }

    @Override
    public List<Statistiques> findByDate(LocalDate dateCalcul) {
        String sql = "SELECT * FROM Statistiques WHERE dateCalcul=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(dateCalcul)));
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Statistiques WHERE id=?";
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
        String sql = "SELECT COUNT(*) FROM Statistiques";
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
    public List<Statistiques> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Statistiques ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Statistiques> findList(String sql, PsBinder binder) {
        List<Statistiques> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapStatistiques(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Statistiques mapStatistiques(ResultSet rs) throws SQLException {
        Statistiques s = new Statistiques();

        s.setId(rs.getLong("id"));

        // champs possibles
        Long cabinetId = safeGetLong(rs, "cabinet_id");
        if (cabinetId != null) setIfExists(s, "setCabinetId", Long.class, cabinetId);

        String cat = safeGetString(rs, "categorie");
        if (cat != null) setIfExists(s, "setCategorie", CategorieStatistique.class, CategorieStatistique.valueOf(cat));

        LocalDate dcCalc = safeGetLocalDate(rs, "dateCalcul");
        if (dcCalc != null) setIfExists(s, "setDateCalcul", LocalDate.class, dcCalc);

        Double val = safeGetDouble(rs, "valeur");
        if (val != null) setIfExists(s, "setValeur", Double.class, val);

        setIfExists(s, "setDescription", String.class, safeGetString(rs, "description"));

        // BaseEntity
        Date dc = safeGetDate(rs, "dateCreation");
        if (dc != null) s.setDateCreation(dc.toLocalDate());

        Timestamp dm = safeGetTimestamp(rs, "dateDerniereModification");
        if (dm != null) s.setDateDerniereModification(dm.toLocalDateTime());

        s.setCreePar(safeGetString(rs, "creePar"));
        s.setModifiePar(safeGetString(rs, "modifiePar"));

        return s;
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

    private LocalDate extractLocalDate(Object obj, String getter) {
        try {
            Object v = obj.getClass().getMethod(getter).invoke(obj);
            return (LocalDate) v;
        } catch (Exception e) {
            return null;
        }
    }

    private CategorieStatistique extractCategorie(Object obj) {
        try {
            Object v = obj.getClass().getMethod("getCategorie").invoke(obj);
            return (CategorieStatistique) v;
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

    private LocalDate safeGetLocalDate(ResultSet rs, String col) {
        Date d = safeGetDate(rs, col);
        return d != null ? d.toLocalDate() : null;
    }
}
