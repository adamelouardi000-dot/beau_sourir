package ma.dentalTech.service.modules.users.impl;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.entities.users.Utilisateur;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.service.modules.users.api.NotificationService;
import ma.dentalTech.mvc.dto.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repo;

    public NotificationServiceImpl(NotificationRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<NotificationDto> findByUser(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId obligatoire");

        // ✅ Repo doit exposer findByUserId(Long)
        return repo.findByUserId(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public NotificationDto create(NotificationCreateRequest r) {
        validateCreate(r);

        Notification n = new Notification();
        n.setTitre(r.titre());
        n.setMessage(r.message());
        n.setDate(r.date() != null ? r.date() : LocalDate.now());
        n.setTime(r.time() != null ? r.time() : LocalTime.now());
        n.setType(r.type());
        n.setPriorite(r.priorite());
        n.setLue(false);

        // ✅ Au lieu de setUtilisateurId(...)
        Utilisateur u = new Utilisateur();
        u.setId(r.utilisateurId());
        n.setUtilisateur(u);

        repo.create(n);
        return toDto(n);
    }

    @Override
    public void markRead(MarkNotificationReadRequest r) {
        if (r == null) throw new IllegalArgumentException("MarkNotificationReadRequest null");
        if (r.notificationId() == null) throw new IllegalArgumentException("notificationId obligatoire");

        Notification n = repo.findById(r.notificationId());
        if (n == null) throw new RuntimeException("Notification introuvable id=" + r.notificationId());

        n.setLue(r.lue());
        repo.update(n);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    private NotificationDto toDto(Notification n) {
        Long userId = (n.getUtilisateur() != null) ? n.getUtilisateur().getId() : null;

        return new NotificationDto(
                n.getId(),
                n.getTitre(),
                n.getMessage(),
                n.getDate(),
                n.getTime(),
                n.getType(),
                n.getPriorite(),
                n.isLue(),
                userId
        );
    }

    private void validateCreate(NotificationCreateRequest r) {
        if (r == null) throw new IllegalArgumentException("NotificationCreateRequest null");
        if (r.titre() == null) throw new IllegalArgumentException("titre obligatoire");
        if (r.message() == null || r.message().isBlank()) throw new IllegalArgumentException("message obligatoire");
        if (r.type() == null) throw new IllegalArgumentException("type obligatoire");
        if (r.priorite() == null) throw new IllegalArgumentException("priorite obligatoire");
        if (r.utilisateurId() == null) throw new IllegalArgumentException("utilisateurId obligatoire");
    }
}
