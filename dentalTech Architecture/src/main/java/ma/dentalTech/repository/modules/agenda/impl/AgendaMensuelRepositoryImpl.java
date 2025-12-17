package ma.dentalTech.repository.modules.agenda.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.agenda.AgendaMensuel;
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
        // pas d'audit (car l'entity ne l'a pas)
        String sql = """
            INSERT INTO AgendaMensuels(medecin_id, mois, annee)
            VALUES (?,?,?)
            """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            Long medecinId = extractLong(a, "getMedecinId");
            if (medecinId == null) medecinId = extractNestedId(a, "getMedecin");
            if (medecinId != null) ps.setLong(1, medecinId);
            else ps.setNull(1, Types.BIGINT);

            String mois = extractString(a, "getMois");
            if (mois == null) {
                Object enumMois = extractObject(a, "getMoisEnum");
                mois = enumMois != null ? enumMois.toString() : null;
            }
            ps.setString(2, mois);

            Integer annee = extractInt(a, "getAnnee");
            if (annee != null) ps.setInt(3, annee);
            else ps.setNull(3, Types.INTEGER);

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

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            Long medecinId = extractLong(a, "getMedecinId");
            if (medecinId == null) medecinId = extractNestedId(a, "getMedecin");
            if (medecinId != null) ps.setLong(1, medecinId);
            else ps.setNull(1, Types.BIGINT);

            String mois = extractString(a, "getMois");
            if (mois == null) {
                Object enumMois = extractObject(a, "getMoisEnum");
                mois = enumMois != null ? enumMois.toString() : null;
            }
            ps.setString(2, mois);

            Integer annee = extractInt(a, "getAnnee");
            if (annee != null) ps.setInt(3, annee);
            else ps.setNull(3, Types.INTEGER);

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
        return findList(sql, ps -> ps.setLong(1, medecinId));
    }

    // ================= Jours non disponibles =================

    @Override
    public List<LocalDate> getJoursNonDisponibles(Long agendaId) {
        String sql = """
            SELECT dateJour
            FROM AgendaMensuel_JoursNonDisponibles
            WHERE agenda_id=?
            ORDER BY dateJour
            """;
        List<LocalDate> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Date d = rs.getDate("dateJour");
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
        String sql = "INSERT INTO AgendaMensuel_JoursNonDisponibles(agenda_id, dateJour) VALUES(?,?)";
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
        String sql = "DELETE FROM AgendaMensuel_JoursNonDisponibles WHERE agenda_id=? AND dateJour=?";
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
        String sql = "DELETE FROM AgendaMensuel_JoursNonDisponibles WHERE agenda_id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, agendaId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ================= Helpers =================

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<AgendaMensuel> findList(String sql, PsBinder binder) {
        List<AgendaMensuel> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapAgenda(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    private AgendaMensuel mapAgenda(ResultSet rs) throws SQLException {
        AgendaMensuel a = new AgendaMensuel();

        a.setId(rs.getLong("id"));

        Long medecinId = safeGetLong(rs, "medecin_id");
        if (medecinId != null) setIfExists(a, "setMedecinId", Long.class, medecinId);

        String mois = safeGetString(rs, "mois");
        if (mois != null) setIfExists(a, "setMois", String.class, mois);

        Integer annee = safeGetInt(rs, "annee");
        if (annee != null) setIfExists(a, "setAnnee", Integer.class, annee);

        return a;
    }

    // ---- Reflection safe getters ----

    private Object extractObject(Object obj, String getter) {
        try { return obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private Long extractLong(Object obj, String getter) {
        try { return (Long) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private Integer extractInt(Object obj, String getter) {
        try { return (Integer) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
    }

    private String extractString(Object obj, String getter) {
        try { return (String) obj.getClass().getMethod(getter).invoke(obj); }
        catch (Exception e) { return null; }
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

    private void setIfExists(Object obj, String setter, Class<?> paramType, Object value) {
        try { obj.getClass().getMethod(setter, paramType).invoke(obj, value); }
        catch (Exception ignored) { }
    }

    // ---- ResultSet safe getters ----

    private String safeGetString(ResultSet rs, String col) {
        try { return rs.getString(col); } catch (SQLException e) { return null; }
    }

    private Long safeGetLong(ResultSet rs, String col) {
        try {
            long v = rs.getLong(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException e) { return null; }
    }

    private Integer safeGetInt(ResultSet rs, String col) {
        try {
            int v = rs.getInt(col);
            return rs.wasNull() ? null : v;
        } catch (SQLException e) { return null; }
    }
}
