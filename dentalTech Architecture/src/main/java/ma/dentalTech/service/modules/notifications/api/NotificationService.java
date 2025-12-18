package ma.dentalTech.service.modules.notifications.api;

import ma.dentalTech.entities.enums.PrioriteNotification;
import ma.dentalTech.entities.enums.TitreNotification;
import ma.dentalTech.entities.enums.TypeNotification;
import ma.dentalTech.entities.users.Notification;

import java.time.LocalDate;
import java.util.List;

public interface NotificationService {

    // CRUD
    List<Notification> findAll();
    Notification findById(Long id);
    void create(Notification notification);
    void update(Notification notification);
    void deleteById(Long id);

    // Recherche
    List<Notification> findByUtilisateur(Long utilisateurId);
    List<Notification> findUnreadByUtilisateur(Long utilisateurId);
    List<Notification> findByDate(Long utilisateurId, LocalDate date);
    List<Notification> findByType(Long utilisateurId, TypeNotification type);
    List<Notification> findByTitre(Long utilisateurId, TitreNotification titre);
    List<Notification> findByPriorite(Long utilisateurId, PrioriteNotification priorite);

    // Actions
    void markAsRead(Long notificationId);
    void markAllAsReadForUser(Long utilisateurId);
}
