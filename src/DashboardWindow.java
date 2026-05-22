import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import components.AppBar;
import components.HeaderPanel;
import components.TabPanel;
import components.LogoutButton;

public class DashboardWindow extends JFrame {

    private DashboardWindow frame = this;
    private JPanel content;
    private LogbookListPanel logbookListPanel;
    private ManageReportPanel manageReportPanel;
    private GenerateIDPanel generateIDPanel;

    public DashboardWindow() {
        setTitle("Office Visitor Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setLayout(null);

        // Initialize content and logbookListPanel first
        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setBounds(0, 36 + 100 + 44, getWidth(), getHeight() - 180);
        content.setLayout(new BorderLayout());
        logbookListPanel = new LogbookListPanel();
        manageReportPanel = new ManageReportPanel();
        generateIDPanel = new GenerateIDPanel();
        content.add(new JLabel("Welcome to the system", SwingConstants.CENTER), BorderLayout.CENTER);

        // Add AppBar (maroon bar with window controls) at the top
        AppBar appBar = new AppBar(frame);
        appBar.setBounds(0, 0, frame.getWidth(), 36);
        frame.getContentPane().add(appBar);
        frame.getContentPane().setComponentZOrder(appBar, 0);

        // Create components
        HeaderPanel header = new HeaderPanel();
        TabPanel nav = new TabPanel(content, logbookListPanel, manageReportPanel, generateIDPanel);
        LogoutButton logoutBtn = new LogoutButton(this);

        getContentPane().add(header);
        getContentPane().add(nav);
        nav.add(logoutBtn);
        logoutBtn.updatePosition(getWidth());
        getContentPane().add(content);
        getContentPane().add(appBar);
        appBar.setBounds(0, 0, frame.getWidth(), 36);
        frame.getContentPane().setComponentZOrder(appBar, 0);

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();
                appBar.setBounds(0, 0, w, 36);
                header.updateBounds(w);
                nav.updateBounds(w);
                content.setBounds(0, 180, w, h - 180);
                logoutBtn.updatePosition(w);
                appBar.revalidate();
                appBar.repaint();
                frame.revalidate();
                frame.repaint();
            }
        });
    }





    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DashboardWindow().setVisible(true));
    }


}
