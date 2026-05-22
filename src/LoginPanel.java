import javax.swing.*;
import java.awt.*;
import java.sql.*;
import config.DatabaseConn;

public class LoginPanel extends JPanel {
    public JTextField adminIdField;
    public JPasswordField passwordField;
    public JButton loginBtn;
    public Runnable onLoginSuccess;
    
    public LoginPanel() {
        setOpaque(false);
        setLayout(null);

        int contentWidth = 550;
        int contentHeight = 520;
        JPanel loginContentPanel = new JPanel(null) {
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
        loginContentPanel.setOpaque(false);
        loginContentPanel.setBounds(0, 0, contentWidth, contentHeight);
        add(loginContentPanel);

        // Authentication icon panel
        JPanel iconPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw user authentication icon
                g2.setColor(new Color(111, 19, 19));
                g2.setStroke(new BasicStroke(3));
                
                // User head circle
                int centerX = getWidth()/2;
                int centerY = getHeight()/2;
                g2.drawOval(centerX-20, centerY-25, 40, 40);
                
                // User body
                g2.drawArc(centerX-25, centerY-15, 50, 50, 0, 180);
                
                // Key icon
                g2.setColor(new Color(111, 19, 19));
                g2.fillRect(centerX+15, centerY-10, 8, 20);
                g2.fillOval(centerX+17, centerY-15, 4, 4);
                g2.setColor(Color.WHITE);
                g2.fillRect(centerX+16, centerY-8, 6, 16);
            }
        };
        iconPanel.setOpaque(false);
        iconPanel.setBounds((contentWidth-80)/2, 20, 80, 60);
        loginContentPanel.add(iconPanel);

        // Title with authentication emphasis
        JLabel title = new JLabel("ADMINISTRATOR ACCESS", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(111, 19, 19));
        title.setBounds(0, 90, contentWidth, 32);
        loginContentPanel.add(title);

        // Subtitle with authentication context
        JLabel subtitle = new JLabel("Secure Login Required", SwingConstants.CENTER);
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        subtitle.setForeground(new Color(100, 100, 100));
        subtitle.setBounds(0, 122, contentWidth, 24);
        loginContentPanel.add(subtitle);

        // Authentication message
        JLabel authMsg = new JLabel("Please enter your administrator credentials to access the system", SwingConstants.CENTER);
        authMsg.setFont(new Font("SansSerif", Font.PLAIN, 14));
        authMsg.setForeground(new Color(150, 150, 150));
        authMsg.setBounds(0, 146, contentWidth, 20);
        loginContentPanel.add(authMsg);

        // Admin ID label with enhanced styling
        JLabel adminIdLabel = new JLabel("ADMINISTRATOR ID");
        adminIdLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        adminIdLabel.setForeground(new Color(44, 62, 80));
        adminIdLabel.setBounds(40, 190, 300, 22);
        loginContentPanel.add(adminIdLabel);

        // Enhanced admin ID input field
        adminIdField = new JTextField();
        adminIdField.setFont(new Font("SansSerif", Font.BOLD, 16));
        adminIdField.setBackground(new Color(250, 250, 250));
        adminIdField.setForeground(new Color(44, 62, 80));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(111, 19, 19), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        adminIdField.setBounds(40, 215, 470, 45);
        adminIdField.setHorizontalAlignment(JTextField.CENTER);
        loginContentPanel.add(adminIdField);

        // Password label with enhanced styling
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        passwordLabel.setForeground(new Color(44, 62, 80));
        passwordLabel.setBounds(40, 280, 300, 22);
        loginContentPanel.add(passwordLabel);

        // Enhanced password input field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.BOLD, 16));
        passwordField.setBackground(new Color(250, 250, 250));
        passwordField.setForeground(new Color(44, 62, 80));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(111, 19, 19), 2),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        passwordField.setBounds(40, 305, 470, 45);
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        loginContentPanel.add(passwordField);

        // Security notice panel
        JPanel noticePanel = new JPanel(new BorderLayout());
        noticePanel.setBackground(new Color(255, 248, 248));
        noticePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(111, 19, 19, 100), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        noticePanel.setBounds(40, 370, 470, 60);
        
        JLabel noticeLabel = new JLabel("Security Notice:");
        noticeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        noticeLabel.setForeground(new Color(111, 19, 19));
        noticePanel.add(noticeLabel, BorderLayout.NORTH);
        
        JLabel noticeText = new JLabel("• This system is monitored and all access is logged");
        noticeText.setFont(new Font("SansSerif", Font.PLAIN, 11));
        noticeText.setForeground(new Color(100, 100, 100));
        noticePanel.add(noticeText, BorderLayout.CENTER);
        
        loginContentPanel.add(noticePanel);

        // Enhanced login button
        loginBtn = new JButton("AUTHENTICATE & CONTINUE");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setBackground(new Color(111, 19, 19));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        loginBtn.setBounds((contentWidth - 280) / 2, 450, 280, 45);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginContentPanel.add(loginBtn);

        // Add hover effects
        addButtonHoverEffects(loginBtn, new Color(90, 15, 15), new Color(111, 19, 19));

        // Login button action: check credentials
        loginBtn.addActionListener(e -> {
            String username = adminIdField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConn.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM dump_login_credentials WHERE type='login' AND username=? AND password=? LIMIT 1")) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    if (onLoginSuccess != null) onLoginSuccess.run();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        });

        // Center loginContentPanel horizontally on resize
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getWidth();
                int x = (w - contentWidth) / 2;
                loginContentPanel.setLocation(x, 0);
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

    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
} 