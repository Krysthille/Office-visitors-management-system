// ReportPanel.java
// Requires: jfreechart-1.5.4.jar, itextpdf-5.5.13.3.jar (or OpenPDF)
// Place these jars in your lib/ directory and add to classpath.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;

// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

// iText imports
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import com.itextpdf.text.Image;
import service.VisitorService;
import model.Visitor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.Timer;

public class ReportPanel extends JPanel {
    private DefaultCategoryDataset dataset;
    private JFreeChart chart;
    private ChartPanel chartPanel;
    private JButton generateReportBtn;
    private JTable dataTable;
    private JScrollPane tableScrollPane;
    private VisitorService visitorService;
    private List<Visitor> allVisitors;
    private JButton viewGraphBtn;
    private JButton manageReportBtn;
    private CardLayout cardLayout;
    private JPanel centerPanel;
    private JTable provinceTable;
    private JScrollPane provinceTableScrollPane;
    private JPanel managePanel;

    public ReportPanel() {
        visitorService = new VisitorService();
        allVisitors = visitorService.getAllVisitors();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // --- LEFT: Action buttons ---
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(32, 12, 32, 12));
        viewGraphBtn = new JButton("View Graph");
        manageReportBtn = new JButton("Manage Report");
        generateReportBtn = new JButton("Generate Report (PDF)");
        Dimension btnSize = new Dimension(160, 40);
        for (JButton btn : new JButton[]{viewGraphBtn, manageReportBtn, generateReportBtn}) {
            btn.setMaximumSize(btnSize);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
            btn.setFocusPainted(false);
            leftPanel.add(btn);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 18)));
        }
        add(leftPanel, BorderLayout.WEST);

        // --- RIGHT: Main content ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        // Week label at the top
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate endOfThisWeek = startOfThisWeek.plusDays(6);
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
        JLabel weekRangeLabel = new JLabel("Week: " + startOfThisWeek.format(dateFmt) + " – " + endOfThisWeek.format(dateFmt), SwingConstants.CENTER);
        weekRangeLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        weekRangeLabel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));
        rightPanel.add(weekRangeLabel, BorderLayout.NORTH);

        // Center: CardLayout for switching views
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        centerPanel.setOpaque(false);
        rightPanel.add(centerPanel, BorderLayout.CENTER);

        // --- Modern Table with Logos Header ---
        // Custom TableModel: first row is for logos image, rest are province data
        String[] provColumns = {"Province", "No. Total Visitors", "Male", "Female", "Total (M+F)", "Diff"};
        Object[][] provData = createProvinceSummaryData();
        DefaultTableModel model = new DefaultTableModel(provData, provColumns) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        provinceTable = new JTable(model) {
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 248, 255));
                } else {
                    c.setBackground(new Color(220, 235, 255));
                }
                c.setFont(new Font("SansSerif", Font.PLAIN, 15));
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
                }
                return c;
            }
        };
        // Table styling
        provinceTable.setShowGrid(false);
        provinceTable.setRowHeight(32);
        provinceTable.setIntercellSpacing(new Dimension(0, 0));
        provinceTable.setFillsViewportHeight(true);
        // Header styling
        JTableHeader header = provinceTable.getTableHeader();
        header.setBackground(new Color(30, 60, 120));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.setReorderingAllowed(false);
        // Rounded corners and shadow for table
        provinceTableScrollPane = new JScrollPane(provinceTable) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                g2.setColor(new Color(200, 210, 230, 80));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 24, 24);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        provinceTableScrollPane.setOpaque(false);
        provinceTableScrollPane.getViewport().setOpaque(false);
        provinceTableScrollPane.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        provinceTableScrollPane.setPreferredSize(new Dimension(420, 120));
        // Set preferred column widths for compactness
        int[] colWidths = {90, 60, 50, 50, 70, 40};
        TableColumnModel colModel = provinceTable.getColumnModel();
        for (int i = 0; i < colWidths.length; i++) {
            colModel.getColumn(i).setPreferredWidth(colWidths[i]);
            colModel.getColumn(i).setMaxWidth(colWidths[i] + 20);
        }
        // Center the table horizontally
        JPanel tableWrapper = new JPanel();
        tableWrapper.setLayout(new BoxLayout(tableWrapper, BoxLayout.X_AXIS));
        tableWrapper.setOpaque(false);
        tableWrapper.add(Box.createHorizontalGlue());
        tableWrapper.add(provinceTableScrollPane);
        tableWrapper.add(Box.createHorizontalGlue());
        centerPanel.add(tableWrapper, "table");

        // Graph view
        dataset = createActualDataset();
        chart = ChartFactory.createBarChart(
            "Total Visitors (This Week)",
            "Day",
            "Visitors",
            dataset
        );
        chartPanel = new ChartPanel(chart);
        centerPanel.add(chartPanel, "graph");

        // Manage Report placeholder
        managePanel = new JPanel(new BorderLayout());
        managePanel.add(new JLabel("Manage Report (coming soon)", SwingConstants.CENTER), BorderLayout.CENTER);
        centerPanel.add(managePanel, "manage");

        // Show province table by default
        cardLayout.show(centerPanel, "table");

        // --- Animation for switching views ---
        // Wrap centerPanel in a fade panel
        final float[] alpha = {1.0f};
        JPanel fadePanel = new JPanel(new CardLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha[0]));
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        fadePanel.setOpaque(false);
        fadePanel.add(centerPanel, "main");
        rightPanel.add(fadePanel, BorderLayout.CENTER);
        // Animation method
        Runnable animateSwitch = () -> {
            Timer timer = new Timer(15, null);
            timer.addActionListener(new ActionListener() {
                boolean fadingOut = true;
                public void actionPerformed(ActionEvent e) {
                    if (fadingOut) {
                        alpha[0] -= 0.1f;
                        if (alpha[0] <= 0.1f) {
                            alpha[0] = 0.1f;
                            fadingOut = false;
                            cardLayout.next(centerPanel);
                        }
                    } else {
                        alpha[0] += 0.1f;
                        if (alpha[0] >= 1.0f) {
                            alpha[0] = 1.0f;
                            timer.stop();
                        }
                    }
                    fadePanel.repaint();
                }
            });
            timer.start();
        };
        // Button actions with animation
        viewGraphBtn.addActionListener(e -> {
            cardLayout.show(centerPanel, "graph");
            animateSwitch.run();
        });
        manageReportBtn.addActionListener(e -> {
            cardLayout.show(centerPanel, "manage");
            animateSwitch.run();
        });
        generateReportBtn.addActionListener(e -> exportReportToPDF());

        // Add right panel to main layout
        add(rightPanel, BorderLayout.CENTER);
    }

    // --- Sample Data (Replace with real DB fetch) ---
    private DefaultCategoryDataset createSampleDataset() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int[] thisWeek = {12, 15, 18, 10, 20, 25, 22};
        int[] prevWeek = {8, 10, 12, 9, 14, 18, 16};
        for (int i = 0; i < days.length; i++) {
            ds.addValue(thisWeek[i], "This Week", days[i]);
            ds.addValue(prevWeek[i], "Prev Week", days[i]);
        }
        return ds;
    }
    private Object[][] createSampleTableData() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int[] thisWeek = {12, 15, 18, 10, 20, 25, 22};
        int[] prevWeek = {8, 10, 12, 9, 14, 18, 16};
        Object[][] data = new Object[7][3];
        for (int i = 0; i < 7; i++) {
            data[i][0] = days[i];
            data[i][1] = thisWeek[i];
            data[i][2] = prevWeek[i];
        }
        return data;
    }

    // --- Actual Data for Chart and Table ---
    private DefaultCategoryDataset createActualDataset() {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfThisWeek.plusDays(i);
            int count = countVisitorsOnDate(day);
            String dayLabel = day.format(dayFmt);
            ds.addValue(count, "Visitors", dayLabel + "\n" + day.format(dateFmt));
        }
        return ds;
    }
    private Object[][] createCurrentWeekTableData() {
        LocalDate today = LocalDate.now();
        LocalDate startOfThisWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
        DateTimeFormatter dayFmt = DateTimeFormatter.ofPattern("EEE");
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Object[][] data = new Object[7][3];
        for (int i = 0; i < 7; i++) {
            LocalDate day = startOfThisWeek.plusDays(i);
            int count = countVisitorsOnDate(day);
            data[i][0] = day.format(dayFmt);
            data[i][1] = day.format(dateFmt);
            data[i][2] = count;
        }
        return data;
    }
    private int countVisitorsOnDate(LocalDate date) {
        return (int) allVisitors.stream().filter(v -> v.getTimestamp().toLocalDate().equals(date)).count();
    }

    // --- Province Summary Data ---
    private Object[][] createProvinceSummaryData() {
        // Group by province, count total, male, female
        // (Assumes allVisitors is already loaded)
        java.util.Map<String, int[]> map = new java.util.HashMap<>();
        for (Visitor v : allVisitors) {
            String prov = v.getProvince() == null ? "" : v.getProvince();
            int[] arr = map.getOrDefault(prov, new int[3]);
            arr[0]++;
            if ("Male".equalsIgnoreCase(v.getGender())) arr[1]++;
            if ("Female".equalsIgnoreCase(v.getGender())) arr[2]++;
            map.put(prov, arr);
        }
        Object[][] data = new Object[map.size()][6];
        int i = 0;
        for (String prov : map.keySet()) {
            int[] arr = map.get(prov);
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
        return data;
    }

    // --- PDF Export ---
    private void exportReportToPDF() {
        try {
            Document document = new Document();
            String fileName = "Visitor_Report_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();
            document.add(new Paragraph("Visitor Report"));
            document.add(new Paragraph(" "));
            // Add week range
            LocalDate today = LocalDate.now();
            LocalDate startOfThisWeek = today.minusDays(today.getDayOfWeek().getValue() - 1);
            LocalDate endOfThisWeek = startOfThisWeek.plusDays(6);
            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMM d, yyyy");
            document.add(new Paragraph("Week: " + startOfThisWeek.format(dateFmt) + " – " + endOfThisWeek.format(dateFmt)));
            document.add(new Paragraph(" "));

            // Export current view
            if (centerPanel.getComponentZOrder(provinceTableScrollPane) == 0) {
                // Province summary table
                PdfPTable pdfTable = new PdfPTable(provinceTable.getColumnCount());
                for (int i = 0; i < provinceTable.getColumnCount(); i++) {
                    pdfTable.addCell(provinceTable.getColumnName(i));
                }
                for (int row = 0; row < provinceTable.getRowCount(); row++) {
                    for (int col = 0; col < provinceTable.getColumnCount(); col++) {
                        pdfTable.addCell(provinceTable.getValueAt(row, col).toString());
                    }
                }
                document.add(pdfTable);
            } else if (centerPanel.getComponentZOrder(chartPanel) == 0) {
                // Graph view
                BufferedImage chartImage = chart.createBufferedImage(600, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(chartImage, "png", baos);
                Image chartImg = Image.getInstance(baos.toByteArray());
                chartImg.scaleToFit(500, 250);
                document.add(chartImg);
                document.add(new Paragraph(" "));
                // Add summary table
                PdfPTable pdfTable = new PdfPTable(dataTable.getColumnCount());
                for (int i = 0; i < dataTable.getColumnCount(); i++) {
                    pdfTable.addCell(dataTable.getColumnName(i));
                }
                for (int row = 0; row < dataTable.getRowCount(); row++) {
                    for (int col = 0; col < dataTable.getColumnCount(); col++) {
                        pdfTable.addCell(dataTable.getValueAt(row, col).toString());
                    }
                }
                document.add(pdfTable);
            } else {
                document.add(new Paragraph("Manage Report (coming soon)"));
            }

            // Always add full visitors log
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Full Visitors Log"));
            PdfPTable logTable = new PdfPTable(6);
            String[] logHeaders = {"Logbook #", "Full Name", "Municipality", "Phone", "Purpose", "Timestamp"};
            for (String h : logHeaders) logTable.addCell(h);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Visitor v : allVisitors) {
                logTable.addCell(v.getLogbookNumber());
                logTable.addCell(v.getFirstName() + " " + v.getLastName());
                logTable.addCell(v.getMunicipality());
                logTable.addCell(v.getPhoneNumber() == null ? "" : v.getPhoneNumber());
                logTable.addCell(v.getPurpose() == null ? "" : v.getPurpose());
                logTable.addCell(v.getTimestamp() != null ? v.getTimestamp().format(dtf) : "");
            }
            document.add(logTable);
            document.close();
            writer.close();
            JOptionPane.showMessageDialog(this, "PDF report exported: " + fileName, "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to export PDF: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 