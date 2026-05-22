import javax.swing.*;
import java.awt.*;
// import java.awt.geom.RoundRectangle2D;
// import javax.swing.border.Border;
// import model.LogbookNumberGenerator;
import components.AppBar;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setUndecorated(true);
            frame.setSize(1200, 750);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(Color.WHITE);
            frame.setLayout(null);

            // Color palette
            Color primaryBlue = new Color(21, 101, 192); // #1565c0
            Color accentRed = new Color(211, 47, 47);    // #d32f2f
            Color white = Color.WHITE;
            Color lightBlue = new Color(227, 242, 253);  // light blue background
            Color borderGray = new Color(200, 200, 200);

            // Add AppBar (maroon bar with window controls) at the top
            AppBar appBar = new AppBar(frame);
            appBar.setBounds(0, 0, frame.getWidth(), 36);
            frame.getContentPane().add(appBar);
            frame.getContentPane().setComponentZOrder(appBar, 0); // Ensure AppBar is on top

            // Adjust headerPanel to be below the AppBar
            int maroonBarHeight = 36;
            int headerTopMargin = 18; // extra space between maroon bar and header
            JPanel headerPanel = new JPanel();
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBounds(0, maroonBarHeight + headerTopMargin, frame.getWidth(), 90);
            headerPanel.setLayout(null);
            frame.getContentPane().add(headerPanel);

            // Logo image (actual image instead of placeholder)
            ImageIcon logoIcon = new ImageIcon("assets/image/dict_logo.png");
            Image logoImg = logoIcon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(logoImg));
            logoLabel.setBounds(30, 20, 80, 80);
            headerPanel.add(logoLabel);

            // Title and subtitle
            JLabel title = new JLabel("Office Visitor  Management System");
            title.setFont(new Font("SansSerif", Font.BOLD, 26));
            title.setForeground(Color.BLACK);
            title.setBounds(120, 30, 600, 30);
            headerPanel.add(title);

            JLabel subtitle = new JLabel("Department of Information and Communications Technology");
            subtitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
            subtitle.setForeground(Color.BLACK);
            subtitle.setBounds(120, 58, 600, 25);
            headerPanel.add(subtitle);

            // Navigation menu (right-aligned, initial position)
            int navRightPad = 48;
            int navGap = 8;
            int navLabelW = 120; // Increased width for full label
            int navDividerW = 20;
            int navY = 30;
            int headerW = headerPanel.getWidth();
            int logbookX = headerW - navRightPad - 2*navLabelW - navDividerW - navGap;
            int dividerX = logbookX + navLabelW + navGap;
            int dashboardX = dividerX + navDividerW + navGap;
            JLabel logbookLabel = new JLabel("Log book");
            logbookLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            logbookLabel.setForeground(Color.BLACK);
            logbookLabel.setHorizontalAlignment(SwingConstants.LEFT);
            logbookLabel.setBounds(logbookX, navY, navLabelW, 30);
            headerPanel.add(logbookLabel);

            JLabel divider = new JLabel("|");
            divider.setFont(new Font("SansSerif", Font.BOLD, 18));
            divider.setForeground(Color.BLACK);
            divider.setBounds(dividerX, navY, navDividerW, 30);
            headerPanel.add(divider);

            JLabel logbookListLabel = new JLabel("Logbook List");
            logbookListLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            logbookListLabel.setForeground(Color.BLACK);
            logbookListLabel.setHorizontalAlignment(SwingConstants.LEFT);
            headerPanel.add(logbookListLabel);

            JLabel divider2 = new JLabel("|");
            divider2.setFont(new Font("SansSerif", Font.BOLD, 18));
            divider2.setForeground(Color.BLACK);
            headerPanel.add(divider2);

            JLabel dashboardLabel = new JLabel("Dashboard");
            dashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            dashboardLabel.setForeground(Color.BLACK);
            dashboardLabel.setHorizontalAlignment(SwingConstants.LEFT);
            dashboardLabel.setBounds(dashboardX, navY, navLabelW, 30);
            headerPanel.add(dashboardLabel);

            // --- Immersive Form Panel (rounded corners, drop shadow) ---
            LogbookFormPanel formPanel = new LogbookFormPanel();
            formPanel.setOpaque(false);
            // Wrap formPanel in a transparent FlowLayout panel to prevent stretching
            JPanel logbookWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            logbookWrapper.setOpaque(false);
            logbookWrapper.add(formPanel);
            // Immersive gradient background for the frame
            JPanel gradientPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setPaint(new java.awt.GradientPaint(0, 0, Color.WHITE, 0, getHeight(), new Color(245, 245, 255)));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            };
            gradientPanel.setLayout(null);
            frame.setContentPane(gradientPanel);
            frame.getContentPane().setLayout(null);

            // Create mainContentPanel with CardLayout for switchable content
            CardLayout cardLayout = new CardLayout();
            JPanel mainContentPanel = new JPanel(cardLayout);
            mainContentPanel.setOpaque(false);
            int contentY = headerPanel.getY() + headerPanel.getHeight() + Math.max(72, frame.getHeight()/10);
            int contentH = 480; // initial height
            mainContentPanel.setBounds(0, contentY, frame.getWidth(), contentH);

            // Create live clock for bottom right
            JLabel clockLabel = new JLabel();
            clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            clockLabel.setForeground(new Color(44, 62, 80));
            clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            clockLabel.setBounds(frame.getWidth() - 200, frame.getHeight() - 50, 180, 30);
            
            // Timer to update clock every second
            Timer clockTimer = new Timer(1000, e -> {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                String timeStr = now.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
                String dateStr = now.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"));
                clockLabel.setText(timeStr + " | " + dateStr);
            });
            clockTimer.start();

            // Add all components to the content pane
            frame.getContentPane().add(headerPanel);
            frame.getContentPane().add(mainContentPanel);
            frame.getContentPane().add(clockLabel);
            frame.getContentPane().add(appBar); // Add AppBar last
            appBar.setBounds(0, 0, frame.getWidth(), 36);
            frame.getContentPane().setComponentZOrder(appBar, 0); // Ensure AppBar is on top

            // Add logbookWrapper, loginPanel, and verificationPanel to mainContentPanel
            mainContentPanel.add(logbookWrapper, "formPanel");
            
            // Create logbook list panel
            LogbookListPanel logbookListPanel = new LogbookListPanel();
            mainContentPanel.add(logbookListPanel, "logbookListPanel");
            
            // Create login panel
            LoginPanel loginPanel = new LoginPanel();
            mainContentPanel.add(loginPanel, "loginPanel");
            
            // Create verification panel
            VerificationPanel verificationPanel = new VerificationPanel();
            mainContentPanel.add(verificationPanel, "verificationPanel");

            // Set correct gap and bounds for mainContentPanel, formPanel, and loginPanel[0] on startup
            int w = frame.getWidth();
            int h = frame.getHeight();
            int panelHeight = 480; // or your preferred height
            int panelY = headerPanel.getY() + headerPanel.getHeight() + Math.max(72, h/10);
            mainContentPanel.setBounds(0, panelY, w, panelHeight);
            formPanel.setBounds(0, 0, w, panelHeight);
            loginPanel.setBounds(0, 0, w, panelHeight);
            verificationPanel.setBounds(0, 0, w, panelHeight);
            logbookListPanel.setBounds(0, 0, w, panelHeight);
            cardLayout.show(mainContentPanel, "formPanel");

            // Set initial card and nav active state
            cardLayout.show(mainContentPanel, "formPanel");
            logbookLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            logbookListLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
            dashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

            // Helper method to center and size formPanel and loginPanel
            Runnable updatePanelBounds = () -> {
                int winW = frame.getWidth();
                int winH = frame.getHeight();
                // Responsive form panel width: max 900px or 60% of window, min 350px
                int updPanelWidth = Math.max(350, Math.min(900, (int)(winW * 0.6)));
                int updPanelHeight = formPanel.getHeight();
                int updPanelX = (winW - updPanelWidth) / 2;
                int updPanelY = headerPanel.getY() + headerPanel.getHeight() + Math.max(72, winH/10);
                formPanel.setBounds(updPanelX, 0, updPanelWidth, updPanelHeight); // y=0 inside mainContentPanel
                loginPanel.setBounds(updPanelX, 0, updPanelWidth, updPanelHeight); // y=0 inside mainContentPanel
                verificationPanel.setBounds(updPanelX, 0, updPanelWidth, updPanelHeight); // y=0 inside mainContentPanel
                logbookListPanel.setBounds(updPanelX, 0, updPanelWidth, updPanelHeight);
                mainContentPanel.setBounds(0, updPanelY, winW, updPanelHeight);
            };

            // Add action listeners for login flow
            loginPanel.setOnLoginSuccess(() -> {
                cardLayout.show(mainContentPanel, "verificationPanel");
                updatePanelBounds.run();
            });
            
            verificationPanel.backBtn.addActionListener(e -> {
                // Go back to login panel
                cardLayout.show(mainContentPanel, "loginPanel");
                updatePanelBounds.run();
            });
            
            // Remove direct submitBtn action listener that opens the dashboard
            // verificationPanel.submitBtn.addActionListener(e -> {
            //     DashboardWindow dashboardWindow = new DashboardWindow();
            //     dashboardWindow.setVisible(true);
            //     frame.dispose();
            // });

            // Instead, set the onVerificationSuccess callback:
            verificationPanel.setOnVerificationSuccess(() -> {
                DashboardWindow dashboardWindow = new DashboardWindow();
                dashboardWindow.setVisible(true);
                frame.dispose();
            });

            // Responsive layout: update panels and AppBar on resize
            frame.addComponentListener(new java.awt.event.ComponentAdapter() {
                public void componentResized(java.awt.event.ComponentEvent e) {
                    int w = frame.getWidth();
                    int h = frame.getHeight();
                    appBar.setBounds(0, 0, w, 36); // Ensure AppBar resizes with frame
                    headerPanel.setBounds(0, maroonBarHeight + headerTopMargin, w, headerPanel.getHeight());
                    // Update clock position
                    clockLabel.setBounds(w - 200, h - 50, 180, 30);
                    // Update nav menu positions
                    int navRightPad = 48;
                    int navGap = 8;
                    int navLabelW = 120;
                    int navDividerW = 20;
                    int navY = 30;
                    int headerW = headerPanel.getWidth();
                    int logbookX = headerW - navRightPad - 3*navLabelW - 2*navDividerW - 2*navGap;
                    int divider1X = logbookX + navLabelW + navGap;
                    int logbookListX = divider1X + navDividerW + navGap;
                    int divider2X = logbookListX + navLabelW + navGap;
                    int dashboardX = divider2X + navDividerW + navGap;
                    logbookLabel.setBounds(logbookX, navY, navLabelW, 30);
                    divider.setBounds(divider1X, navY, navDividerW, 30);
                    logbookListLabel.setBounds(logbookListX, navY, navLabelW, 30);
                    divider2.setBounds(divider2X, navY, navDividerW, 30);
                    dashboardLabel.setBounds(dashboardX, navY, navLabelW, 30);
                    mainContentPanel.revalidate();
                    mainContentPanel.repaint();
                    headerPanel.revalidate();
                    headerPanel.repaint();
                    frame.revalidate();
                    frame.repaint();
                    updatePanelBounds.run();
                }
            });

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);

            // Force layout update for logbook form on startup
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
            logbookWrapper.revalidate();
            logbookWrapper.repaint();
            formPanel.revalidate();
            formPanel.repaint();

            // Add mouse listeners for navigation
            logbookLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logbookListLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dashboardLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            logbookLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    cardLayout.show(mainContentPanel, "formPanel");
                    logbookLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    logbookListLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    dashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    updatePanelBounds.run();
                }
            });
            logbookListLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    cardLayout.show(mainContentPanel, "logbookListPanel");
                    logbookLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    logbookListLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    dashboardLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    updatePanelBounds.run();
                }
            });
            dashboardLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    cardLayout.show(mainContentPanel, "loginPanel");
                    logbookLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    logbookListLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
                    dashboardLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
                    updatePanelBounds.run();
                }
            });
        });
    }
} 