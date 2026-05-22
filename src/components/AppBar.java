package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AppBar extends JPanel {
    public AppBar(JFrame frame) {
        setBackground(new Color(111, 19, 19)); // Maroon
        setLayout(null);
        int barHeight = 36;
        setPreferredSize(new Dimension(1200, barHeight));

        // Scale icons to 24x24
        ImageIcon closeIcon = new ImageIcon(new ImageIcon("assets/icons/close-white.png").getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH));
        ImageIcon minIcon = new ImageIcon(new ImageIcon("assets/icons/minimize-white.png").getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH));
        ImageIcon maxIcon = new ImageIcon(new ImageIcon("assets/icons/maximize_white.png").getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH));

        int btnW = 28, btnH = 28, btnGap = 8, rightPad = 8;
        int y = (barHeight - btnH) / 2;
        int barWidth = 1200;
        int xClose = barWidth - rightPad - btnW;
        int xMax = xClose - btnGap - btnW;
        int xMin = xMax - btnGap - btnW;

        JButton minBtn = new JButton(minIcon);
        minBtn.setFocusPainted(false);
        minBtn.setBorderPainted(false);
        minBtn.setContentAreaFilled(false);
        minBtn.setBounds(xMin, y, btnW, btnH);
        minBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        minBtn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    for (float opacity = 1.0f; opacity > 0.0f; opacity -= 0.07f) {
                        final float op = opacity;
                        SwingUtilities.invokeLater(() -> {
                            try { frame.setOpacity(op); } catch (Exception ex) {}
                        });
                        Thread.sleep(15); // Adjust for speed
                    }
                    SwingUtilities.invokeLater(() -> {
                        frame.setState(Frame.ICONIFIED);
                        try { frame.setOpacity(1.0f); } catch (Exception ex) {}
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });
        add(minBtn);

        JButton maxBtn = new JButton(maxIcon);
        maxBtn.setFocusPainted(false);
        maxBtn.setBorderPainted(false);
        maxBtn.setContentAreaFilled(false);
        maxBtn.setBounds(xMax, y, btnW, btnH);
        maxBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        maxBtn.addActionListener(e -> {
            if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                frame.setExtendedState(Frame.NORMAL);
            } else {
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        });
        add(maxBtn);

        JButton closeBtn = new JButton(closeIcon);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBounds(xClose, y, btnW, btnH);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> System.exit(0));
        add(closeBtn);

        // Responsive layout on resize
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int xClose = w - rightPad - btnW;
                int xMax = xClose - btnGap - btnW;
                int xMin = xMax - btnGap - btnW;
                minBtn.setBounds(xMin, y, btnW, btnH);
                maxBtn.setBounds(xMax, y, btnW, btnH);
                closeBtn.setBounds(xClose, y, btnW, btnH);
            }
        });
    }
} 