package ma.dentalTech.mvc.ui.modules.users;

import javax.swing.*;
import java.awt.*;

public class UserPanel extends JPanel {
    public UserPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Gestion des utilisateurs", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
