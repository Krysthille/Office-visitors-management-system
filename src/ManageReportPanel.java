import javax.swing.*;
import java.awt.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
import service.VisitorService;
import model.Visitor;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class ManageReportPanel extends JPanel {
    // Only declare these fields once at the top of the class
    private JPanel sidebar;
    private JPanel mainContent;
    private JPanel cardPanel;
    private JPanel reportContainerPanel;
    private JPanel graphPanel;

    public ManageReportPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Sidebar (left)
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Soft vertical gradient
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(245, 248, 255), 0, getHeight(), new Color(225, 230, 245));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
            }
        };
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 248, 255, 0));
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 8, 12, 8)
        ));
        sidebar.add(Box.createVerticalStrut(40));

        // Add modern buttons with icons and animation
        String[] btnNames = {"Manage Report", "View Graph", "Generate Report"};
        String[] iconPaths = {"assets/icons/report.png", "assets/icons/graph.png", "assets/icons/pdf.png"};
        for (int i = 0; i < btnNames.length; i++) {
            String name = btnNames[i];
            String iconPath = iconPaths[i];
            JButton btn = new JButton(name);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(160, 44));
            btn.setFont(new Font("SansSerif", Font.BOLD, 15));
            btn.setBackground(new Color(30, 60, 120));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 60, 120), 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
            ));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            // Add icon if available
            try {
                ImageIcon icon = new ImageIcon(iconPath);
                Image img = icon.getImage().getScaledInstance(22, 22, Image.SCALE_SMOOTH);
                btn.setIcon(new ImageIcon(img));
                btn.setHorizontalAlignment(SwingConstants.LEFT);
                btn.setIconTextGap(12);
            } catch (Exception e) { /* ignore if icon not found */ }
            // Animation: smooth color transition on hover
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                javax.swing.Timer timer;
                Color start = new Color(30, 60, 120);
                Color end = new Color(50, 90, 180);
                float[] curr = {0f};
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    if (timer != null && timer.isRunning()) timer.stop();
                    timer = new javax.swing.Timer(15, null);
                    timer.addActionListener(ev -> {
                        curr[0] += 0.1f;
                        if (curr[0] > 1f) curr[0] = 1f;
                        btn.setBackground(blend(start, end, curr[0]));
                        if (curr[0] >= 1f) timer.stop();
                    });
                    timer.start();
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    if (timer != null && timer.isRunning()) timer.stop();
                    timer = new javax.swing.Timer(15, null);
                    timer.addActionListener(ev -> {
                        curr[0] -= 0.1f;
                        if (curr[0] < 0f) curr[0] = 0f;
                        btn.setBackground(blend(start, end, curr[0]));
                        if (curr[0] <= 0f) timer.stop();
                    });
                    timer.start();
                }
            });
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(18));
        }
        sidebar.add(Box.createVerticalGlue());

        // Main content (right)
        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(Color.WHITE);
        // Card panel for switching views
        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(Color.WHITE);

        // Main report container (logo + table)
        reportContainerPanel = new JPanel(new GridBagLayout());
        reportContainerPanel.setBackground(Color.WHITE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));
        container.setOpaque(true);

        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK),
            BorderFactory.createEmptyBorder(0,0,8,0)
        ));
        try {
            ImageIcon logosIcon = new ImageIcon("assets/image/logos.png");
            int maxWidth = 800;
            JLabel logosLabel;
            if (logosIcon.getIconWidth() > maxWidth) {
                double scale = (double) maxWidth / logosIcon.getIconWidth();
                int newW = maxWidth;
                int newH = (int) (logosIcon.getIconHeight() * scale);
                java.awt.Image scaledImg = logosIcon.getImage().getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH);
                logosLabel = new JLabel(new ImageIcon(scaledImg));
            } else {
                logosLabel = new JLabel(logosIcon);
            }
            logosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(logosLabel);
        } catch (Exception e) {
            JLabel logosLabel = new JLabel("[Logos]", SwingConstants.CENTER);
            logosLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoPanel.add(logosLabel);
        }
        container.add(logoPanel);

        // Table panel
        JPanel tablePanel = new JPanel();
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setLayout(new BoxLayout(tablePanel, BoxLayout.Y_AXIS));
        tablePanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        String[] columns = {"Province", "<html>Total no. of<br>Visitors</html>", "Male", "Female", "Total", "Diff"};
        // Aggregate actual data
        VisitorService visitorService = new VisitorService();
        List<Visitor> allVisitors = visitorService.getAllVisitors();
        Map<String, int[]> provinceMap = new LinkedHashMap<>();
        for (Visitor v : allVisitors) {
            String prov = v.getProvince() == null ? "" : v.getProvince();
            int[] arr = provinceMap.getOrDefault(prov, new int[3]);
            arr[0]++;
            if ("Male".equalsIgnoreCase(v.getGender())) arr[1]++;
            if ("Female".equalsIgnoreCase(v.getGender())) arr[2]++;
            provinceMap.put(prov, arr);
        }
        Object[][] data = new Object[provinceMap.size()][6];
        int i = 0;
        for (Map.Entry<String, int[]> entry : provinceMap.entrySet()) {
            String prov = entry.getKey();
            int[] arr = entry.getValue();
            int total = arr[1] + arr[2];
            int diff = arr[0] - total;
            data[i][0] = prov;
            data[i][1] = arr[0];
            data[i][2] = arr[1];
            data[i][3] = arr[2];
            data[i][4] = total;
            data[i][5] = diff;
            i++;
        }
        JTable table = new JTable(data, columns) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                } else {
                    c.setBackground(new Color(220, 235, 255));
                }
                c.setFont(new Font("SansSerif", Font.PLAIN, 17)); // larger font
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                }
                return c;
            }
        };
        table.setShowGrid(false);
        table.setRowHeight(28);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(30, 60, 120));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        // Set custom renderer for multi-line header on the 'Total No. of Visitors' column
        table.getColumnModel().getColumn(1).setHeaderRenderer(new MultiLineHeaderRenderer());
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 56));
        // Set preferred/minimum column widths to fit header text
        int[] colWidths = {90, 110, 60, 60, 170, 180};
        for (int j = 0; j < colWidths.length; j++) {
            table.getColumnModel().getColumn(j).setPreferredWidth(colWidths[j]);
            table.getColumnModel().getColumn(j).setMinWidth(colWidths[j]);
        }
        // Center all columns except Province
        javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        for (int j = 1; j < table.getColumnCount(); j++) {
            table.getColumnModel().getColumn(j).setCellRenderer(centerRenderer);
        }
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(750, 120));
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        tablePanel.add(tableScroll);
        container.add(tablePanel);

        // Center the container at the top of reportContainerPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.PAGE_START;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        reportContainerPanel.add(container, gbc);

        // Graph panel (minimal vertical bar chart: Visitors per Week)
        graphPanel = new JPanel(new BorderLayout());
        graphPanel.setBackground(Color.WHITE);
        // Button panel for graph type selection
        JPanel graphButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton totalBtn = new JButton("Total Visitors");
        JButton maleBtn = new JButton("Male");
        JButton femaleBtn = new JButton("Female");
        Font btnFont = new Font("SansSerif", Font.BOLD, 13);
        for (JButton btn : new JButton[]{totalBtn, maleBtn, femaleBtn}) {
            btn.setFont(btnFont);
            btn.setBackground(new Color(30, 60, 120));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            graphButtonPanel.add(btn);
        }
        graphPanel.add(graphButtonPanel, BorderLayout.NORTH);

        // Prepare data for current week
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.DayOfWeek firstDayOfWeek = java.time.DayOfWeek.MONDAY;
        java.time.LocalDate startOfWeek = today.with(firstDayOfWeek);
        String[] weekdays = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int[] weekTotal = new int[7];
        int[] weekMale = new int[7];
        int[] weekFemale = new int[7];
        for (Visitor v : allVisitors) {
            java.time.LocalDate date = v.getTimestamp().toLocalDate();
            int dayIdx = (int) java.time.temporal.ChronoUnit.DAYS.between(startOfWeek, date);
            if (dayIdx >= 0 && dayIdx < 7) {
                weekTotal[dayIdx]++;
                if ("Male".equalsIgnoreCase(v.getGender())) weekMale[dayIdx]++;
                if ("Female".equalsIgnoreCase(v.getGender())) weekFemale[dayIdx]++;
            }
        }

        DefaultCategoryDataset weekDataset = new DefaultCategoryDataset();
        for (int w = 0; w < 7; w++) {
            weekDataset.addValue(weekTotal[w], "Visitors", weekdays[w]);
        }
        JFreeChart weekBarChart = ChartFactory.createBarChart(
            "Visitors per Week", // chart title
            "Weekday",           // x-axis label
            "No. of Visitors",   // y-axis label
            weekDataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            false, false, false
        );
        // Minimal design: remove extra decorations
        weekBarChart.setBackgroundPaint(Color.WHITE);
        weekBarChart.getPlot().setBackgroundPaint(Color.WHITE);
        weekBarChart.getPlot().setOutlineVisible(false);
        org.jfree.chart.plot.CategoryPlot plot = (org.jfree.chart.plot.CategoryPlot) weekBarChart.getPlot();
        plot.getDomainAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        plot.getRangeAxis().setTickLabelFont(new Font("SansSerif", Font.PLAIN, 14));
        plot.getDomainAxis().setAxisLineVisible(false);
        plot.getRangeAxis().setAxisLineVisible(false);
        plot.setRangeGridlinesVisible(false);
        plot.setDomainGridlinesVisible(false);
        ChartPanel weekChartPanel = new ChartPanel(weekBarChart);
        weekChartPanel.setPreferredSize(new Dimension(400, 220));
        graphPanel.add(weekChartPanel, BorderLayout.CENTER);

        JButton ageBtn = new JButton("Age");
        graphButtonPanel.add(ageBtn);
        // Age ranges
        String[] ageRanges = {"15-20", "21-30", "31-40", "41-50", "51-60", "60+"};
        int[][] weekAge = new int[7][ageRanges.length];
        for (Visitor v : allVisitors) {
            java.time.LocalDate date = v.getTimestamp().toLocalDate();
            int dayIdx = (int) java.time.temporal.ChronoUnit.DAYS.between(startOfWeek, date);
            if (dayIdx >= 0 && dayIdx < 7 && v.getAge() != null) {
                int age = v.getAge();
                int rangeIdx =
                    (age >= 15 && age <= 20) ? 0 :
                    (age >= 21 && age <= 30) ? 1 :
                    (age >= 31 && age <= 40) ? 2 :
                    (age >= 41 && age <= 50) ? 3 :
                    (age >= 51 && age <= 60) ? 4 :
                    (age > 60) ? 5 : -1;
                if (rangeIdx >= 0) weekAge[dayIdx][rangeIdx]++;
            }
        }

        // Button actions to switch graph type
        totalBtn.addActionListener(e -> {
            weekDataset.clear();
            for (int w = 0; w < 7; w++) weekDataset.addValue(weekTotal[w], "Visitors", weekdays[w]);
            weekBarChart.setTitle("Visitors per Week");
            plot.getRangeAxis().setLabel("No. of Visitors");
            plot.setDataset(weekDataset);
            plot.setRenderer(new org.jfree.chart.renderer.category.BarRenderer());
        });
        maleBtn.addActionListener(e -> {
            weekDataset.clear();
            for (int w = 0; w < 7; w++) weekDataset.addValue(weekMale[w], "Male", weekdays[w]);
            for (int w = 0; w < 7; w++) weekDataset.addValue(weekFemale[w], "Female", weekdays[w]);
            weekBarChart.setTitle("Gender per Week");
            plot.getRangeAxis().setLabel("No. of Visitors");
            org.jfree.chart.renderer.category.BarRenderer renderer = new org.jfree.chart.renderer.category.BarRenderer();
            renderer.setSeriesPaint(0, new Color(33, 150, 243)); // blue for male
            renderer.setSeriesPaint(1, new Color(244, 67, 54)); // red for female
            plot.setRenderer(renderer);
            plot.setDataset(weekDataset);
        });
        femaleBtn.setVisible(false); // Hide the old female button
        ageBtn.addActionListener(e -> {
            DefaultCategoryDataset ageDataset = new DefaultCategoryDataset();
            for (int range = 0; range < ageRanges.length; range++) {
                for (int w = 0; w < 7; w++) {
                    ageDataset.addValue(weekAge[w][range], ageRanges[range], weekdays[w]);
                }
            }
            weekBarChart.setTitle("Age per Week");
            plot.getRangeAxis().setLabel("No. of Visitors");
            plot.setDataset(ageDataset);
            plot.setRenderer(new org.jfree.chart.renderer.category.BarRenderer());
        });

        // Add both panels to cardPanel
        cardPanel.add(reportContainerPanel, "report");
        cardPanel.add(graphPanel, "graph");
        mainContent.add(cardPanel, BorderLayout.CENTER);

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);

        // Sidebar button actions
        // Find buttons by name
        for (Component comp : sidebar.getComponents()) {
            if (comp instanceof JButton btn) {
                switch (btn.getText()) {
                    case "View Graph" -> btn.addActionListener(e -> showCard("graph"));
                    case "Manage Report" -> btn.addActionListener(e -> showCard("report"));
                    // 'Generate Report' can be handled here if needed
                }
            }
        }
    }

    private void showCard(String name) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, name);
    }

    private static Color blend(Color c1, Color c2, float ratio) {
        float ir = 1.0f - ratio;
        int r = (int) (c1.getRed() * ir + c2.getRed() * ratio);
        int g = (int) (c1.getGreen() * ir + c2.getGreen() * ratio);
        int b = (int) (c1.getBlue() * ir + c2.getBlue() * ratio);
        return new Color(r, g, b);
    }

    // Custom header renderer for multi-line headers
    class MultiLineHeaderRenderer extends javax.swing.table.DefaultTableCellRenderer {
        public MultiLineHeaderRenderer() {
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setHorizontalAlignment(CENTER);
            label.setVerticalAlignment(CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 14));
            label.setForeground(Color.WHITE);
            label.setBackground(new Color(30, 60, 120));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
            return label;
        }
    }
}
