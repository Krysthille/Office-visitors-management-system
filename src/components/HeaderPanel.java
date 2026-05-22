package components;

import javax.swing.*;
import java.awt.*;

public class HeaderPanel extends JPanel {
    
    public HeaderPanel() {
        setLayout(null);
        setBackground(Color.WHITE);
        setBounds(0, 36, 800, 100); // Default bounds, will be updated by parent
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        initializeComponents();
    }

    private void initializeComponents() {
        ImageIcon logoIcon = new ImageIcon("assets/image/dict_logo.png");
        Image logoImg = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
        logoLabel.setBounds(30, 20, 60, 60);
        add(logoLabel);

        JLabel title = new JLabel("  Office Visitor Management System");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(33, 33, 33));
        title.setBounds(110, 28, 600, 30);
        title.setIcon(new ImageIcon("assets/icons/office.png"));
        add(title);

        JLabel subtitle = new JLabel("Department of Information and Communications Technology");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(90, 90, 90));
        subtitle.setBounds(110, 55, 600, 22);
        add(subtitle);
    }

    public void updateBounds(int width) {
        setBounds(0, 36, width, 100);
    }
} 