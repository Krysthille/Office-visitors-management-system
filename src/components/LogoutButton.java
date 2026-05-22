package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogoutButton extends JButton {
    private JFrame frame;

    public LogoutButton(JFrame frame) {
        this.frame = frame;
        
        setText("Logout");
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(new Color(248, 249, 250)); // Light gray background
        setForeground(new Color(108, 117, 125)); // Gray text
        setFocusPainted(false);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(true);
        setBorderPainted(true);
        setMargin(new Insets(6, 16, 6, 16));
        setPreferredSize(new Dimension(100, 32));
        
        setupEventListeners();
    }

    private void setupEventListeners() {
        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                setBackground(new Color(220, 53, 69)); // Red on hover
                setForeground(Color.WHITE);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 53, 69), 1),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)
                ));
            }
            
            public void mouseExited(MouseEvent evt) {
                setBackground(new Color(248, 249, 250)); // Light gray background
                setForeground(new Color(108, 117, 125)); // Gray text
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                    BorderFactory.createEmptyBorder(6, 16, 6, 16)
                ));
            }
        });
        
        addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                frame, 
                "Are you sure you want to logout?", 
                "Confirm Logout", 
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                frame.dispose();
                // You can add code here to return to the main login screen
                // For now, just close the dashboard
            }
        });
    }

    public void updatePosition(int width) {
        setBounds(width - 120, 6, 100, 32);
    }
} 