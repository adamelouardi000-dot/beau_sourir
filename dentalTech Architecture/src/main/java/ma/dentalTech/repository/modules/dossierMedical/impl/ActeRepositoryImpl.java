package ma.dentalTech.repository.modules.dossierMedical.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.dossierMedical.Acte;
import ma.dentalTech.repository.modules.dossierMedical.api.ActeRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActeRepositoryImpl implements ActeRepository {

    // ---------------- CRUD ----------------

    @Override
    public List<Acte> findAll() {
        return findAllOrderByLibelle();
    }

    @Override
    public Acte findById(Long id) {
        String sql = "SELECT * FROM Actes WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapActe(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Acte a) {
        String sql = "INSERT INTO Actes(libelle, categorie, prixBase) VALUES (?,?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());

            if (a.getPrixBase() != null) ps.setDouble(3, a.getPrixBase());
            else ps.setNull(3, Types.DECIMAL);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Acte a) {
        String sql = "UPDATE Actes SET libelle=?, categorie=?, prixBase=? WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, a.getLibelle());
            ps.setString(2, a.getCategorie());

            if (a.getPrixBase() != null) ps.setDouble(3, a.getPrixBase());
            else ps.setNull(3, Types.DECIMAL);

            ps.setLong(4, a.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Acte a) {
        if (a != null) deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Actes WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // ---------------- Extras (comme l'interface) ----------------

    @Override
    public List<Acte> findAllOrderByLibelle() {
        String sql = "SELECT * FROM Actes ORDER BY libelle";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapActe(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Optional<Acte> findByLibelle(String libelle) {
        String sql = "SELECT * FROM Actes WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, libelle);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapActe(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Acte> findByPrixBetween(Double min, Double max) {
        String sql = "SELECT * FROM Actes WHERE prixBase BETWEEN ? AND ? ORDER BY prixBase";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapActe(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Acte> searchByLibelle(String keyword) {
        String sql = "SELECT * FROM Actes WHERE libelle LIKE ? ORDER BY libelle";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapActe(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Acte> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Actes ORDER BY libelle LIMIT ? OFFSET ?";
        List<Acte> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapActe(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    // ---------------- Mapper local (car RowMappers n'a pas encore mapActe) ----------------

    private Acte mapActe(ResultSet rs) throws SQLException {
        Acte a = new Acte();
        a.setId(rs.getLong("id"));
        a.setLibelle(rs.getString("libelle"));
        a.setCategorie(rs.getString("categorie"));

        double prix = rs.getDouble("prixBase");
        if (!rs.wasNull()) a.setPrixBase(prix);

        // Champs BaseEntity si présents dans la table (sinon SQL ignore)
        Date dc = rs.getDate("dateCreation");
        if (dc != null) a.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) a.setDateDerniereModification(dm.toLocalDateTime());

        a.setCreePar(rs.getString("creePar"));
        a.setModifiePar(rs.getString("modifiePar"));

        return a;
    }
}
