import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;
import service.VisitorService;
import model.Visitor;

public class LogbookFormPanel extends JPanel {
    // Public fields for access if needed
    public JTextField[] leftFields;
    public JTextField[] rightFields;
    public JComboBox<String> genderComboBox;
    public JComboBox<String> municipalityComboBox;
    public JButton photoBtn;
    public JButton submitBtn;
    public JLabel noLabel;
    public JLabel tsLabel;
    
    // Image display labels
    private JLabel signatureImageLabel;
    private JLabel photoImageLabel;
    private String currentSignaturePath;
    private String currentPhotoPath;

    public LogbookFormPanel() {
        setOpaque(true);
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250)); // Subtle background
        setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));

        // Title/Header
        JLabel titleLabel = new JLabel("VISITOR LOGBOOK", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(44, 62, 80));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(22, 0, 18, 0));

        // Main content panel with border and background
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(44, 62, 80), 2),
            BorderFactory.createEmptyBorder(24, 32, 24, 32)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 16, 10, 16);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Top: No. and Timestamp
        String generatedNo;
        try {
            VisitorService visitorService = new VisitorService();
            generatedNo = visitorService.generateLogbookNumber();
        } catch (Exception e) {
            generatedNo = "ERROR";
        }
        noLabel = new JLabel("No. " + generatedNo);
        noLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        noLabel.setForeground(new Color(52, 73, 94));
        tsLabel = new JLabel("Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")));
        tsLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        tsLabel.setForeground(new Color(52, 73, 94));
        JPanel topPanel = new JPanel(new GridLayout(1,2));
        topPanel.setOpaque(false);
        topPanel.add(noLabel);
        topPanel.add(tsLabel);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4; gbc.weightx = 1;
        contentPanel.add(topPanel, gbc);

        // Section header (only one)
        gbc.gridy++;
        gbc.gridwidth = 4;
        JLabel personalHeader = new JLabel("PERSONAL INFORMATION");
        personalHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        personalHeader.setForeground(new Color(41, 128, 185));
        personalHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(41, 128, 185)));
        contentPanel.add(personalHeader, gbc);

        // Field style
        Color fieldBg = new Color(245, 247, 250);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 15);
        int fieldH = 32;
        Border fieldBorder = BorderFactory.createLineBorder(new Color(180, 180, 180), 1);

        // Left column fields (excluding Municipality which will be handled separately)
        String[] leftLabels = {"LAST NAME", "FIRST NAME", "MIDDLE NAME", "PROVINCE", "BARANGAY"};
        leftFields = new JTextField[leftLabels.length];
        int leftRow = 2;
        
        // Handle first 3 fields normally
        for (int i = 0; i < 3; i++) {
            gbc.gridx = 0; gbc.gridy = leftRow; gbc.gridwidth = 1;
            gbc.weightx = 0;
            JLabel l = new JLabel(leftLabels[i]);
            l.setFont(fieldFont.deriveFont(Font.BOLD));
            l.setForeground(new Color(44, 62, 80));
            contentPanel.add(l, gbc);
            gbc.gridx = 1;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JTextField tf = new JTextField();
            tf.setFont(fieldFont);
            tf.setBackground(fieldBg);
            tf.setBorder(fieldBorder);
            tf.setMinimumSize(new Dimension(200, fieldH));
            tf.setPreferredSize(new Dimension(400, fieldH));
            contentPanel.add(tf, gbc);
            leftFields[i] = tf;
            leftRow++;
        }
        
        // Handle Province field
        gbc.gridx = 0; gbc.gridy = leftRow; gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel provinceLabel = new JLabel(leftLabels[3]);
        provinceLabel.setFont(fieldFont.deriveFont(Font.BOLD));
        provinceLabel.setForeground(new Color(44, 62, 80));
        contentPanel.add(provinceLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField provinceField = new JTextField();
        provinceField.setFont(fieldFont);
        provinceField.setBackground(fieldBg);
        provinceField.setBorder(fieldBorder);
        provinceField.setMinimumSize(new Dimension(200, fieldH));
        provinceField.setPreferredSize(new Dimension(400, fieldH));
        contentPanel.add(provinceField, gbc);
        leftFields[3] = provinceField;
        leftRow++;
        
        // Handle Municipality field as ComboBox
        gbc.gridx = 0; gbc.gridy = leftRow; gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel municipalityLabel = new JLabel("MUNICIPALITY");
        municipalityLabel.setFont(fieldFont.deriveFont(Font.BOLD));
        municipalityLabel.setForeground(new Color(44, 62, 80));
        contentPanel.add(municipalityLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] municipalityOptions = {"Almeria", "Biliran", "Cabucgayan", "Caibiran", "Culaba", "Kawayan", "Maripipi", "Naval"};
        municipalityComboBox = new JComboBox<>(municipalityOptions);
        municipalityComboBox.setFont(fieldFont);
        municipalityComboBox.setBackground(fieldBg);
        municipalityComboBox.setBorder(fieldBorder);
        municipalityComboBox.setMinimumSize(new Dimension(200, fieldH));
        municipalityComboBox.setPreferredSize(new Dimension(400, fieldH));
        contentPanel.add(municipalityComboBox, gbc);
        leftRow++;
        
        // Handle Barangay field
        gbc.gridx = 0; gbc.gridy = leftRow; gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel barangayLabel = new JLabel(leftLabels[4]);
        barangayLabel.setFont(fieldFont.deriveFont(Font.BOLD));
        barangayLabel.setForeground(new Color(44, 62, 80));
        contentPanel.add(barangayLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField barangayField = new JTextField();
        barangayField.setFont(fieldFont);
        barangayField.setBackground(fieldBg);
        barangayField.setBorder(fieldBorder);
        barangayField.setMinimumSize(new Dimension(200, fieldH));
        barangayField.setPreferredSize(new Dimension(400, fieldH));
        contentPanel.add(barangayField, gbc);
        leftFields[4] = barangayField;

        // Right column fields (excluding Gender which will be handled separately)
        String[] rightLabels = {"AGE", "PHONE NUMBER", "EMAIL", "SECTOR", "PURPOSE"};
        rightFields = new JTextField[rightLabels.length];
        int rightRow = 2;
        
        // Handle Gender field separately as ComboBox
        gbc.gridx = 2; gbc.gridy = rightRow; gbc.gridwidth = 1;
        gbc.weightx = 0;
        JLabel genderLabel = new JLabel("GENDER");
        genderLabel.setFont(fieldFont.deriveFont(Font.BOLD));
        genderLabel.setForeground(new Color(44, 62, 80));
        contentPanel.add(genderLabel, gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] genderOptions = {"Male", "Female", "Prefer not to say"};
        genderComboBox = new JComboBox<>(genderOptions);
        genderComboBox.setFont(fieldFont);
        genderComboBox.setBackground(fieldBg);
        genderComboBox.setBorder(fieldBorder);
        genderComboBox.setMinimumSize(new Dimension(200, fieldH));
        genderComboBox.setPreferredSize(new Dimension(400, fieldH));
        contentPanel.add(genderComboBox, gbc);
        rightRow++;
        
        // Handle remaining right column fields
        for (int i = 0; i < rightLabels.length; i++) {
            gbc.gridx = 2; gbc.gridy = rightRow; gbc.gridwidth = 1;
            gbc.weightx = 0;
            JLabel l = new JLabel(rightLabels[i]);
            l.setFont(fieldFont.deriveFont(Font.BOLD));
            l.setForeground(new Color(44, 62, 80));
            contentPanel.add(l, gbc);
            gbc.gridx = 3;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            JTextField tf = new JTextField();
            tf.setFont(fieldFont);
            tf.setBackground(fieldBg);
            tf.setBorder(fieldBorder);
            tf.setMinimumSize(new Dimension(200, fieldH));
            tf.setPreferredSize(new Dimension(400, fieldH));
            contentPanel.add(tf, gbc);
            rightFields[i] = tf;
            rightRow++;
        }

        // Signature and Photo row
        gbc.gridx = 0; gbc.gridy += 2; gbc.gridwidth = 2;
        JPanel signaturePanel = new JPanel(new BorderLayout());
        signaturePanel.setPreferredSize(new Dimension(140, 160));
        signaturePanel.setBackground(new Color(245, 247, 250));
        signaturePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2), "SIGNATURE", 0, 2, new Font("SansSerif", Font.BOLD, 13), new Color(41, 128, 185)));
        
        // Create signature image label
        signatureImageLabel = new JLabel("Signature not available");
        signatureImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        signatureImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        signatureImageLabel.setPreferredSize(new Dimension(120, 80));
        signatureImageLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        signaturePanel.add(signatureImageLabel, BorderLayout.CENTER);
        
        // Add Signature button
        JButton signatureBtn = new JButton("Signature");
        signatureBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        signatureBtn.setBackground(new Color(21, 101, 192)); // #1565c0
        signatureBtn.setForeground(Color.WHITE);
        signatureBtn.setFocusPainted(false);
        signatureBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(21, 101, 192), 1, true),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
        signatureBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signatureBtn.setOpaque(true);
        signatureBtn.setBorderPainted(true);
        signatureBtn.setMargin(new Insets(8, 24, 8, 24));
        signatureBtn.setPreferredSize(new Dimension(140, 36));
        signatureBtn.setMaximumSize(new Dimension(160, 40));
        signatureBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signatureBtn.setBackground(new Color(13, 71, 161)); // darker blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signatureBtn.setBackground(new Color(21, 101, 192));
            }
        });
        signatureBtn.addActionListener(e -> {
            // Pass reference to this LogbookFormPanel to DrawingCanvas
            model.DrawingCanvas.main(new String[]{}, this);
        });
        signaturePanel.add(signatureBtn, BorderLayout.SOUTH);
        contentPanel.add(signaturePanel, gbc);

        // Photo panel next to signature
        gbc.gridx = 2; gbc.gridy = gbc.gridy; gbc.gridwidth = 2;
        JPanel photoPanel = new JPanel(new BorderLayout());
        photoPanel.setPreferredSize(new Dimension(140, 160));
        photoPanel.setBackground(new Color(245, 247, 250));
        photoPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2), "PHOTO", 0, 2, new Font("SansSerif", Font.BOLD, 13), new Color(41, 128, 185)));
        
        // Create photo image label
        photoImageLabel = new JLabel("Photo capture not available");
        photoImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        photoImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        photoImageLabel.setPreferredSize(new Dimension(120, 80));
        photoImageLabel.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1));
        photoPanel.add(photoImageLabel, BorderLayout.CENTER);
        
        // Add Take a photo button
        photoBtn = new JButton("Take a photo");
        photoBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        photoBtn.setBackground(new Color(21, 101, 192));
        photoBtn.setForeground(Color.WHITE);
        photoBtn.setFocusPainted(false);
        photoBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(21, 101, 192), 1, true),
            BorderFactory.createEmptyBorder(8, 24, 8, 24)
        ));
        photoBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        photoBtn.setOpaque(true);
        photoBtn.setBorderPainted(true);
        photoBtn.setMargin(new Insets(8, 24, 8, 24));
        photoBtn.setPreferredSize(new Dimension(140, 36));
        photoBtn.setMaximumSize(new Dimension(160, 40));
        photoBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                photoBtn.setBackground(new Color(13, 71, 161));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                photoBtn.setBackground(new Color(21, 101, 192));
            }
        });
        photoBtn.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                new model.WebcamCapture(this);
            });
        });
        photoPanel.add(photoBtn, BorderLayout.SOUTH);
        contentPanel.add(photoPanel, gbc);

        // Set contentPanel preferred size to make the form smaller and fit on screen
        contentPanel.setPreferredSize(new Dimension(700, 600));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null); // Optional: cleaner look
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setPreferredSize(new Dimension(700, 500));
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add submit button at the bottom, outside the scroll pane
        JPanel submitPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitPanel.setOpaque(false);
        submitBtn = new JButton("SUBMIT");
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitBtn.setBackground(new Color(41, 128, 185));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setPreferredSize(new Dimension(200, 40));
        submitBtn.addActionListener(e -> {
            if (validateForm()) {
                saveVisitorToDatabase();
            }
        });
        submitPanel.add(submitBtn);
        add(submitPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update signature image display
     */
    public void updateSignatureImage(String imagePath) {
        System.out.println("[DEBUG] updateSignatureImage called with: " + imagePath);
        currentSignaturePath = imagePath;
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                // Scale image to fit the label
                Image scaledImage = image.getScaledInstance(120, 80, Image.SCALE_SMOOTH);
                signatureImageLabel.setIcon(new ImageIcon(scaledImage));
                signatureImageLabel.setText("");
                System.out.println("[DEBUG] Signature image updated successfully");
            } else {
                System.err.println("[DEBUG] Signature file does not exist: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading signature image: " + e.getMessage());
            signatureImageLabel.setIcon(null);
            signatureImageLabel.setText("Error loading signature");
        }
    }
    
    /**
     * Update photo image display
     */
    public void updatePhotoImage(String imagePath) {
        System.out.println("[DEBUG] updatePhotoImage called with: " + imagePath);
        currentPhotoPath = imagePath;
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                BufferedImage image = ImageIO.read(imageFile);
                // Scale image to fit the label
                Image scaledImage = image.getScaledInstance(120, 80, Image.SCALE_SMOOTH);
                photoImageLabel.setIcon(new ImageIcon(scaledImage));
                photoImageLabel.setText("");
                System.out.println("[DEBUG] Photo image updated successfully");
            } else {
                System.err.println("[DEBUG] Photo file does not exist: " + imagePath);
            }
        } catch (IOException e) {
            System.err.println("Error loading photo image: " + e.getMessage());
            photoImageLabel.setIcon(null);
            photoImageLabel.setText("Error loading photo");
        }
    }
    
    /**
     * Get current signature path
     */
    public String getCurrentSignaturePath() {
        System.out.println("[DEBUG] getCurrentSignaturePath: " + currentSignaturePath);
        return currentSignaturePath;
    }
    
    /**
     * Get current photo path
     */
    public String getCurrentPhotoPath() {
        System.out.println("[DEBUG] getCurrentPhotoPath: " + currentPhotoPath);
        return currentPhotoPath;
    }
    
    /**
     * Validate form fields before submission
     */
    private boolean validateForm() {
        // Check required fields
        if (leftFields[0].getText().trim().isEmpty()) {
            showError("Last Name is required!");
            leftFields[0].requestFocus();
            return false;
        }
        
        if (leftFields[1].getText().trim().isEmpty()) {
            showError("First Name is required!");
            leftFields[1].requestFocus();
            return false;
        }
        
        if (leftFields[3].getText().trim().isEmpty()) {
            showError("Province is required!");
            leftFields[3].requestFocus();
            return false;
        }
        
        if (municipalityComboBox.getSelectedItem() == null || municipalityComboBox.getSelectedItem().toString().trim().isEmpty()) {
            showError("Municipality is required!");
            municipalityComboBox.requestFocus();
            return false;
        }
        
        if (leftFields[4].getText().trim().isEmpty()) {
            showError("Barangay is required!");
            leftFields[4].requestFocus();
            return false;
        }
        
        if (rightFields[0].getText().trim().isEmpty()) {
            showError("Age is required!");
            rightFields[0].requestFocus();
            return false;
        }
        
        // Validate age is a number
        try {
            int age = Integer.parseInt(rightFields[0].getText().trim());
            if (age <= 0 || age > 150) {
                showError("Please enter a valid age (1-150)!");
                rightFields[0].requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("Age must be a number!");
            rightFields[0].requestFocus();
            return false;
        }
        
        if (genderComboBox.getSelectedItem() == null || genderComboBox.getSelectedItem().toString().trim().isEmpty()) {
            showError("Gender is required!");
            genderComboBox.requestFocus();
            return false;
        }
        
        if (rightFields[1].getText().trim().isEmpty()) {
            showError("Phone Number is required!");
            rightFields[1].requestFocus();
            return false;
        }
        
        if (rightFields[4].getText().trim().isEmpty()) {
            showError("Purpose is required!");
            rightFields[4].requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Save visitor data to database
     */
    private void saveVisitorToDatabase() {
        try {
            // Create Visitor object from form data
            Visitor visitor = new Visitor();
            visitor.setLastName(leftFields[0].getText().trim());
            visitor.setFirstName(leftFields[1].getText().trim());
            visitor.setMiddleName(leftFields[2].getText().trim());
            visitor.setProvince(leftFields[3].getText().trim());
            visitor.setMunicipality(municipalityComboBox.getSelectedItem().toString());
            visitor.setBarangay(leftFields[4].getText().trim());
            visitor.setAge(Integer.parseInt(rightFields[0].getText().trim()));
            visitor.setGender(genderComboBox.getSelectedItem().toString());
            visitor.setPhoneNumber(rightFields[1].getText().trim());
            visitor.setEmail(rightFields[2].getText().trim());
            visitor.setSector(rightFields[3].getText().trim());
            visitor.setPurpose(rightFields[4].getText().trim());
            visitor.setTimestamp(LocalDateTime.now());
            // Set signature and photo paths
            visitor.setSignaturePath(getCurrentSignaturePath());
            visitor.setPhotoPath(getCurrentPhotoPath());
            
            // Save to database
            VisitorService visitorService = new VisitorService();
            boolean success = visitorService.saveVisitor(visitor);
            
            if (success) {
                showSuccess("Visitor information saved successfully!\nLogbook Number: " + visitor.getLogbookNumber());
                clearForm();
                updateLogbookNumber();
            } else {
                showError("Failed to save visitor information. Please try again.");
            }
            
        } catch (Exception e) {
            System.err.println("Error saving visitor: " + e.getMessage());
            e.printStackTrace();
            showError("An error occurred while saving. Please check your database connection.");
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Show success message
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Clear all form fields
     */
    private void clearForm() {
        // Clear left fields
        for (JTextField field : leftFields) {
            field.setText("");
        }
        
        // Clear right fields
        for (JTextField field : rightFields) {
            field.setText("");
        }
        
        // Reset combo boxes
        municipalityComboBox.setSelectedIndex(0);
        genderComboBox.setSelectedIndex(0);
        
        // Clear images
        signatureImageLabel.setIcon(null);
        signatureImageLabel.setText("Signature not available");
        photoImageLabel.setIcon(null);
        photoImageLabel.setText("Photo capture not available");
        currentSignaturePath = null;
        currentPhotoPath = null;
    }
    
    /**
     * Update logbook number display
     */
    private void updateLogbookNumber() {
        try {
            VisitorService visitorService = new VisitorService();
            String newNumber = visitorService.generateLogbookNumber();
            noLabel.setText("No. " + newNumber);
        } catch (Exception e) {
            System.err.println("Error updating logbook number: " + e.getMessage());
        }
    }
} 