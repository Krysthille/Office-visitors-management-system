package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TabPanel extends JPanel {
    private FancyTab[] tabs;
    private JPanel content;
    private JPanel logbookListPanel;
    private JPanel manageReportPanel;
    private JPanel generateIDPanel;

    public TabPanel(JPanel content, JPanel logbookListPanel, JPanel manageReportPanel, JPanel generateIDPanel) {
        this.content = content;
        this.logbookListPanel = logbookListPanel;
        this.manageReportPanel = manageReportPanel;
        this.generateIDPanel = generateIDPanel;
        
        setLayout(null);
        setBackground(Color.WHITE); // Clean white background
        setBounds(0, 136, 800, 44); // Smaller height for minimal tabs
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));

        initializeTabs();
    }

    private void initializeTabs() {
        FancyTab visitors = new FancyTab("Visitors Log", new ImageIcon("assets/icons/log.png"));
        FancyTab reports = new FancyTab("Reports", new ImageIcon("assets/icons/report.png"));
        FancyTab generateID = new FancyTab("Generate ID", new ImageIcon("assets/icons/id.png"));
        FancyTab loginSessions = new FancyTab("Login Sessions", new ImageIcon("assets/icons/session.png"));

        visitors.setBounds(16, 2, 160, 40);
        reports.setBounds(184, 2, 160, 40);
        generateID.setBounds(352, 2, 160, 40);
        loginSessions.setBounds(520, 2, 160, 40);

        // Create an array to manage tab selection
        tabs = new FancyTab[]{visitors, reports, generateID, loginSessions};
        
        // Set up tab selection callbacks
        visitors.setOnTabSelected(() -> {
            setActiveTab(visitors);
            // Show LogbookListPanel when Visitors Log is active
            content.removeAll();
            content.add(logbookListPanel, BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
        
        reports.setOnTabSelected(() -> {
            setActiveTab(reports);
            // Show ManageReportPanel when Reports tab is active
            content.removeAll();
            content.add(manageReportPanel, BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
        
        generateID.setOnTabSelected(() -> {
            setActiveTab(generateID);
            // Show GenerateIDPanel when Generate ID tab is active
            content.removeAll();
            content.add(generateIDPanel, BorderLayout.CENTER);
            content.revalidate();
            content.repaint();
        });
        loginSessions.setOnTabSelected(() -> setActiveTab(loginSessions));
        
        // Set initial selection
        setActiveTab(visitors);
        // Show LogbookListPanel by default
        content.removeAll();
        content.add(logbookListPanel, BorderLayout.CENTER);
        content.revalidate();
        content.repaint();

        add(visitors);
        add(reports);
        add(generateID);
        add(loginSessions);
    }

    private void setActiveTab(FancyTab activeTab) {
        for (FancyTab tab : tabs) {
            tab.setSelected(tab == activeTab);
        }
    }

    public void updateBounds(int width) {
        setBounds(0, 136, width, 44);
    }
} 