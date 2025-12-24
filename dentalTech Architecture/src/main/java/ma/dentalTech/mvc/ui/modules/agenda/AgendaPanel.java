package ma.dentalTech.mvc.ui.modules.agenda;

import javax.swing.*;
import java.awt.*;

public class AgendaPanel extends JPanel {
    public AgendaPanel() {
        setLayout(new BorderLayout());
        add(new JLabel("Agenda RDV", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
