package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.dossierMedical.Ordonnance;
import ma.dentalTech.entities.dossierMedical.Prescription;
import ma.dentalTech.repository.modules.dossierMedical.api.PrescriptionRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PrescriptionRepoImpl implements PrescriptionRepo {

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM Prescriptions ORDER BY id DESC";
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapPrescription(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM Prescriptions WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapPrescription(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Prescription p) {
        // Table: ordonnance_id, medicament_id, posologie, duree(VARCHAR), quantite + BaseEntity
        String sql = """
            INSERT INTO Prescriptions(
                ordonnance_id,
                medicament_id,
                posologie,
                duree,
                quantite,
                dateCreation,
                dateDerniereModification,
                creePar,
                modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long ordonnanceId = extractOrdonnanceId(p);
            Long medicamentId = extractMedicamentId(p);

            if (ordonnanceId == null) {
                throw new IllegalArgumentException("ordonnanceId obligatoire (NULL trouvé dans Prescription)");
            }
            if (medicamentId == null) {
                throw new IllegalArgumentException("medicamentId obligatoire (NULL trouvé dans Prescription)");
            }

            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);

            // posologie NOT NULL -> entity.frequence
            String posologie = p.getFrequence();
            if (posologie == null || posologie.isBlank()) posologie = "1 fois / jour";
            ps.setString(3, posologie);

            // duree est VARCHAR(80) en BD -> on stocke le nombre de jours
            String dureeStr = (p.getDureeEnJours() != null) ? String.valueOf(p.getDureeEnJours()) : null;
            ps.setString(4, dureeStr);

            if (p.getQuantite() != null) ps.setInt(5, p.getQuantite());
            else ps.setNull(5, Types.INTEGER);

            LocalDate dc = (p.getDateCreation() != null) ? p.getDateCreation() : LocalDate.now();
            ps.setDate(6, Date.valueOf(dc));
            p.setDateCreation(dc);

            LocalDateTime dm = (p.getDateDerniereModification() != null) ? p.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(dm));
            p.setDateDerniereModification(dm);

            ps.setString(8, p.getCreePar());
            ps.setString(9, p.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Prescription p) {
        String sql = """
            UPDATE Prescriptions SET
                ordonnance_id=?,
                medicament_id=?,
                posologie=?,
                duree=?,
                quantite=?,
                dateDerniereModification=?,
                creePar=?,
                modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long ordonnanceId = extractOrdonnanceId(p);
            Long medicamentId = extractMedicamentId(p);

            if (ordonnanceId == null) throw new IllegalArgumentException("ordonnanceId obligatoire");
            if (medicamentId == null) throw new IllegalArgumentException("medicamentId obligatoire");

            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);

            String posologie = p.getFrequence();
            if (posologie == null || posologie.isBlank()) posologie = "1 fois / jour";
            ps.setString(3, posologie);

            ps.setString(4, (p.getDureeEnJours() != null) ? String.valueOf(p.getDureeEnJours()) : null);

            if (p.getQuantite() != null) ps.setInt(5, p.getQuantite());
            else ps.setNull(5, Types.INTEGER);

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, p.getCreePar());
            ps.setString(8, p.getModifiePar());
            ps.setLong(9, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Prescription p) {
        if (p != null) deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Prescriptions WHERE id = ?";
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
    public List<Prescription> findByOrdonnance(Long ordonnanceId) {
        String sql = "SELECT * FROM Prescriptions WHERE ordonnance_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, ordonnanceId));
    }

    @Override
    public List<Prescription> findByMedicament(Long medicamentId) {
        String sql = "SELECT * FROM Prescriptions WHERE medicament_id=? ORDER BY id DESC";
        return findList(sql, ps -> ps.setLong(1, medicamentId));
    }

    @Override
    public List<Prescription> findByOrdonnanceAndMedicament(Long ordonnanceId, Long medicamentId) {
        String sql = "SELECT * FROM Prescriptions WHERE ordonnance_id=? AND medicament_id=? ORDER BY id DESC";
        return findList(sql, ps -> {
            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);
        });
    }

    @Override
    public List<Prescription> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Prescriptions ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Prescription> findList(String sql, PsBinder binder) {
        List<Prescription> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapPrescription(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private Prescription mapPrescription(ResultSet rs) throws SQLException {
        Prescription p = new Prescription();
        p.setId(rs.getLong("id"));

        // ordonnance
        long ordonnanceId = rs.getLong("ordonnance_id");
        if (!rs.wasNull()) {
            Ordonnance o = new Ordonnance();
            setIdAny(o, ordonnanceId);
            p.setOrdonnance(o);
        }

        // medicament
        long medicamentId = rs.getLong("medicament_id");
        if (!rs.wasNull()) {
            Medicament m = new Medicament();
            setIdAny(m, medicamentId);
            p.setMedicament(m);
        }

        // posologie -> frequence
        p.setFrequence(rs.getString("posologie"));

        // duree (varchar) -> dureeEnJours (Integer) si possible
        String dureeStr = rs.getString("duree");
        if (dureeStr != null) {
            try { p.setDureeEnJours(Integer.parseInt(dureeStr.trim())); }
            catch (Exception ignored) { /* si non numérique, ignore */ }
        }

        int qte = rs.getInt("quantite");
        if (!rs.wasNull()) p.setQuantite(qte);

        Date dc = rs.getDate("dateCreation");
        if (dc != null) p.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) p.setDateDerniereModification(dm.toLocalDateTime());

        p.setCreePar(rs.getString("creePar"));
        p.setModifiePar(rs.getString("modifiePar"));

        return p;
    }

    private Long extractOrdonnanceId(Prescription p) {
        if (p.getOrdonnance() != null && p.getOrdonnance().getId() != null) return p.getOrdonnance().getId();
        return null;
    }

    private Long extractMedicamentId(Prescription p) {
        if (p.getMedicament() != null && p.getMedicament().getId() != null) return p.getMedicament().getId();
        return null;
    }

    private void setIdAny(Object obj, long id) {
        try {
            obj.getClass().getMethod("setId", Long.class).invoke(obj, id);
            return;
        } catch (Exception ignored) {}
        try {
            obj.getClass().getMethod("setId", long.class).invoke(obj, id);
        } catch (Exception ignored) {}
    }
}
