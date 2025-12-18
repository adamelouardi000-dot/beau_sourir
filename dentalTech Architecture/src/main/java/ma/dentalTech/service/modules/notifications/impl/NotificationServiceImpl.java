package ma.dentalTech.service.modules.notifications.impl;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.repository.modules.users.api.NotificationRepository;
import ma.dentalTech.service.modules.notifications.api.NotificationService;

import java.time.LocalDate;
import java.util.List;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepo;

    public NotificationServiceImpl(NotificationRepository notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    @Override
    public List<Notification> findAll() {
        return notificationRepo.findAll();
    }

    @Override
    public Notification findById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        Notification n = notificationRepo.findById(id);
        if (n == null) throw new IllegalArgumentException("Notification introuvable (id=" + id + ")");
        return n;
    }

    @Override
    public void create(Notification notification) {
        if (notification == null) throw new IllegalArgumentException("notification ne doit pas être null");
        notificationRepo.create(notification);
    }

    @Override
    public void update(Notification notification) {
        if (notification == null) throw new IllegalArgumentException("notification ne doit pas être null");
        if (notification.getId() == null) throw new IllegalArgumentException("id obligatoire pour update");
        if (notificationRepo.findById(notification.getId()) == null)
            throw new IllegalArgumentException("Notification introuvable (id=" + notification.getId() + ")");
        notificationRepo.update(notification);
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) throw new IllegalArgumentException("id ne doit pas être null");
        notificationRepo.deleteById(id);
    }

    @Override
    public List<Notification> findByUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        return notificationRepo.findByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findUnreadByUtilisateur(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        return notificationRepo.findUnreadByUtilisateur(utilisateurId);
    }

    @Override
    public List<Notification> findByDate(Long utilisateurId, LocalDate date) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (date == null) throw new IllegalArgumentException("date ne doit pas être null");
        return notificationRepo.findByDate(utilisateurId, date);
    }

    @Override
    public List<Notification> findByType(Long utilisateurId, TypeNotification type) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (type == null) throw new IllegalArgumentException("type ne doit pas être null");
        return notificationRepo.findByType(utilisateurId, type);
    }

    @Override
    public List<Notification> findByTitre(Long utilisateurId, TitreNotification titre) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (titre == null) throw new IllegalArgumentException("titre ne doit pas être null");
        return notificationRepo.findByTitre(utilisateurId, titre);
    }

    @Override
    public List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        if (priorite == null) throw new IllegalArgumentException("priorite ne doit pas être null");
        return notificationRepo.findByPriorite(utilisateurId, priorite);
    }

    @Override
    public void markAsRead(Long notificationId) {
        if (notificationId == null) throw new IllegalArgumentException("notificationId ne doit pas être null");
        notificationRepo.markAsRead(notificationId);
    }

    @Override
    public void markAllAsReadForUser(Long utilisateurId) {
        if (utilisateurId == null) throw new IllegalArgumentException("utilisateurId ne doit pas être null");
        notificationRepo.markAllAsReadForUser(utilisateurId);
    }
}
