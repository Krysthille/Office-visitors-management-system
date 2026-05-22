package model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Date;
import javax.imageio.ImageIO;


public class DrawingCanvas extends JPanel {

    private List<Point> points = new ArrayList<>();
    private final Rectangle drawBox = new Rectangle(200, 100, 400, 200); // adjusted Y position for smaller gap
    private boolean isSaving = false;
    private String currentSignatureID = "";
    private boolean penMode = false;
    private Object logbookFormPanel; // Reference to the form panel

    public DrawingCanvas() {
        this(null);
    }
    
    public DrawingCanvas(Object logbookFormPanel) {
        this.logbookFormPanel = logbookFormPanel;
        setBackground(Color.WHITE);

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (penMode && drawBox.contains(e.getPoint())) {
                    points.add(e.getPoint());
                    repaint();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (penMode) points.add(null); // lift pen
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.LIGHT_GRAY);
        g2d.draw(drawBox);

        if (!isSaving && points.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.ITALIC, 16));
            g2d.setColor(Color.GRAY);
            String placeholder = "Sign here";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(placeholder);
            g2d.drawString(placeholder, drawBox.x + (drawBox.width - textWidth) / 2, drawBox.y + 30);
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            if (p1 != null && p2 != null && drawBox.contains(p1) && drawBox.contains(p2)) {
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        if (isSaving) {
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            g2d.setColor(Color.DARK_GRAY);

            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String signatureID = currentSignatureID;

            FontMetrics fm = g2d.getFontMetrics();
            int padding = 5;

            String sigIDText = "Signature ID: " + signatureID;
            int yStart = drawBox.y + drawBox.height - padding - 20;
            int xSigID = drawBox.x + drawBox.width - fm.stringWidth(sigIDText) - padding;
            g2d.drawString(sigIDText, xSigID, yStart);

            String timeText = "Timestamped: " + timestamp;
            int xTime = drawBox.x + drawBox.width - fm.stringWidth(timeText) - padding;
            g2d.drawString(timeText, xTime, yStart + 12);
        }
    }

    public void saveSignature(JFrame parent) {
        isSaving = true;
        currentSignatureID = generateHexID(10);

        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        this.paint(g2d);
        g2d.dispose();
        isSaving = false;

        BufferedImage cropped = image.getSubimage(drawBox.x, drawBox.y, drawBox.width, drawBox.height);

        int confirm = JOptionPane.showConfirmDialog(this, new JLabel(new ImageIcon(cropped)),
                "Preview Signature", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                File dir = new File("signiture");
                if (!dir.exists()) dir.mkdirs();

                String fileName = "signiture/sign_" + System.currentTimeMillis() + ".png";
                File outputFile = new File(fileName);
                ImageIO.write(cropped, "png", outputFile);
                
                // Update the form panel if available
                if (logbookFormPanel != null) {
                    try {
                        // Use SwingUtilities.invokeLater to ensure UI updates happen on EDT
                        SwingUtilities.invokeLater(() -> {
                            try {
                                java.lang.reflect.Method updateMethod = logbookFormPanel.getClass().getMethod("updateSignatureImage", String.class);
                                updateMethod.invoke(logbookFormPanel, fileName);
                                System.out.println("[DEBUG] Signature updated: " + fileName);
                            } catch (Exception ex) {
                                System.err.println("Error updating form panel: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        System.err.println("Error scheduling form panel update: " + ex.getMessage());
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Signature saved to: " + fileName);
                SwingUtilities.getWindowAncestor(this).dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save signature.");
            }
        }

        repaint();
    }

    public void clearCanvas() {
        points.clear();
        repaint();
    }

    public void togglePenMode() {
        penMode = !penMode;
        setCursor(penMode ? Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) : Cursor.getDefaultCursor());
    }

    private String generateHexID(int length) {
        Random rand = new Random();
        StringBuilder hex = new StringBuilder();
        while (hex.length() < length) {
            hex.append(Integer.toHexString(rand.nextInt(16)));
        }
        return hex.toString().toUpperCase();
    }

    public void saveSignatureImage(BufferedImage image, JFrame frameToClose) {
        try {
            File dir = new File("signiture");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = "signiture/sign_" + System.currentTimeMillis() + ".png";
            File outputFile = new File(fileName);
            javax.imageio.ImageIO.write(image, "png", outputFile);
            javax.swing.JOptionPane.showMessageDialog(this, "Signature saved to: " + fileName);
            frameToClose.dispose();
        } catch (java.io.IOException ex) {
            ex.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, "Failed to save signature.");
        }
    }

    public BufferedImage getSignatureImage() {
        isSaving = true;
        currentSignatureID = generateHexID(10);

        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        this.paint(g2d);
        g2d.dispose();
        isSaving = false;

        // Crop to the signature box
        return image.getSubimage(drawBox.x, drawBox.y, drawBox.width, drawBox.height);
    }

    public static void main(String[] args) {
        main(args, null);
    }
    
    public static void main(String[] args, Object logbookFormPanel) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Signature Pad");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);

            DrawingCanvas canvas = new DrawingCanvas(logbookFormPanel);

            JPanel buttonPanel = new JPanel();
            JButton writeButton = new JButton("Write");
            JButton clearButton = new JButton("Clear");
            JButton saveButton = new JButton("Save");

            writeButton.setBackground(new Color(200, 230, 255));
            clearButton.setBackground(new Color(255, 230, 200));
            saveButton.setBackground(new Color(200, 255, 200));

            writeButton.addActionListener(e -> canvas.togglePenMode());
            clearButton.addActionListener(e -> canvas.clearCanvas());
            saveButton.addActionListener(e -> canvas.saveSignature(frame));

            buttonPanel.add(writeButton);
            buttonPanel.add(clearButton);
            buttonPanel.add(saveButton);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 5));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPanel.add(buttonPanel, BorderLayout.NORTH);
            mainPanel.add(canvas, BorderLayout.CENTER);

            frame.setContentPane(mainPanel);
            frame.setVisible(true);
        });
    }
}
