package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.SituationFinanciere;
import ma.dentalTech.entities.enums.StatutSituationFinanciere;
import ma.dentalTech.repository.modules.dossierMedical.api.SituationFinanciereRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class SituationFinanciereRepoImpl implements SituationFinanciereRepo {

    // ================= CRUD =================

    @Override
    public List<SituationFinanciere> findAll() {
        String sql = "SELECT * FROM SituationFinancieres ORDER BY id DESC";
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapSituation(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public SituationFinanciere findById(Long id) {
        String sql = "SELECT * FROM SituationFinancieres WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapSituation(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(SituationFinanciere s) {
        String sql = """
            INSERT INTO SituationFinancieres(
                totaleDesActes, totalePaye, credit, statut, enPromo, dossierMedical_id,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (s.getTotaleDesActes() != null) ps.setDouble(1, s.getTotaleDesActes());
            else ps.setNull(1, Types.DECIMAL);

            if (s.getTotalePaye() != null) ps.setDouble(2, s.getTotalePaye());
            else ps.setNull(2, Types.DECIMAL);

            if (s.getCredit() != null) ps.setDouble(3, s.getCredit());
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, s.getStatut() != null ? s.getStatut().name() : null);
            ps.setBoolean(5, s.isEnPromo());

            Long dossierId = (s.getDossierMedical() != null) ? s.getDossierMedical().getId() : null;
            if (dossierId != null) ps.setLong(6, dossierId);
            else ps.setNull(6, Types.BIGINT);

            ps.setDate(7, Date.valueOf(s.getDateCreation() != null ? s.getDateCreation() : LocalDate.now()));
            ps.setTimestamp(8, Timestamp.valueOf(s.getDateDerniereModification() != null ? s.getDateDerniereModification() : LocalDateTime.now()));
            ps.setString(9, s.getCreePar());
            ps.setString(10, s.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(SituationFinanciere s) {
        String sql = """
            UPDATE SituationFinancieres SET
                totaleDesActes=?, totalePaye=?, credit=?, statut=?, enPromo=?, dossierMedical_id=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            if (s.getTotaleDesActes() != null) ps.setDouble(1, s.getTotaleDesActes());
            else ps.setNull(1, Types.DECIMAL);

            if (s.getTotalePaye() != null) ps.setDouble(2, s.getTotalePaye());
            else ps.setNull(2, Types.DECIMAL);

            if (s.getCredit() != null) ps.setDouble(3, s.getCredit());
            else ps.setNull(3, Types.DECIMAL);

            ps.setString(4, s.getStatut() != null ? s.getStatut().name() : null);
            ps.setBoolean(5, s.isEnPromo());

            Long dossierId = (s.getDossierMedical() != null) ? s.getDossierMedical().getId() : null;
            if (dossierId != null) ps.setLong(6, dossierId);
            else ps.setNull(6, Types.BIGINT);

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, s.getCreePar());
            ps.setString(9, s.getModifiePar());
            ps.setLong(10, s.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(SituationFinanciere s) {
        if (s != null) deleteById(s.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM SituationFinancieres WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ================= QUERIES =================

    @Override
    public Optional<SituationFinanciere> findByDossierMedical(Long dossierMedicalId) {
        String sql = "SELECT * FROM SituationFinancieres WHERE dossierMedical_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, dossierMedicalId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapSituation(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<SituationFinanciere> findByStatut(StatutSituationFinanciere statut) {
        String sql = "SELECT * FROM SituationFinancieres WHERE statut = ? ORDER BY id DESC";
        return findList(sql, ps -> ps.setString(1, statut.name()));
    }

    @Override
    public List<SituationFinanciere> findByEnPromo(boolean enPromo) {
        String sql = "SELECT * FROM SituationFinancieres WHERE enPromo = ? ORDER BY id DESC";
        return findList(sql, ps -> ps.setBoolean(1, enPromo));
    }

    @Override
    public List<SituationFinanciere> findByCreditBetween(Double min, Double max) {
        String sql = "SELECT * FROM SituationFinancieres WHERE credit BETWEEN ? AND ? ORDER BY credit";
        return findList(sql, ps -> {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
        });
    }

    @Override
    public List<SituationFinanciere> findPage(int limit, int offset) {
        String sql = "SELECT * FROM SituationFinancieres ORDER BY id DESC LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ================= HELPERS =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<SituationFinanciere> findList(String sql, PsBinder binder) {
        List<SituationFinanciere> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapSituation(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private SituationFinanciere mapSituation(ResultSet rs) throws SQLException {
        SituationFinanciere s = new SituationFinanciere();

        s.setId(rs.getLong("id"));

        double tda = rs.getDouble("totaleDesActes");
        if (!rs.wasNull()) s.setTotaleDesActes(tda);

        double tp = rs.getDouble("totalePaye");
        if (!rs.wasNull()) s.setTotalePaye(tp);

        double credit = rs.getDouble("credit");
        if (!rs.wasNull()) s.setCredit(credit);

        String st = rs.getString("statut");
        if (st != null) s.setStatut(StatutSituationFinanciere.valueOf(st));

        s.setEnPromo(rs.getBoolean("enPromo"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) s.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) s.setDateDerniereModification(dm.toLocalDateTime());

        s.setCreePar(rs.getString("creePar"));
        s.setModifiePar(rs.getString("modifiePar"));

        // dossierMedical non hydraté ici (service pourra charger)
        return s;
    }
}
