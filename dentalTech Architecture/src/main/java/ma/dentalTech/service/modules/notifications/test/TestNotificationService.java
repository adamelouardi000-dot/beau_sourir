package ma.dentalTech.service.modules.notifications.test;

import ma.dentalTech.configuration.ApplicationContext;
import ma.dentalTech.configuration.SessionFactory;

import ma.dentalTech.entities.users.Notification;
import ma.dentalTech.service.modules.notifications.api.NotificationService;

public class TestNotificationService {

    public static void main(String[] args) {
        NotificationService service = ApplicationContext.getBean(NotificationService.class);

        Long id = null;

        try {
            // ⚠️ Ici on ne “devine” pas les champs obligatoires de Notification
            // donc on fait un test minimal de lecture si ton DB n'est pas prête.
            System.out.println("findAll size = " + service.findAll().size());

            // Si ton entity Notification a un builder/constructeur simple,
            // tu peux décommenter et compléter selon tes champs NOT NULL :
            /*
            Notification n = new Notification();
            // n.set... (selon ton entity)
            service.create(n);
            id = n.getId();
            System.out.println("✅ create notification id=" + id);

            service.markAsRead(id);
            System.out.println("✅ markAsRead OK");

            service.deleteById(id);
            System.out.println("✅ delete OK");
            id = null;
            */

        } finally {
            if (id != null) {
                try { service.deleteById(id); } catch (Exception ignored) {}
            }
            SessionFactory.getInstance().closeConnection();
        }
    }
}
