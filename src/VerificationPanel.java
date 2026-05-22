import javax.swing.*;
import java.awt.*;
import java.sql.*;
import config.DatabaseConn;

public class VerificationPanel extends JPanel {
    public JTextField codeField;
    public JButton submitBtn;
    public JButton backBtn;
    public Runnable onVerificationSuccess;
    
    public VerificationPanel() {
        setOpaque(false);
        setLayout(null);

        int contentWidth = 550;
        int contentHeight = 420;
        JPanel verificationContentPanel = new JPanel(null) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Enhanced shadow with multiple layers
                g2.setColor(new Color(0,0,0,20));
                g2.fillRoundRect(12, 12, getWidth()-24, getHeight()-24, 30, 30);
                g2.setColor(new Color(0,0,0,15));
                g2.fillRoundRect(8, 8, getWidth()-16, getHeight()-16, 28, 28);
                g2.setColor(new Color(0,0,0,10));
                g2.fillRoundRect(4, 4, getWidth()-8, getHeight()-8, 26, 26);
                
                // Main panel with gradient
                GradientPaint gradient = new GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(248, 250, 252));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 24, 24);
                
                // Security-themed border
                g2.setColor(new Color(111, 19, 19));
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth()-4, getHeight()-4, 24, 24);
                
                // Security pattern overlay
                g2.setColor(new Color(111, 19, 19, 5));
                for (int i = 0; i < getWidth(); i += 20) {
                    g2.drawLine(i, 0, i, getHeight());
                }
                for (int i = 0; i < getHeight(); i += 20) {
                    g2.drawLine(0, i, getWidth(), i);
                }
            }
        };
        verificationContentPanel.setOpaque(false);
        verificationContentPanel.setBounds(0, 0, contentWidth, contentHeight);
        add(verificationContentPanel);

        // Security icon panel
        JPanel iconPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw security shield icon
                g2.setColor(new Color(111, 19, 19));
                g2.setStroke(new BasicStroke(3));
                
                // Shield shape
                int[] xPoints = {getWidth()/2, getWidth()/2-25, getWidth()/2-20, getWidth()/2-15, getWidth()/2-10, getWidth()/2+10, getWidth()/2+15, getWidth()/2+20, getWidth()/2+25};
                int[] yPoints = {getHeight()-10, getHeight()-25, getHeight()-35, getHeight()-45, getHeight()-55, getHeight()-55, getHeight()-45, getHeight()-35, getHeight()-25};
                g2.fillPolygon(xPoints, yPoints, 9);
                
                // Lock icon inside shield
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(2));
                int lockX = getWidth()/2 - 8;
                int lockY = getHeight() - 35;
                g2.drawRect(lockX, lockY, 16, 12);
                g2.drawArc(lockX-2, lockY-6, 20, 12, 0, 180);
                g2.fillRect(lockX+6, lockY+2, 4, 8);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setBounds((contentWidth-80)/2, 20, 80, 60);
        verificationContentPanel.add(iconPanel);

        // Title with security emphasis
        JLabel title = new JLabel("SECURITY VERIFICATION", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(111, 19, 19));
        title.setBounds(0, 90, contentWidth, 32);
        verificationContentPanel.add(title);

        // Subtitle with authentication context
        JLabel subtitle = new JLabel("Two-Factor Authentication Required", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setBounds(0, 122, contentWidth, 24);
        verificationContentPanel.add(subtitle);

        // Security message
        JLabel securityMsg = new JLabel("Please enter the authorized security code sent to your device", SwingConstants.CENTER);
        securityMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        securityMsg.setForeground(new Color(150, 150, 150));
        securityMsg.setBounds(0, 146, contentWidth, 20);
        verificationContentPanel.add(securityMsg);

        // Security Code label with enhanced styling
        JLabel codeLabel = new JLabel("AUTHORIZED SECURITY CODE");
        codeLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        codeLabel.setForeground(new Color(44, 62, 80));
        codeLabel.setBounds(40, 190, 300, 22);
        verificationContentPanel.add(codeLabel);

        // Enhanced code input field
        codeField = new JTextField();
        codeField.setFont(new Font("SansSerif", Font.BOLD, 18));
        codeField.setBackground(new Color(250, 250, 250));
        codeField.setForeground(new Color(44, 62, 80));
        codeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(111, 19, 19), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        codeField.setBounds(40, 215, 470, 45);
        codeField.setHorizontalAlignment(JTextField.CENTER);
        verificationContentPanel.add(codeField);

        // Security tips panel
        JPanel tipsPanel = new JPanel(new BorderLayout());
        tipsPanel.setBackground(new Color(255, 248, 248));
        tipsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(111, 19, 19, 100), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        tipsPanel.setBounds(40, 270, 470, 60);
        
        JLabel tipsLabel = new JLabel("Security Tips:");
        tipsLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        tipsLabel.setForeground(new Color(111, 19, 19));
        tipsPanel.add(tipsLabel, BorderLayout.NORTH);
        
        JLabel tipsText = new JLabel("• Never share your security code with anyone");
        tipsText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tipsText.setForeground(new Color(100, 100, 100));
        tipsPanel.add(tipsText, BorderLayout.CENTER);
        
        verificationContentPanel.add(tipsPanel);

        // Enhanced buttons with better styling
        backBtn = new JButton("Back to Login");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setBackground(new Color(100, 100, 100));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        backBtn.setBounds(40, 350, 140, 45);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verificationContentPanel.add(backBtn);

        submitBtn = new JButton("VERIFY & ACCESS");
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitBtn.setBackground(new Color(111, 19, 19));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        submitBtn.setBounds(370, 350, 140, 45);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        verificationContentPanel.add(submitBtn);

        // Add hover effects
        addButtonHoverEffects(backBtn, new Color(80, 80, 80), new Color(100, 100, 100));
        addButtonHoverEffects(submitBtn, new Color(90, 15, 15), new Color(111, 19, 19));

        // Verification button action: check code
        submitBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter the verification code.", "Missing Field", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM dump_login_credentials WHERE type='verification' AND code=? LIMIT 1")) {
                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Verification successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onVerificationSuccess != null) onVerificationSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid verification code. Please try again.", "Verification Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        // Center verificationContentPanel horizontally on resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int x = (w - contentWidth) / 2;
                verificationContentPanel.setLocation(x, 0);
            }
        });
    }
    
    private void addButtonHoverEffects(JButton button, Color hoverColor, Color normalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
            }
        });
    }

    public void setOnVerificationSuccess(Runnable callback) {
        this.onVerificationSuccess = callback;
    }
} 