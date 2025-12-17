package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Medicament;
import ma.dentalTech.entities.enums.FormeMedicament;
import ma.dentalTech.repository.modules.dossierMedical.api.MedicamentRepo;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class MedicamentRepoImpl implements MedicamentRepo {

    // ---------------- CRUD ----------------

    @Override
    public List<Medicament> findAll() {
        return findAllOrderByNom();
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM Medicaments WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapMedicament(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Medicament m) {
        String sql = """
            INSERT INTO Medicaments(
                nom, laboratoire, type, forme, remboursable, prixUnitaire, description,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme() != null ? m.getForme().name() : null);
            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) ps.setDouble(6, m.getPrixUnitaire());
            else ps.setNull(6, Types.DECIMAL);

            ps.setString(7, m.getDescription());

            LocalDate dc = m.getDateCreation() != null ? m.getDateCreation() : LocalDate.now();
            ps.setDate(8, Date.valueOf(dc));
            m.setDateCreation(dc);

            LocalDateTime dm = m.getDateDerniereModification() != null ? m.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(9, Timestamp.valueOf(dm));
            m.setDateDerniereModification(dm);

            ps.setString(10, m.getCreePar());
            ps.setString(11, m.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Medicament m) {
        String sql = """
            UPDATE Medicaments SET
                nom=?, laboratoire=?, type=?, forme=?, remboursable=?, prixUnitaire=?, description=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLaboratoire());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme() != null ? m.getForme().name() : null);
            ps.setBoolean(5, m.isRemboursable());

            if (m.getPrixUnitaire() != null) ps.setDouble(6, m.getPrixUnitaire());
            else ps.setNull(6, Types.DECIMAL);

            ps.setString(7, m.getDescription());

            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, m.getCreePar());
            ps.setString(10, m.getModifiePar());
            ps.setLong(11, m.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Medicament m) {
        if (m != null) deleteById(m.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Medicaments WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Extras ----------------

    @Override
    public List<Medicament> findAllOrderByNom() {
        String sql = "SELECT * FROM Medicaments ORDER BY nom";
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapMedicament(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Medicament> findByNom(String nom) {
        String sql = "SELECT * FROM Medicaments WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapMedicament(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Medicament> findByLaboratoire(String laboratoireLike) {
        String sql = "SELECT * FROM Medicaments WHERE laboratoire LIKE ? ORDER BY nom";
        return findList(sql, ps -> ps.setString(1, "%" + laboratoireLike + "%"));
    }

    @Override
    public List<Medicament> findByType(String typeLike) {
        String sql = "SELECT * FROM Medicaments WHERE type LIKE ? ORDER BY nom";
        return findList(sql, ps -> ps.setString(1, "%" + typeLike + "%"));
    }

    @Override
    public List<Medicament> findByForme(FormeMedicament forme) {
        String sql = "SELECT * FROM Medicaments WHERE forme = ? ORDER BY nom";
        return findList(sql, ps -> ps.setString(1, forme.name()));
    }

    @Override
    public List<Medicament> findByRemboursable(boolean remboursable) {
        String sql = "SELECT * FROM Medicaments WHERE remboursable = ? ORDER BY nom";
        return findList(sql, ps -> ps.setBoolean(1, remboursable));
    }

    @Override
    public List<Medicament> findByPrixBetween(Double min, Double max) {
        String sql = "SELECT * FROM Medicaments WHERE prixUnitaire BETWEEN ? AND ? ORDER BY prixUnitaire";
        return findList(sql, ps -> {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
        });
    }

    @Override
    public List<Medicament> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Medicaments ORDER BY nom LIMIT ? OFFSET ?";
        return findList(sql, ps -> {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
        });
    }

    // ---------------- Helpers ----------------

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Medicament> findList(String sql, PsBinder binder) {
        List<Medicament> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapMedicament(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    private Medicament mapMedicament(ResultSet rs) throws SQLException {
        Medicament m = new Medicament();

        m.setId(rs.getLong("id"));
        m.setNom(rs.getString("nom"));
        m.setLaboratoire(rs.getString("laboratoire"));
        m.setType(rs.getString("type"));

        String forme = rs.getString("forme");
        if (forme != null) m.setForme(FormeMedicament.valueOf(forme));

        m.setRemboursable(rs.getBoolean("remboursable"));

        double prix = rs.getDouble("prixUnitaire");
        if (!rs.wasNull()) m.setPrixUnitaire(prix);

        m.setDescription(rs.getString("description"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) m.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) m.setDateDerniereModification(dm.toLocalDateTime());

        m.setCreePar(rs.getString("creePar"));
        m.setModifiePar(rs.getString("modifiePar"));

        return m;
    }
}
