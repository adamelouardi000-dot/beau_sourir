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

    // ---------------- CRUD ----------------

    @Override
    public List<DossierMedical> findAll() {
        String sql = "SELECT * FROM DossierMedical ORDER BY dateCreation DESC";
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
        String sql = "SELECT * FROM DossierMedical WHERE id = ?";
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
            INSERT INTO DossierMedical(
                patient_id,
                dateCreation,
                dateDerniereModification,
                creePar,
                modifiePar
            )
            VALUES (?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long patientId = extractPatientId(d);
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            LocalDate dc = d.getDateCreation() != null ? d.getDateCreation() : LocalDate.now();
            ps.setDate(2, Date.valueOf(dc));
            d.setDateCreation(dc);

            LocalDateTime dm = d.getDateDerniereModification() != null ? d.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(3, Timestamp.valueOf(dm));
            d.setDateDerniereModification(dm);

            ps.setString(4, d.getCreePar());
            ps.setString(5, d.getModifiePar());

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
            UPDATE DossierMedical SET
                patient_id=?,
                dateCreation=?,
                dateDerniereModification=?,
                creePar=?,
                modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long patientId = extractPatientId(d);
            if (patientId != null) ps.setLong(1, patientId);
            else ps.setNull(1, Types.BIGINT);

            LocalDate dc = d.getDateCreation() != null ? d.getDateCreation() : LocalDate.now();
            ps.setDate(2, Date.valueOf(dc));

            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, d.getCreePar());
            ps.setString(5, d.getModifiePar());
            ps.setLong(6, d.getId());

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
        String sql = "DELETE FROM DossierMedical WHERE id = ?";
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
        String sql = "SELECT * FROM DossierMedical WHERE patient_id = ?";
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
        String sql = "SELECT * FROM DossierMedical WHERE dateCreation > ? ORDER BY dateCreation DESC";
        return findList(sql, ps -> ps.setDate(1, Date.valueOf(date)));
    }

    @Override
    public List<DossierMedical> findByActif(boolean actif) {
        String sql = "SELECT * FROM DossierMedical WHERE actif = ? ORDER BY dateCreation DESC";
        return findList(sql, ps -> ps.setBoolean(1, actif));
    }

    @Override
    public Optional<DossierMedical> findByNumero(String numero) {
        String sql = "SELECT * FROM DossierMedical WHERE numero = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapDossierMedical(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DossierMedical> findPage(int limit, int offset) {
        String sql = "SELECT * FROM DossierMedical ORDER BY dateCreation DESC LIMIT ? OFFSET ?";
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

        long patientId = rs.getLong("patient_id");
        if (!rs.wasNull()) {
            try {
                // si tu as un setter setPatientId(Long) dans l'entity, on le remplit
                var m = d.getClass().getMethod("setPatientId", Long.class);
                m.invoke(d, patientId);
            } catch (Exception ignored) {
                // sinon on laisse patient null (le service pourra hydrater le patient si besoin)
            }
        }

        return d;
    }

    private Long extractPatientId(DossierMedical d) {
        try {
            var p = d.getPatient();
            if (p == null) return null;
            return p.getId();
        } catch (Exception e) {
            return null;
        }
    }
}
