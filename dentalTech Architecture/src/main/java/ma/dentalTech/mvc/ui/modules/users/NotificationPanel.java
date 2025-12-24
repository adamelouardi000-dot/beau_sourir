package ma.dentalTech.mvc.ui.modules.users;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel {
    public NotificationPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Notifications", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
