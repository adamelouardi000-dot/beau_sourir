package ma.dentalTech.repository.modules.users.impl;

import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.common.RowMappers;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM Notifications ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapNotification(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
    @Override
    public List<Notification> findByUserId(Long userId) {
        String sql = """
        SELECT * FROM Notifications
        WHERE utilisateur_id=?
        ORDER BY date DESC, time DESC
        """;
        List<Notification> out = new java.util.ArrayList<>();
        try (java.sql.Connection c = SessionFactory.getInstance().getConnection();
             java.sql.PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Notification n = new Notification();
                    n.setId(rs.getLong("id"));
                    n.setTitre(ma.dentalTech.entities.enums.TitreNotification.valueOf(rs.getString("titre")));
                    n.setMessage(rs.getString("message"));
                    n.setDate(rs.getDate("date").toLocalDate());
                    n.setTime(rs.getTime("time").toLocalTime());
                    n.setType(ma.dentalTech.entities.enums.TypeNotification.valueOf(rs.getString("type")));
                    n.setPriorite(ma.dentalTech.entities.enums.PrioriteNotification.valueOf(rs.getString("priorite")));
                    n.setLue(rs.getBoolean("lue"));

                    // ⚠️ si Notification a un champ Utilisateur (pas utilisateurId)
                    ma.dentalTech.entities.users.Utilisateur u = new ma.dentalTech.entities.users.Utilisateur();
                    u.setId(rs.getLong("utilisateur_id"));
                    n.setUtilisateur(u);

                    out.add(n);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Erreur findByUserId userId=" + userId, e);
        }
        return out;
    }


    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM Notifications WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapNotification(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Notification n) {
        String sql = """
            INSERT INTO Notifications(titre,message,date,time,type,priorite,lue,utilisateur_id,
                                      dateCreation,dateDerniereModification,creePar,modifiePar)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, Date.valueOf(n.getDate()));
            ps.setTime(4, Time.valueOf(n.getTime()));
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setBoolean(7, n.isLue());
            ps.setLong(8, n.getUtilisateur().getId());

            ps.setDate(9, Date.valueOf(java.time.LocalDate.now()));
            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(11, n.getCreePar());
            ps.setString(12, n.getModifiePar());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Notification n) {
        String sql = """
            UPDATE Notifications SET titre=?, message=?, date=?, time=?, type=?, priorite=?, lue=?,
                                    utilisateur_id=?, dateDerniereModification=?, creePar=?, modifiePar=?
            WHERE id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, Date.valueOf(n.getDate()));
            ps.setTime(4, Time.valueOf(n.getTime()));
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setBoolean(7, n.isLue());
            ps.setLong(8, n.getUtilisateur().getId());

            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(10, n.getCreePar());
            ps.setString(11, n.getModifiePar());
            ps.setLong(12, n.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Notification n) {
        if (n != null) deleteById(n.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Notifications WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- Queries --------

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? ORDER BY date DESC, time DESC";
        return findList(sql, ps -> ps.setLong(1, utilisateurId));
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? AND lue=FALSE ORDER BY date DESC, time DESC";
        return findList(sql, ps -> ps.setLong(1, utilisateurId));
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? AND date=? ORDER BY time DESC";
        return findList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setDate(2, Date.valueOf(date));
        });
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? AND type=? ORDER BY date DESC, time DESC";
        return findList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setString(2, type.name());
        });
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? AND titre=? ORDER BY date DESC, time DESC";
        return findList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setString(2, titre.name());
        });
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        String sql = "SELECT * FROM Notifications WHERE utilisateur_id=? AND priorite=? ORDER BY date DESC, time DESC";
        return findList(sql, ps -> {
            ps.setLong(1, utilisateurId);
            ps.setString(2, priorite.name());
        });
    }

    @Override
    public void markAsRead(Long notificationId) {
        String sql = "UPDATE Notifications SET lue=TRUE, dateDerniereModification=CURRENT_TIMESTAMP WHERE id=?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, notificationId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        String sql = """
            UPDATE Notifications
            SET lue=TRUE, dateDerniereModification=CURRENT_TIMESTAMP
            WHERE utilisateur_id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, utilisateurId);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // -------- small helper (style simple, pas framework) --------

    private interface PsBinder { void bind(PreparedStatement ps) throws SQLException; }

    private List<Notification> findList(String sql, PsBinder binder) {
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }
}
