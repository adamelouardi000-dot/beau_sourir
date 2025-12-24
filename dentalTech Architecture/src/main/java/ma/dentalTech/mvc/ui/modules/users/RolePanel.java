package ma.dentalTech.mvc.ui.modules.users;

import javax.swing.*;
import java.awt.*;

public class RolePanel extends JPanel {
    public RolePanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Gestion des rôles", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
