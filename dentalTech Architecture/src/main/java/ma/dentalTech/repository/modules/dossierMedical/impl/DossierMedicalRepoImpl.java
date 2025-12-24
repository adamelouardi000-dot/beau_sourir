package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.DossierMedical;
import ma.dentalTech.repository.modules.dossierMedical.api.DossierMedicalRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class DossierMedicalRepoImpl implements DossierMedicalRepo {

    // ✅ NOM TABLE BD (selon ton script SQL)
    private static final String TABLE = "DossiersMedicaux";

    // ---------------- CRUD ----------------

    @Override
    public List<DossierMedical> findAll() {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY dateCreation DESC";
        List<DossierMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapDossierMedical(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public DossierMedical findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapDossierMedical(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(DossierMedical d) {
        String sql = """
            INSERT INTO %s(
                patient_id,
                medecin_id,
                dateOuverture,
                noteGenerale,
                dateCreation,
                dateDerniereModification,
                creePar,
                modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?)
            """.formatted(TABLE);

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // patient_id
            Long patientId = extractIdByReflection(d, "getPatientId", "getPatient");
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            // medecin_id
            Long medecinId = extractIdByReflection(d, "getMedecinId", "getMedecin");
            if (medecinId != null) ps.setLong(2, medecinId);
            else ps.setNull(2, Types.BIGINT);

            LocalDate dateOuverture = readLocalDate(d, "getDateOuverture");
            if (dateOuverture == null) dateOuverture = LocalDate.now();
            ps.setDate(3, Date.valueOf(dateOuverture));
            writeLocalDate(d, "setDateOuverture", dateOuverture);

            String noteGenerale = readString(d, "getNoteGenerale");
            ps.setString(4, noteGenerale);

            LocalDate dc = d.getDateCreation() != null ? d.getDateCreation() : LocalDate.now();
            ps.setDate(5, Date.valueOf(dc));
            d.setDateCreation(dc);

            LocalDateTime dm = d.getDateDerniereModification() != null ? d.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(dm));
            d.setDateDerniereModification(dm);

            ps.setString(7, d.getCreePar());
            ps.setString(8, d.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(DossierMedical d) {
        String sql = """
            UPDATE %s SET
                patient_id=?,
                medecin_id=?,
                dateOuverture=?,
                noteGenerale=?,
                dateCreation=?,
                dateDerniereModification=?,
                creePar=?,
                modifiePar=?
            WHERE id=?
            """.formatted(TABLE);

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long patientId = extractIdByReflection(d, "getPatientId", "getPatient");
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            Long medecinId = extractIdByReflection(d, "getMedecinId", "getMedecin");
            if (medecinId != null) ps.setLong(2, medecinId);
            else ps.setNull(2, Types.BIGINT);

            LocalDate dateOuverture = readLocalDate(d, "getDateOuverture");
            if (dateOuverture == null) dateOuverture = LocalDate.now();
            ps.setDate(3, Date.valueOf(dateOuverture));

            ps.setString(4, readString(d, "getNoteGenerale"));

            LocalDate dc = d.getDateCreation() != null ? d.getDateCreation() : LocalDate.now();
            ps.setDate(5, Date.valueOf(dc));

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, d.getCreePar());
            ps.setString(8, d.getModifiePar());
            ps.setLong(9, d.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DossierMedical d) {
        if (d != null) deleteById(d.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- Extras (selon ton interface) ----------------

    @Override
    public Optional<DossierMedical> findByPatient(Long patientId) {
        String sql = "SELECT * FROM " + TABLE + " WHERE patient_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapDossierMedical(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DossierMedical> findByDateCreationAfter(LocalDate date) {
        String sql = "SELECT * FROM " + TABLE + " WHERE dateCreation > ? ORDER BY dateCreation DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<DossierMedical> findByActif(boolean actif) {
        // ⚠️ ta table DossiersMedicaux n'a pas "actif" dans ton SQL
        // on renvoie juste tout (ou tu peux supprimer cette méthode de l'interface si tu veux)
        return findAll();
    }

    @Override
    public Optional<DossierMedical> findByNumero(String numero) {
        // ⚠️ ta table DossiersMedicaux n'a pas "numero" dans ton SQL
        return Optional.empty();
    }

    @Override
    public List<DossierMedical> findPage(int limit, int offset) {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY dateCreation DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ---------------- Helpers ----------------

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<DossierMedical> findList(String sql, PsBinder binder) {
        List<DossierMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapDossierMedical(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private DossierMedical mapDossierMedical(ResultSet rs) throws SQLException {
        DossierMedical d = new DossierMedical();

        d.setId(rs.getLong("id"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) d.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) d.setDateDerniereModification(dm.toLocalDateTime());

        d.setCreePar(rs.getString("creePar"));
        d.setModifiePar(rs.getString("modifiePar"));

        // patient_id -> setPatientId(Long) si existe
        long patientId = rs.getLong("patient_id");
        if (!rs.wasNull()) {
            invokeIfExists(d, "setPatientId", Long.class, patientId);
        }

        // medecin_id -> setMedecinId(Long) si existe
        long medecinId = rs.getLong("medecin_id");
        if (!rs.wasNull()) {
            invokeIfExists(d, "setMedecinId", Long.class, medecinId);
        }

        Date douv = rs.getDate("dateOuverture");
        if (douv != null) {
            invokeIfExists(d, "setDateOuverture", LocalDate.class, douv.toLocalDate());
        }

        String note = rs.getString("noteGenerale");
        invokeIfExists(d, "setNoteGenerale", String.class, note);

        return d;
    }

    // ---------- Reflection helpers (safe) ----------

    private void invokeIfExists(Object target, String method, Class<?> paramType, Object value) {
        try {
            var m = target.getClass().getMethod(method, paramType);
            m.invoke(target, value);
        } catch (Exception ignored) { }
    }

    private Long extractIdByReflection(DossierMedical d, String getterIdName, String getterObjectName) {
        try {
            // 1) getXxxId()
            var mid = d.getClass().getMethod(getterIdName);
            Object v = mid.invoke(d);
            if (v instanceof Long l) return l;

        } catch (Exception ignored) { }

        try {
            // 2) getXxx().getId()
            var mo = d.getClass().getMethod(getterObjectName);
            Object obj = mo.invoke(d);
            if (obj == null) return null;

            var getId = obj.getClass().getMethod("getId");
            Object id = getId.invoke(obj);
            if (id instanceof Long l) return l;

        } catch (Exception ignored) { }

        return null;
    }

    private LocalDate readLocalDate(Object target, String getter) {
        try {
            var m = target.getClass().getMethod(getter);
            Object v = m.invoke(target);
            return (LocalDate) v;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void writeLocalDate(Object target, String setter, LocalDate value) {
        try {
            var m = target.getClass().getMethod(setter, LocalDate.class);
            m.invoke(target, value);
        } catch (Exception ignored) { }
    }

    private String readString(Object target, String getter) {
        try {
            var m = target.getClass().getMethod(getter);
            Object v = m.invoke(target);
            return (String) v;
        } catch (Exception ignored) {
            return null;
        }
    }
}
