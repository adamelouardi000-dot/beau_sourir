package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Consultation;
import ma.dentalTech.entities.enums.StatutConsultation;
import ma.dentalTech.repository.modules.dossierMedical.api.ConsultationRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ConsultationRepoImpl implements ConsultationRepo {

    // ================= CRUD =================

    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM Consultations ORDER BY dateConsultation DESC";
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapConsultation(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM Consultations WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapConsultation(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Consultation cst) {
        // ✅ Adapté à ta table Consultations (beau_sourir)
        String sql = """
            INSERT INTO Consultations(
                dossier_medical_id,
                rdv_id,
                dateConsultation,
                motif,
                diagnostic,
                noteMedecin,
                dateCreation,
                dateDerniereModification,
                creePar,
                modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 1) dossier_medical_id (obligatoire)
            Long dmId = extractDossierMedicalId(cst);
            if (dmId != null) ps.setLong(1, dmId);
            else ps.setNull(1, Types.BIGINT);

            // 2) rdv_id (nullable)
            Long rdvId = extractRdvId(cst);
            if (rdvId != null) ps.setLong(2, rdvId);
            else ps.setNull(2, Types.BIGINT);

            // 3) dateConsultation (DATETIME)
            LocalDateTime dt = extractDateConsultation(cst);
            ps.setTimestamp(3, Timestamp.valueOf(dt));

            // 4..6 contenu
            ps.setString(4, extractMotif(cst));
            ps.setString(5, extractDiagnostic(cst));
            ps.setString(6, extractNoteMedecin(cst));

            // BaseEntity
            LocalDate dc = (cst.getDateCreation() != null) ? cst.getDateCreation() : LocalDate.now();
            ps.setDate(7, Date.valueOf(dc));
            cst.setDateCreation(dc);

            LocalDateTime ddm = (cst.getDateDerniereModification() != null) ? cst.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(ddm));
            cst.setDateDerniereModification(ddm);

            ps.setString(9, cst.getCreePar());
            ps.setString(10, cst.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cst.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Consultation cst) {
        String sql = """
            UPDATE Consultations SET
                dossier_medical_id=?,
                rdv_id=?,
                dateConsultation=?,
                motif=?,
                diagnostic=?,
                noteMedecin=?,
                dateDerniereModification=?,
                creePar=?,
                modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long dmId = extractDossierMedicalId(cst);
            if (dmId != null) ps.setLong(1, dmId);
            else ps.setNull(1, Types.BIGINT);

            Long rdvId = extractRdvId(cst);
            if (rdvId != null) ps.setLong(2, rdvId);
            else ps.setNull(2, Types.BIGINT);

            LocalDateTime dt = extractDateConsultation(cst);
            ps.setTimestamp(3, Timestamp.valueOf(dt));

            ps.setString(4, extractMotif(cst));
            ps.setString(5, extractDiagnostic(cst));
            ps.setString(6, extractNoteMedecin(cst));

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, cst.getCreePar());
            ps.setString(9, cst.getModifiePar());

            ps.setLong(10, cst.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Consultation cst) {
        if (cst != null) deleteById(cst.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Consultations WHERE id=?";
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
    public List<Consultation> findByDossierMedical(Long dossierMedicalId) {
        String sql = "SELECT * FROM Consultations WHERE dossier_medical_id=? ORDER BY dateConsultation DESC";
        return findList(sql, ps -> ps.setLong(1, dossierMedicalId));
    }

    @Override
    public List<Consultation> findByMedecin(Long medecinId) {
        // ❌ Ta table Consultations n'a pas medecin_id => on renvoie vide (pour respecter l'interface)
        return List.of();
    }

    @Override
    public List<Consultation> findByDate(LocalDate date) {
        // dateConsultation est DATETIME => filtre sur la date
        String sql = "SELECT * FROM Consultations WHERE DATE(dateConsultation)=? ORDER BY dateConsultation DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<Consultation> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Consultations WHERE DATE(dateConsultation) BETWEEN ? AND ? ORDER BY dateConsultation DESC";
        return findList(sql, ps -> {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
        });
    }

    @Override
    public List<Consultation> findByStatut(StatutConsultation statut) {
        // ❌ Ta table Consultations n'a pas statut => vide
        return List.of();
    }

    @Override
    public List<Consultation> findByFacturee(boolean facturee) {
        // ❌ Ta table Consultations n'a pas facturee => vide
        return List.of();
    }

    @Override
    public List<Consultation> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Consultations ORDER BY dateConsultation DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= HELPERS =================

    private interface PsBinder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Consultation> findList(String sql, PsBinder binder) {
        List<Consultation> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Consultation mapConsultation(ResultSet rs) throws SQLException {
        Consultation c = new Consultation();

        c.setId(rs.getLong("id"));

        Timestamp t = rs.getTimestamp("dateConsultation");
        if (t != null) {
            // si ton entity a LocalDateTime => ok
            try {
                var m = c.getClass().getMethod("setDateConsultation", LocalDateTime.class);
                m.invoke(c, t.toLocalDateTime());
            } catch (Exception ignored) {
                // si ton entity a LocalDate => fallback
                try {
                    var m2 = c.getClass().getMethod("setDateConsultation", LocalDate.class);
                    m2.invoke(c, t.toLocalDateTime().toLocalDate());
                } catch (Exception ignored2) {}
            }
        }

        // motif / diagnostic / noteMedecin (selon ton entity)
        trySet(c, "setMotif", String.class, rs.getString("motif"));
        trySet(c, "setDiagnostic", String.class, rs.getString("diagnostic"));
        trySet(c, "setNoteMedecin", String.class, rs.getString("noteMedecin"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());

        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        // dossier_medical_id : on tente setDossierMedicalId(Long) si existe
        long dmId = rs.getLong("dossier_medical_id");
        if (!rs.wasNull()) {
            trySet(c, "setDossierMedicalId", Long.class, dmId);
        }

        // rdv_id : idem
        long rdvId = rs.getLong("rdv_id");
        if (!rs.wasNull()) {
            trySet(c, "setRdvId", Long.class, rdvId);
        }

        return c;
    }

    private void trySet(Object target, String method, Class<?> type, Object value) {
        try {
            var m = target.getClass().getMethod(method, type);
            m.invoke(target, value);
        } catch (Exception ignored) {
        }
    }

    private Long extractDossierMedicalId(Consultation cst) {
        // 1) dossierMedical.getId()
        try {
            Object dm = cst.getDossierMedical();
            if (dm != null) {
                var getId = dm.getClass().getMethod("getId");
                Object id = getId.invoke(dm);
                if (id instanceof Long) return (Long) id;
                if (id instanceof Number) return ((Number) id).longValue();
            }
        } catch (Exception ignored) {}

        // 2) getDossierMedicalId()
        try {
            var m = cst.getClass().getMethod("getDossierMedicalId");
            Object v = m.invoke(cst);
            if (v instanceof Long) return (Long) v;
            if (v instanceof Number) return ((Number) v).longValue();
        } catch (Exception ignored) {}

        return null;
    }

    private Long extractRdvId(Consultation cst) {
        try {
            var m = cst.getClass().getMethod("getRdvId");
            Object v = m.invoke(cst);
            if (v instanceof Long) return (Long) v;
            if (v instanceof Number) return ((Number) v).longValue();
        } catch (Exception ignored) {}
        return null;
    }

    private LocalDateTime extractDateConsultation(Consultation cst) {
        // support LocalDateTime ou LocalDate
        try {
            var m = cst.getClass().getMethod("getDateConsultation");
            Object v = m.invoke(cst);
            if (v instanceof LocalDateTime) return (LocalDateTime) v;
            if (v instanceof LocalDate) return ((LocalDate) v).atStartOfDay();
        } catch (Exception ignored) {}
        return LocalDateTime.now();
    }

    private String extractMotif(Consultation cst) {
        try {
            var m = cst.getClass().getMethod("getMotif");
            Object v = m.invoke(cst);
            return (String) v;
        } catch (Exception ignored) {}
        return null;
    }

    private String extractDiagnostic(Consultation cst) {
        try {
            var m = cst.getClass().getMethod("getDiagnostic");
            Object v = m.invoke(cst);
            return (String) v;
        } catch (Exception ignored) {}
        return null;
    }

    private String extractNoteMedecin(Consultation cst) {
        // selon ton entity ça peut être noteMedecin ou observationMedecin
        try {
            var m = cst.getClass().getMethod("getNoteMedecin");
            Object v = m.invoke(cst);
            return (String) v;
        } catch (Exception ignored) {}

        try {
            var m = cst.getClass().getMethod("getObservationMedecin");
            Object v = m.invoke(cst);
            return (String) v;
        } catch (Exception ignored) {}

        return null;
    }
}
