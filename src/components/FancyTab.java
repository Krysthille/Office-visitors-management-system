package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FancyTab extends JPanel {
    private final String label;
    private final Icon icon;
    private boolean selected = false;
    private float progress = 0f;
    private final Timer animator;
    private Runnable onTabSelected; // Callback for tab selection

    public FancyTab(String label, Icon icon) {
        this.label = label;
        this.icon = icon;
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(160, 40)); // Smaller, more minimal size

        animator = new Timer(8, e -> { // Very fast animation
            float target = selected ? 1f : 0f;
            if (progress < target) {
                progress = Math.min(progress + 0.12f, target);
                repaint();
            } else if (progress > target) {
                progress = Math.max(progress - 0.12f, target);
                repaint();
            } else {
                ((Timer) e.getSource()).stop();
            }
        });

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                animator.start();
            }

            public void mouseExited(MouseEvent e) {
                if (!selected) animator.start();
            }

            public void mouseClicked(MouseEvent e) {
                if (onTabSelected != null) {
                    onTabSelected.run();
                }
            }
        });
    }

    public void setOnTabSelected(Runnable callback) {
        this.onTabSelected = callback;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        animator.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        
        // Add bottom box shadow for all tabs
        g2.setColor(new Color(0, 0, 0, 15));
        g2.fillRect(2, height - 1, width - 4, 2);
        
        // Selected state with enhanced styling
        if (selected) {
            // Bottom border for selected state
            g2.setColor(new Color(25, 118, 210));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(0, height - 3, width, height - 3);
            
            // Add subtle background tint
            g2.setColor(new Color(25, 118, 210, 10));
            g2.fillRect(1, 1, width - 2, height - 4);
        } else if (progress > 0) {
            // Hover effect with subtle background
            g2.setColor(new Color(25, 118, 210, (int) (20 * progress)));
            g2.fillRect(1, 1, width - 2, height - 2);
        }

        // Icon with minimal positioning
        int iconSize = 16;
        int iconX = 12;
        int iconY = (height - iconSize) / 2;
        
        if (icon != null) {
            // Scale icon for minimal appearance
            Image img = ((ImageIcon) icon).getImage();
            Image scaledImg = img.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
            g2.drawImage(scaledImg, iconX, iconY, null);
        }

        // Text with minimal typography - centered
        g2.setColor(selected ? new Color(25, 118, 210) : new Color(80, 80, 80));
        g2.setFont(new Font("Segoe UI", selected ? Font.BOLD : Font.PLAIN, 12));
        
        FontMetrics fm = g2.getFontMetrics();
        
        // Center the text horizontally in the tab
        int textWidth = fm.stringWidth(label);
        int textX = (width - textWidth) / 2;
        int textY = (height + fm.getAscent()) / 2 - 1;
        
        g2.drawString(label, textX, textY);
    }
} 