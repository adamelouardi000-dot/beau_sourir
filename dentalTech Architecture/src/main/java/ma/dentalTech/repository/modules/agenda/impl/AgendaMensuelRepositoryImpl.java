package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
import ma.dentalTech.entities.enums.Mois;
import ma.dentalTech.repository.modules.agenda.api.AgendaMensuelRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class AgendaMensuelRepositoryImpl implements AgendaMensuelRepository {

    // ================= CRUD =================

    @Override
    public List<AgendaMensuel> findAll() {
        String sql = "SELECT * FROM AgendaMensuels ORDER BY annee DESC, mois DESC";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapAgenda(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public AgendaMensuel findById(Long id) {
        String sql = "SELECT * FROM AgendaMensuels WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapAgenda(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(AgendaMensuel a) {
        String sql = """
            INSERT INTO AgendaMensuels(medecin_id, mois, annee)
            VALUES (?,?,?)
            """;

        if (a.getMois() == null) {
            throw new RuntimeException("AgendaMensuel.mois est NULL (il faut set Mois.JANVIER etc.)");
        }
        if (a.getAnnee() == null) {
            throw new RuntimeException("AgendaMensuel.annee est NULL");
        }
        if (a.getMedecinId() == null) {
            throw new RuntimeException("AgendaMensuel.medecinId est NULL");
        }

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, a.getMedecinId());
            ps.setString(2, a.getMois().name());   // ✅ ENUM -> String
            ps.setInt(3, a.getAnnee());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(AgendaMensuel a) {
        String sql = """
            UPDATE AgendaMensuels SET
                medecin_id=?, mois=?, annee=?
            WHERE id=?
            """;

        if (a.getId() == null) throw new RuntimeException("AgendaMensuel.id est NULL (update impossible)");
        if (a.getMois() == null) throw new RuntimeException("AgendaMensuel.mois est NULL");
        if (a.getAnnee() == null) throw new RuntimeException("AgendaMensuel.annee est NULL");
        if (a.getMedecinId() == null) throw new RuntimeException("AgendaMensuel.medecinId est NULL");

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, a.getMedecinId());
            ps.setString(2, a.getMois().name());   // ✅
            ps.setInt(3, a.getAnnee());
            ps.setLong(4, a.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(AgendaMensuel a) {
        if (a != null) deleteById(a.getId());
    }

    @Override
    public void deleteById(Long id) {
        clearJoursNonDisponibles(id);

        String sql = "DELETE FROM AgendaMensuels WHERE id = ?";
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
    public Optional<AgendaMensuel> findByMedecinAndMoisAndAnnee(Long medecinId, String moisEnumName, int annee) {
        String sql = "SELECT * FROM AgendaMensuels WHERE medecin_id=? AND mois=? AND annee=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            ps.setString(2, moisEnumName);
            ps.setInt(3, annee);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapAgenda(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AgendaMensuel> findByMedecin(Long medecinId) {
        String sql = "SELECT * FROM AgendaMensuels WHERE medecin_id=? ORDER BY annee DESC, mois DESC";
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapAgenda(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    // ================= Jours non disponibles =================

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        String sql = """
            SELECT jour
            FROM Agenda_JoursNonDisponibles
            WHERE agenda_id=?
            ORDER BY jour
            """;
        List<LocalDate> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("jour");
                    if (d != null) out.add(d.toLocalDate());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public void addJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "INSERT INTO Agenda_JoursNonDisponibles(agenda_id, jour) VALUES(?,?)";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeJourNonDisponible(Long agendaId, LocalDate date) {
        String sql = "DELETE FROM Agenda_JoursNonDisponibles WHERE agenda_id=? AND jour=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.setDate(2, Date.valueOf(date));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clearJoursNonDisponibles(Long agendaId) {
        String sql = "DELETE FROM Agenda_JoursNonDisponibles WHERE agenda_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Mapping =================

    private AgendaMensuel mapAgenda(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();
        a.setId(rs.getLong("id"));
        a.setMedecinId(rs.getLong("medecin_id"));

        String moisStr = rs.getString("mois");
        if (moisStr != null) a.setMois(Mois.valueOf(moisStr)); // ✅ String -> ENUM

        a.setAnnee(rs.getInt("annee"));
        return a;
    }
}
