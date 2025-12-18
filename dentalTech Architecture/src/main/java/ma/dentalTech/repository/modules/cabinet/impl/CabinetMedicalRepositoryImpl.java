package ma.dentalTech.repository.modules.cabinet.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.cabinet.CabinetMedical;
import ma.dentalTech.entities.users.Staff;
import ma.dentalTech.repository.modules.cabinet.api.CabinetMedicalRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class CabinetMedicalRepositoryImpl implements CabinetMedicalRepository {

    // ---------------- CRUD ----------------

    @Override
    public List<CabinetMedical> findAll() {
        String sql = "SELECT * FROM Cabinets ORDER BY nom";
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapCabinet(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public CabinetMedical findById(Long id) {
        String sql = "SELECT * FROM Cabinets WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapCabinet(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(CabinetMedical cab) {
        String sql = """
            INSERT INTO Cabinets(
                nom, adresse, email,
                dateCreation, dateDerniereModification, creePar, modifiePar
            )
            VALUES (?,?,?,?,?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cab.getNom());
            ps.setString(2, cab.getAdresse());
            ps.setString(3, cab.getEmail());

            LocalDate dc = cab.getDateCreation() != null ? cab.getDateCreation() : LocalDate.now();
            ps.setDate(4, Date.valueOf(dc));
            cab.setDateCreation(dc);

            LocalDateTime dm = cab.getDateDerniereModification() != null ? cab.getDateDerniereModification() : LocalDateTime.now();
            ps.setTimestamp(5, Timestamp.valueOf(dm));
            cab.setDateDerniereModification(dm);

            ps.setString(6, cab.getCreePar());
            ps.setString(7, cab.getModifiePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cab.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CabinetMedical cab) {
        String sql = """
            UPDATE Cabinets SET
                nom=?, adresse=?, email=?,
                dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, cab.getNom());
            ps.setString(2, cab.getAdresse());
            ps.setString(3, cab.getEmail());

            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(5, cab.getCreePar());
            ps.setString(6, cab.getModifiePar());
            ps.setLong(7, cab.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(CabinetMedical cab) {
        if (cab != null) deleteById(cab.getId());
    }

    @Override
    public void deleteById(Long id) {
        // enlever les relations avant suppression (si pas cascade)
        removeAllStaffFromCabinet(id);

        String sql = "DELETE FROM Cabinets WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ---------------- Extras ----------------

    @Override
    public Optional<CabinetMedical> findByNom(String nom) {
        String sql = "SELECT * FROM Cabinets WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nom);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapCabinet(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CabinetMedical> findByEmail(String email) {
        String sql = "SELECT * FROM Cabinets WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapCabinet(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CabinetMedical> searchByNomOrAdresse(String keyword) {
        String sql = "SELECT * FROM Cabinets WHERE nom LIKE ? OR adresse LIKE ? ORDER BY nom";
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            String k = "%" + keyword + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCabinet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM CabinetMedical WHERE id = ?";
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
        String sql = "SELECT COUNT(*) FROM Cabinets";
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
    public List<CabinetMedical> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Cabinets ORDER BY nom LIMIT ? OFFSET ?";
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCabinet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    // ---------------- Many-to-Many Cabinet <-> Staff ----------------

    @Override
    public void addStaffToCabinet(Long cabinetId, Long staffId) {
        String sql = "INSERT INTO Cabinet_Staff(cabinet_id, staff_id) VALUES(?, ?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeStaffFromCabinet(Long cabinetId, Long staffId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=? AND staff_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setLong(2, staffId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeAllStaffFromCabinet(Long cabinetId) {
        String sql = "DELETE FROM Cabinet_Staff WHERE cabinet_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Staff> getStaffOfCabinet(Long cabinetId) {
        String sql = """
            SELECT s.*
            FROM Staff s
            JOIN Cabinet_Staff cs ON cs.staff_id = s.id
            WHERE cs.cabinet_id = ?
            ORDER BY s.nom
            """;
        List<Staff> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<CabinetMedical> getCabinetsOfStaff(Long staffId) {
        String sql = """
            SELECT cm.*
            FROM Cabinets cm
            JOIN Cabinet_Staff cs ON cs.cabinet_id = cm.id
            WHERE cs.staff_id = ?
            ORDER BY cm.nom
            """;
        List<CabinetMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, staffId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapCabinet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    // ---------------- Mappers ----------------

    private CabinetMedical mapCabinet(ResultSet rs) throws SQLException {
        CabinetMedical c = new CabinetMedical();

        c.setId(rs.getLong("id"));
        c.setNom(rs.getString("nom"));
        c.setAdresse(rs.getString("adresse"));
        c.setEmail(rs.getString("email"));

        Date dc = rs.getDate("dateCreation");
        if (dc != null) c.setDateCreation(dc.toLocalDate());

        Timestamp dm = rs.getTimestamp("dateDerniereModification");
        if (dm != null) c.setDateDerniereModification(dm.toLocalDateTime());

        c.setCreePar(rs.getString("creePar"));
        c.setModifiePar(rs.getString("modifiePar"));

        return c;
    }

    private Staff mapStaff(ResultSet rs) throws SQLException {
        Staff s = new Staff();

        s.setId(rs.getLong("id"));
        s.setNom(rs.getString("nom"));
        s.setEmail(rs.getString("email"));

        return s;
    }
}
