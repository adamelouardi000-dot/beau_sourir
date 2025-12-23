package ma.dentalTech.service.modules.users.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;
import ma.dentalTech.entities.enums.*;
import ma.dentalTech.mvc.dto.*;
import ma.dentalTech.service.modules.users.api.NotificationService;
import ma.dentalTech.service.modules.users.api.UserBackofficeService;

import java.time.LocalDate;
import java.time.LocalTime;

public class TestNotificationService {

    public static void main(String[] args) {

        NotificationService notifService = ApplicationContext.getBean(NotificationService.class);
        UserBackofficeService userService = ApplicationContext.getBean(UserBackofficeService.class);

        Long userId = null;
        Long notifId = null;

        try {
            // créer un user de test (si tu as déjà un user seed, tu peux mettre userId = 1L et supprimer ce bloc)
            UserDto u = userService.createUser(new UserCreateRequest(
                    "NotifUser",
                    "notif_" + System.currentTimeMillis() + "@mail.com",
                    "Adresse",
                    "CIN_NOTIF_" + System.currentTimeMillis(),
                    "0600000000",
                    Sexe.Femme,
                    "login_notif_" + System.currentTimeMillis(),
                    "pass123",
                    LocalDate.of(2000, 1, 1)
            ));
            userId = u.id();
            System.out.println("✅ user created id=" + userId);

            // CREATE NOTIFICATION
            NotificationDto created = notifService.create(new NotificationCreateRequest(
                    TitreNotification.INFO,
                    "Hello Notification",
                    LocalDate.now(),
                    LocalTime.now(),
                    TypeNotification.MESSAGE_SYSTEME,
                    PrioriteNotification.NORMALE,
                    userId
            ));

            notifId = created.id();
            System.out.println("✅ notification created id=" + notifId);

            // LIST
            System.out.println("notifications size => " + notifService.findByUser(userId).size());

            // MARK READ
            notifService.markRead(new MarkNotificationReadRequest(notifId, true));
            System.out.println("✅ marked read");

            // DELETE NOTIF
            notifService.deleteById(notifId);
            notifId = null;
            System.out.println("✅ notification deleted");

            // DELETE USER
            userService.deleteUser(userId);
            userId = null;
            System.out.println("✅ user deleted");

        } finally {
            if (notifId != null) {
                try { notifService.deleteById(notifId); } catch (Exception ignored) {}
            }
            if (userId != null) {
                try { userService.deleteUser(userId); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
