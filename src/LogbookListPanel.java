import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import service.VisitorService;
import model.Visitor;
import java.time.LocalDateTime;
import java.time.Duration;
import javax.swing.Timer;
import javax.swing.RowSorter;
import javax.swing.SortOrder;

public class LogbookListPanel extends JPanel {
    private JTable visitorTable;
    private DefaultTableModel tableModel;
    private JButton refreshBtn;
    private JButton viewDetailsBtn;
    private JButton deleteBtn;
    private VisitorService visitorService;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private LocalDateTime lastUpdated;
    private Timer timer;
    private int lastKnownRowCount = -1;
    private Timer autoRefreshTimer;
    private TableRowSorter<DefaultTableModel> sorter;

    public LogbookListPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(new Color(245, 247, 250));
        setBorder(null);
        visitorService = new VisitorService();

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 32, 8, 32));

        // Title and subtitle (left)
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titleLabel = new JLabel("VISITOR LOGBOOK LIST");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));
        subtitleLabel = new JLabel("View and manage all visitor entries");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Buttons (right)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 0));
        buttonPanel.setOpaque(false);
        refreshBtn = createButton("🔄 Refresh", new Color(41, 128, 185), new Color(21, 101, 192));
        viewDetailsBtn = createButton("👁️ View Details", new Color(39, 174, 96), new Color(29, 140, 72));
        viewDetailsBtn.setEnabled(false);
        deleteBtn = createButton("🗑️ Delete", new Color(231, 76, 60), new Color(192, 57, 43));
        deleteBtn.setEnabled(false);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(deleteBtn);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- TABLE ---
        createTable();
        JScrollPane scrollPane = new JScrollPane(visitorTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 32, 32, 32));
        scrollPane.getViewport().setBackground(new Color(250, 250, 252));
        add(scrollPane, BorderLayout.CENTER);

        addActionListeners();
        loadVisitorData();
        lastUpdated = LocalDateTime.now();
        startLiveTimer();
        startAutoRefresh();
    }

    private JButton createButton(String text, Color bg, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 24, 8, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setOpaque(true);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    private void createTable() {
        String[] columnNames = {
            "#", "Logbook #", "Full Name", "Municipality", "Phone Number", 
            "Purpose", "Timestamp", "Signature", "Photo"
        };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        visitorTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? new Color(245, 247, 250) : Color.WHITE);
                } else {
                    c.setBackground(new Color(220, 230, 250));
                }
                return c;
            }
        };
        visitorTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        visitorTable.setRowHeight(28);
        visitorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        visitorTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 15));
        visitorTable.getTableHeader().setBackground(new Color(44, 62, 80));
        visitorTable.getTableHeader().setForeground(Color.WHITE);
        visitorTable.getTableHeader().setReorderingAllowed(false);
        visitorTable.setGridColor(new Color(220, 220, 220));
        visitorTable.setShowGrid(true);
        visitorTable.setIntercellSpacing(new Dimension(0, 0));
        visitorTable.setFillsViewportHeight(true);
        visitorTable.setBorder(BorderFactory.createEmptyBorder());
        visitorTable.setOpaque(false);
        visitorTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Center header text
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) visitorTable.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Center cell text by default
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Left-align for Full Name column (index 2)
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        // Custom renderer for numbering column (index 0)
        visitorTable.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setText(String.valueOf(row + 1));
                return c;
            }
        });
        // Left-align for Full Name column (index 2)
        for (int i = 0; i < visitorTable.getColumnCount(); i++) {
            if (i == 2) {
                visitorTable.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            } else if (i != 0) { // skip numbering column, already set
                visitorTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }
        // Enable sorting
        sorter = new TableRowSorter<>(tableModel);
        // Custom comparator for Timestamp column (index 6)
        sorter.setComparator(6, (o1, o2) -> {
            try {
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
                java.time.LocalDateTime d1 = o1 == null || o1.equals("N/A") ? java.time.LocalDateTime.MIN : java.time.LocalDateTime.parse(o1.toString(), formatter);
                java.time.LocalDateTime d2 = o2 == null || o2.equals("N/A") ? java.time.LocalDateTime.MIN : java.time.LocalDateTime.parse(o2.toString(), formatter);
                return d1.compareTo(d2);
            } catch (Exception e) {
                return 0;
            }
        });
        visitorTable.setRowSorter(sorter);
        // Selection listener
        visitorTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = visitorTable.getSelectedRow() != -1;
            viewDetailsBtn.setEnabled(hasSelection);
            deleteBtn.setEnabled(hasSelection);
        });
    }

    private void addActionListeners() {
        refreshBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadVisitorData();
            }
        });
        viewDetailsBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewVisitorDetails();
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteVisitor();
            }
        });
    }

    private void loadVisitorData() {
        tableModel.setRowCount(0);
        try {
            List<Visitor> visitors = visitorService.getAllVisitors();
            lastKnownRowCount = visitors.size();
            if (visitors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No visitor records found.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
            for (int i = 0; i < visitors.size(); i++) {
                Visitor visitor = visitors.get(i);
                Object[] row = {
                    i + 1, // numbering
                    visitor.getLogbookNumber(),
                    visitor.getFullName(),
                    visitor.getMunicipality(),
                    visitor.getPhoneNumber(),
                    visitor.getPurpose(),
                    visitor.getTimestamp() != null ? visitor.getTimestamp().format(formatter) : "N/A",
                    visitor.getSignaturePath() != null ? "✓" : "✗",
                    visitor.getPhotoPath() != null ? "✓" : "✗"
                };
                tableModel.addRow(row);
            }
            lastUpdated = LocalDateTime.now();
            updateSubtitleLive();
            // Set default sort to ascending by Timestamp (index 6)
            if (sorter != null) {
                java.util.List<RowSorter.SortKey> sortKeys = new java.util.ArrayList<>();
                sortKeys.add(new RowSorter.SortKey(6, SortOrder.ASCENDING));
                sorter.setSortKeys(sortKeys);
                sorter.sort();
            }
        } catch (Exception e) {
            System.err.println("Error loading visitor data: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading visitor data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startLiveTimer() {
        timer = new Timer(1000, e -> updateSubtitleLive());
        timer.start();
    }

    private void updateSubtitleLive() {
        if (lastUpdated == null) return;
        subtitleLabel.setText("Last updated: " + lastUpdated.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")));
    }

    private void startAutoRefresh() {
        autoRefreshTimer = new Timer(3000, e -> {
            try {
                int currentCount = visitorService.getAllVisitors().size();
                if (currentCount != lastKnownRowCount) {
                    loadVisitorData();
                    lastKnownRowCount = currentCount;
                }
            } catch (Exception ex) {
                // Optionally log or handle error
            }
        });
        autoRefreshTimer.start();
    }

    private void viewVisitorDetails() {
        int selectedRow = visitorTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        // Convert to model index in case of sorting
        int modelRow = visitorTable.convertRowIndexToModel(selectedRow);
        String logbookNumber = (String) tableModel.getValueAt(modelRow, 1); // Logbook # is at index 1
        
        try {
            Visitor visitor = visitorService.getVisitorByLogbookNumber(logbookNumber);
            if (visitor != null) {
                showVisitorDetailsDialog(visitor);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Visitor not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Error getting visitor details: " + e.getMessage());
            JOptionPane.showMessageDialog(this, 
                "Error getting visitor details: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showVisitorDetailsDialog(Visitor visitor) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Visitor Details - " + visitor.getLogbookNumber(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Add visitor details
        addDetailRow(detailsPanel, gbc, "Logbook Number:", visitor.getLogbookNumber());
        addDetailRow(detailsPanel, gbc, "Full Name:", visitor.getFullName());
        addDetailRow(detailsPanel, gbc, "Address:", visitor.getAddress());
        addDetailRow(detailsPanel, gbc, "Age:", visitor.getAge() != null ? visitor.getAge().toString() : "N/A");
        addDetailRow(detailsPanel, gbc, "Gender:", visitor.getGender());
        addDetailRow(detailsPanel, gbc, "Phone Number:", visitor.getPhoneNumber());
        addDetailRow(detailsPanel, gbc, "Email:", visitor.getEmail() != null ? visitor.getEmail() : "N/A");
        addDetailRow(detailsPanel, gbc, "Sector:", visitor.getSector() != null ? visitor.getSector() : "N/A");
        addDetailRow(detailsPanel, gbc, "Purpose:", visitor.getPurpose());
        addDetailRow(detailsPanel, gbc, "Timestamp:", visitor.getTimestamp() != null ? 
            visitor.getTimestamp().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")) : "N/A");
        addDetailRow(detailsPanel, gbc, "Signature:", visitor.getSignaturePath() != null ? "Available" : "Not available");
        addDetailRow(detailsPanel, gbc, "Photo:", visitor.getPhotoPath() != null ? "Available" : "Not available");
        
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(closeBtn);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value != null ? value : "N/A");
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
    }
    
    private void deleteVisitor() {
        int selectedRow = visitorTable.getSelectedRow();
        if (selectedRow == -1) return;
        
        int modelRow = visitorTable.convertRowIndexToModel(selectedRow);
        String logbookNumber = (String) tableModel.getValueAt(modelRow, 1); // Logbook # is at index 1
        String visitorName = (String) tableModel.getValueAt(modelRow, 2); // Full Name is at index 2
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete visitor:\n" +
            "Logbook #: " + logbookNumber + "\n" +
            "Name: " + visitorName + "\n\n" +
            "This action cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                Visitor visitor = visitorService.getVisitorByLogbookNumber(logbookNumber);
                if (visitor != null) {
                    boolean success = visitorService.deleteVisitor(visitor.getId());
                    if (success) {
                        JOptionPane.showMessageDialog(this,
                            "Visitor deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadVisitorData(); // Refresh the table
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete visitor.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error deleting visitor: " + e.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error deleting visitor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
} 