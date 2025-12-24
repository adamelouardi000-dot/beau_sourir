package ma.dentalTech.mvc.ui;

import ma.dentalTech.mvc.ui.modules.users.UserPanel;
import ma.dentalTech.mvc.ui.modules.users.RolePanel;
import ma.dentalTech.mvc.ui.modules.users.NotificationPanel;
import ma.dentalTech.mvc.ui.modules.agenda.AgendaPanel;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("DentalTech - Back Office (Swing natif)");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("👤 Utilisateurs", new UserPanel());
        tabs.addTab("🔐 Rôles", new RolePanel());
        tabs.addTab("🔔 Notifications", new NotificationPanel());
        tabs.addTab("📅 Agenda", new AgendaPanel());

        add(tabs, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
