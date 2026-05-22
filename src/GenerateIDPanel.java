import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import service.VisitorService;
import model.Visitor;
import java.util.List;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.imageio.ImageIO;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;

public class GenerateIDPanel extends JPanel {
    private JTextField searchField;
    private JButton generateButton;
    private JTextArea resultArea;
    private JLabel statusLabel;
    private JTable visitorTable;
    private DefaultTableModel tableModel;
    private VisitorService visitorService;
    private JPanel tableContainer;
    private JPanel resultPanel;

    public GenerateIDPanel() {
        visitorService = new VisitorService();
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
    }

    private void initializeComponents() {
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Remove top/bottom padding

        JLabel titleLabel = new JLabel("Generate ID");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 33, 33));
        headerPanel.add(titleLabel);

        // Search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20)); // Remove top/bottom padding

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchLabel.setForeground(new Color(80, 80, 80));

        searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        generateButton = new JButton("Search");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateButton.setBackground(new Color(25, 118, 210));
        generateButton.setForeground(Color.WHITE);
        generateButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(generateButton);

        // Result panel
        resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());
        resultPanel.setBackground(Color.WHITE);
        resultPanel.setBorder(BorderFactory.createEmptyBorder(5, 20, 20, 20));

        JLabel resultLabel = new JLabel("Generated ID:");
        resultLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultLabel.setForeground(new Color(80, 80, 80));

        resultArea = new JTextArea();
        resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Result"),
            BorderFactory.createEmptyBorder(30, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));

        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Table Container (left side) ---
        tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        String[] columns = {"Timestamp", "Logbook #", "First Name", "Middle Name", "Last Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        visitorTable = new JTable(tableModel);
        visitorTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        visitorTable.setRowHeight(24);
        visitorTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        visitorTable.getTableHeader().setBackground(new Color(44, 62, 80));
        visitorTable.getTableHeader().setForeground(Color.WHITE);
        visitorTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(visitorTable);
        int searchPanelWidth = searchPanel.getPreferredSize().width;
        tableScroll.setPreferredSize(new Dimension(searchPanelWidth, 180));
        tableContainer.setPreferredSize(new Dimension(searchPanelWidth, 180));
        tableContainer.add(tableScroll, BorderLayout.CENTER);

        // --- Result Panel (right side) ---
        JPanel resultPanelWrapper = new JPanel(new BorderLayout());
        resultPanelWrapper.setBackground(Color.WHITE);
        resultPanelWrapper.setBorder(BorderFactory.createEmptyBorder(0, 24, 0, 0)); // small gap
        resultPanelWrapper.add(resultPanel, BorderLayout.CENTER);
        // resultPanel.setPreferredSize(new Dimension(400, 220)); // Removed as per edit hint

        // --- Add components using GridBagLayout ---
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        add(headerPanel, gbc);

        gbc.gridy++;
        add(searchPanel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 20, 0, 0); // align with search panel left
        add(tableContainer, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 24, 0, 0); // small gap between table and result
        add(resultPanelWrapper, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(Box.createVerticalGlue(), gbc);

        loadVisitorTableData();
        setupEventListeners();
    }

    private void setupEventListeners() {
        // Search field enter key
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    filterVisitorTable();
                }
            }
        });

        // Generate button click
        generateButton.addActionListener(e -> filterVisitorTable());

        // Search field focus
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Enter search term...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(33, 33, 33));
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Enter search term...");
                    searchField.setForeground(new Color(150, 150, 150));
                }
            }
        });

        // Set initial placeholder text
        searchField.setText("Enter search term...");
        searchField.setForeground(new Color(150, 150, 150));

        // Table double-click to generate ID
        visitorTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && visitorTable.getSelectedRow() != -1) {
                    int modelRow = visitorTable.convertRowIndexToModel(visitorTable.getSelectedRow());
                    String logbookNumber = (String) tableModel.getValueAt(modelRow, 1); // Logbook #
                    Visitor v = visitorService.getVisitorByLogbookNumber(logbookNumber);
                    if (v != null) {
                        showIDCard(v);
                    }
                }
            }
        });
    }

    private void filterVisitorTable() {
        String searchTerm = searchField.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        List<Visitor> visitors = visitorService.getAllVisitors();
        if (searchTerm.isEmpty() || searchTerm.equals("enter search term...")) {
            for (Visitor v : visitors) {
                addVisitorRow(v);
            }
        } else {
            for (Visitor v : visitors) {
                if ((v.getLogbookNumber() != null && v.getLogbookNumber().toLowerCase().contains(searchTerm)) ||
                    (v.getFirstName() != null && v.getFirstName().toLowerCase().contains(searchTerm)) ||
                    (v.getMiddleName() != null && v.getMiddleName().toLowerCase().contains(searchTerm)) ||
                    (v.getLastName() != null && v.getLastName().toLowerCase().contains(searchTerm))) {
                    addVisitorRow(v);
                }
            }
        }
    }

    private void addVisitorRow(Visitor v) {
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        tableModel.addRow(new Object[] {
            v.getTimestamp() != null ? v.getTimestamp().format(dtf) : "",
            v.getLogbookNumber(),
            v.getFirstName(),
            v.getMiddleName(),
            v.getLastName()
        });
    }

    private void generateID() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty() || searchTerm.equals("Enter search term...")) {
            statusLabel.setText("Please enter a search term");
            statusLabel.setForeground(new Color(220, 53, 69));
            return;
        }

        // Simulate ID generation
        statusLabel.setText("Generating ID...");
        statusLabel.setForeground(new Color(25, 118, 210));

        // Simulate processing delay
        Timer timer = new Timer(1000, e -> {
            String generatedID = generateUniqueID(searchTerm);
            resultArea.setText("Generated ID for: " + searchTerm + "\n\n" +
                            "ID: " + generatedID + "\n\n" +
                            "Generated on: " + java.time.LocalDateTime.now().format(
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            ));
            
            statusLabel.setText("ID generated successfully");
            statusLabel.setForeground(new Color(40, 167, 69));
        });
        timer.setRepeats(false);
        timer.start();
    }

    private String generateUniqueID(String searchTerm) {
        // Simple ID generation logic
        String timestamp = String.valueOf(System.currentTimeMillis());
        String hash = String.valueOf(searchTerm.hashCode());
        return "ID-" + timestamp.substring(timestamp.length() - 6) + "-" + 
               hash.substring(Math.max(0, hash.length() - 4)).toUpperCase();
    }

    private void loadVisitorTableData() {
        tableModel.setRowCount(0);
        List<Visitor> visitors = visitorService.getAllVisitors();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Visitor v : visitors) {
            tableModel.addRow(new Object[] {
                v.getTimestamp() != null ? v.getTimestamp().format(dtf) : "",
                v.getLogbookNumber(),
                v.getFirstName(),
                v.getMiddleName(),
                v.getLastName()
            });
        }
    }

    private void showIDCard(Visitor v) {
        // Debug print to terminal
        System.out.println("[DEBUG] Generating ID card for visitor: " + v.getLogbookNumber() + " - " + v.getFirstName() + " " + v.getLastName());
        // Show debug message in result panel
        resultPanel.removeAll();
        resultPanel.setLayout(new BorderLayout());
        JLabel debugLabel = new JLabel("Generating ID card...", SwingConstants.CENTER);
        debugLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        debugLabel.setForeground(new Color(25, 118, 210));
        resultPanel.add(debugLabel, BorderLayout.CENTER);
        resultPanel.revalidate();
        resultPanel.repaint();
        // Simulate a short delay before rendering the card (for UI feedback)
        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            JPanel idCardPanel = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // White background
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    // No border here; border will be on the container
                    // Logo
                    try {
                        Image logo = ImageIO.read(new File("assets/image/dict_logo.png"));
                        g2.drawImage(logo, 10, 8, 32, 32, this);
                    } catch (Exception ex) {}
                    // Department text
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                    g2.drawString("Department of Information and Communications Technology", 50, 20);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 7));
                    g2.drawString("Biliran Province", 50, 32);
                    // Photo box (photo is loaded from the file path in the visitors_logbook table)
                    int photoX = 10, photoY = 50, photoW = 80, photoH = 80;
                    g2.setColor(new Color(220,220,220));
                    g2.fillRect(photoX, photoY, photoW, photoH);
                    g2.setColor(Color.GRAY);
                    g2.drawRect(photoX, photoY, photoW, photoH);
                    if (v.getPhotoPath() != null && !v.getPhotoPath().isEmpty()) {
                        try {
                            System.out.println("[DEBUG] Loading photo from: " + v.getPhotoPath());
                            System.out.println("[DEBUG] Photo path (quoted): '" + v.getPhotoPath() + "'");
                            System.out.println("[DEBUG] Photo path length: " + v.getPhotoPath().length());
                            System.out.println("[DEBUG] Current working directory: " + System.getProperty("user.dir"));
                            File photoFile = new File(System.getProperty("user.dir"), v.getPhotoPath());
                            System.out.println("[DEBUG] Photo file exists: " + photoFile.exists());
                            Image img = ImageIO.read(photoFile);
                            if (img != null) {
                                g2.drawImage(img, photoX, photoY, photoW, photoH, this);
                            }
                        } catch (Exception ex) {}
                    } else {
                        g2.setColor(Color.WHITE);
                        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
                        g2.drawString("Photo", photoX + 20, photoY + 45);
                    }
                    // Name and contact
                    int textX = photoX + photoW + 16;
                    int textY = photoY + 16;
                    g2.setColor(Color.BLACK);
                    g2.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g2.drawString(v.getLastName() != null ? v.getLastName() : "", textX, textY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 14));
                    String firstName = v.getFirstName() != null ? v.getFirstName() : "";
                    String middleInitial = (v.getMiddleName() != null && !v.getMiddleName().isEmpty()) ? (v.getMiddleName().substring(0,1) + ".") : "";
                    g2.drawString(firstName + (middleInitial.isEmpty() ? "" : "  " + middleInitial), textX, textY + 18);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 10));
                    g2.drawString(v.getPhoneNumber() != null ? v.getPhoneNumber() : "", textX, textY + 33);
                    // Signature
                    int signY = photoY + photoH + 20;
                    int signX = photoX;
                    int signW = 60;
                    g2.setColor(Color.BLACK);
                    g2.drawLine(signX, signY, signX + signW, signY);
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 7));
                    g2.drawString("Signature", signX, signY + 9);
                    // Signature image
                    if (v.getSignaturePath() != null && !v.getSignaturePath().isEmpty()) {
                        try {
                            Image signImg = ImageIO.read(new File(v.getSignaturePath()));
                            if (signImg != null) {
                                g2.drawImage(signImg, signX, signY - 16, 40, 16, this);
                            }
                        } catch (Exception ex) {}
                    }
                    // Barangay, Municipality, Province - aligned horizontally with signature
                    int colW = 90;
                    int barX = signX + signW + 30;
                    int munX = barX + colW + 10;
                    int provX = munX + colW + 10;
                    int labelY = signY + 9;
                    int valueY = signY - 4;
                    g2.setColor(Color.BLACK);
                    // Draw lines for each field
                    g2.drawLine(barX, signY, barX + colW, signY);
                    g2.drawLine(munX, signY, munX + colW, signY);
                    g2.drawLine(provX, signY, provX + colW, signY);
                    // Draw labels
                    g2.setFont(new Font("SansSerif", Font.PLAIN, 8));
                    g2.drawString("Barangay", barX + 20, labelY);
                    g2.drawString("Municipality", munX + 20, labelY);
                    g2.drawString("Province", provX + 20, labelY);
                    // Draw values
                    g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                    g2.drawString(v.getBarangay() != null ? v.getBarangay() : "", barX + 5, valueY);
                    g2.drawString(v.getMunicipality() != null ? v.getMunicipality() : "", munX + 5, valueY);
                    g2.drawString(v.getProvince() != null ? v.getProvince() : "", provX + 5, valueY);
                }
            };
            idCardPanel.setPreferredSize(new Dimension(400, 200));
            idCardPanel.setMinimumSize(new Dimension(400, 200));
            idCardPanel.setMaximumSize(new Dimension(400, 200));
            // Create a container for the ID card with a border that fits tightly
            JPanel idCardContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            idCardContainer.setBackground(Color.WHITE);
            idCardContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            idCardContainer.add(idCardPanel);
            // Place the container in the result panel, keeping the label/title
            resultPanel.removeAll();
            resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
            resultPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            JLabel resultLabel = new JLabel("Generated ID:");
            resultLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            resultLabel.setForeground(new Color(80, 80, 80));
            resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(resultLabel);
            // Wrap the idCardContainer in a panel to center it and prevent overflow
            JPanel idCardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            idCardWrapper.setBackground(Color.WHITE);
            idCardWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
            idCardWrapper.add(idCardContainer);
            resultPanel.add(idCardWrapper);
            // Add Save button below the ID card, outside the border, left-aligned
            JButton saveButton = new JButton("Save");
            saveButton.setFont(new Font("SansSerif", Font.BOLD, 13));
            saveButton.setBackground(new Color(25, 118, 210));
            saveButton.setForeground(Color.WHITE);
            saveButton.setFocusPainted(false);
            saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            JPanel buttonPanel = new JPanel();
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.add(saveButton);
            buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            resultPanel.add(buttonPanel);
            // Save button action: show modal for image/pdf choice
            saveButton.addActionListener(e2 -> {
                Object[] options = {"Image", "PDF", "Cancel"};
                int choice = JOptionPane.showOptionDialog(resultPanel,
                        "How do you want to save the ID?",
                        "Save ID",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (choice == JOptionPane.YES_OPTION) {
                    // Save as image
                    String defaultFileName = (v.getLastName() != null ? v.getLastName() : "ID") + "_" + (v.getFirstName() != null ? v.getFirstName() : "");
                    defaultFileName = defaultFileName.replaceAll("\\s+", "_");
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save ID as Image");
                    fileChooser.setSelectedFile(new java.io.File(defaultFileName + ".png"));
                    int userSelection = fileChooser.showSaveDialog(resultPanel);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        java.io.File fileToSave = fileChooser.getSelectedFile();
                        try {
                            // Find the ID card panel to save
                            java.awt.Component idCardComp = idCardContainer.getComponent(0);
                            int w = idCardComp.getWidth();
                            int h = idCardComp.getHeight();
                            int scale = 2; // 2x for high quality
                            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                                w * scale, h * scale, java.awt.image.BufferedImage.TYPE_INT_ARGB);
                            java.awt.Graphics2D g2d = img.createGraphics();
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            g2d.scale(scale, scale);
                            idCardComp.paint(g2d);
                            g2d.dispose();
                            javax.imageio.ImageIO.write(img, "png", fileToSave);
                            JOptionPane.showMessageDialog(resultPanel, "ID saved as image: " + fileToSave.getAbsolutePath());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(resultPanel, "Failed to save image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (choice == JOptionPane.NO_OPTION) {
                    // Save as PDF
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Save ID as PDF");
                    String defaultFileName = (v.getLastName() != null ? v.getLastName() : "ID") + "_" + (v.getFirstName() != null ? v.getFirstName() : "");
                    defaultFileName = defaultFileName.replaceAll("\\s+", "_");
                    fileChooser.setSelectedFile(new java.io.File(defaultFileName + ".pdf"));
                    int userSelection = fileChooser.showSaveDialog(resultPanel);
                    if (userSelection == JFileChooser.APPROVE_OPTION) {
                        java.io.File fileToSave = fileChooser.getSelectedFile();
                        try {
                            // Find the ID card panel to save
                            java.awt.Component idCardComp = idCardContainer.getComponent(0);
                            int w = idCardComp.getWidth();
                            int h = idCardComp.getHeight();
                            int scale = 2; // 2x for high quality
                            java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                                w * scale, h * scale, java.awt.image.BufferedImage.TYPE_INT_RGB);
                            java.awt.Graphics2D g2d = img.createGraphics();
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
                            g2d.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                            g2d.scale(scale, scale);
                            idCardComp.paint(g2d);
                            g2d.dispose();
                            // Write to PDF using iText
                            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(fileToSave));
                            document.open();
                            com.itextpdf.text.Image pdfImg = com.itextpdf.text.Image.getInstance(img, null);
                            float pdfW = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
                            float scaleW = pdfW * 0.6f; // 60% of available width
                            float scaleH = pdfImg.getHeight() * (scaleW / pdfImg.getWidth());
                            pdfImg.scaleAbsolute(scaleW, scaleH);
                            document.add(pdfImg);
                            document.close();
                            JOptionPane.showMessageDialog(resultPanel, "ID saved as PDF: " + fileToSave.getAbsolutePath());
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(resultPanel, "Failed to save PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });
            resultPanel.revalidate();
            resultPanel.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }
} 