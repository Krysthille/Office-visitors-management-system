package model;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Window;


// LoadingDialog: modal dialog with rotating icon and message
class LoadingDialog extends JDialog {
    private final JLabel label;
    private final Timer timer;
    private int angle = 0;
    private final String message;

    public LoadingDialog(Window parent, String message) {
        super(parent, message, ModalityType.APPLICATION_MODAL);
        this.message = message;
        setUndecorated(true);
        setLayout(new BorderLayout());
        setSize(220, 100);
        setLocationRelativeTo(parent);
        label = new JLabel(message, JLabel.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
        add(label, BorderLayout.CENTER);
        // Rotating icon
        JLabel iconLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2.translate(w/2, h/2);
                g2.rotate(Math.toRadians(angle));
                g2.setColor(new Color(41,128,185));
                g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(-18, -18, 36, 36, 0, 270);
                g2.dispose();
            }
        };
        iconLabel.setPreferredSize(new Dimension(40,40));
        add(iconLabel, BorderLayout.WEST);
        timer = new Timer(40, e -> {
            angle = (angle + 10) % 360;
            iconLabel.repaint();
        });
    }
    public void showDialog() {
        timer.start();
        setVisible(true);
    }
    public void closeDialog() {
        timer.stop();
        setVisible(false);
        dispose();
    }
}

public class WebcamCapture extends JFrame {
    static {
        System.load("C:/xampp/htdocs/pulse_repository/personal/dict_project/ovms_project/office_visitor_management_system/lib/opencv/build/java/x64/opencv_java4110.dll");
    }

    private JLabel cameraScreen;
    private JButton captureButton;
    private VideoCapture capture;
    private Mat frame;
    private boolean capturing = true;
    private Thread cameraThread;
    private LoadingDialog loadingDialog;
    private Object logbookFormPanel; // Reference to the form panel

    public WebcamCapture() {
        this(null);
    }
    
    public WebcamCapture(Object logbookFormPanel) {
        super("Camera Capture");
        this.logbookFormPanel = logbookFormPanel;
        setLayout(new BorderLayout());

        cameraScreen = new JLabel();
        captureButton = new JButton("Capture");

        add(cameraScreen, BorderLayout.CENTER);
        add(captureButton, BorderLayout.SOUTH);

        setSize(640, 480);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Show loading dialog while opening camera
        loadingDialog = new LoadingDialog(this, "Opening Camera");
        SwingUtilities.invokeLater(() -> loadingDialog.showDialog());

        // Initialize camera with error handling (in background)
        new Thread(() -> {
            boolean opened = initializeCamera();
            SwingUtilities.invokeLater(() -> {
                loadingDialog.closeDialog();
                if (opened) {
                    setVisible(true);
                    startCameraThread();
                } else {
                    showCameraError();
                }
            });
        }).start();

        captureButton.addActionListener(e -> {
            if (capture != null && capture.isOpened() && !frame.empty()) {
                BufferedImage bufferedImage = matToBufferedImage(frame);
                // Open preview and hide camera window
                this.setVisible(false);
                new CaptureImagePreview(bufferedImage, this, logbookFormPanel);
            } else {
                JOptionPane.showMessageDialog(this, "Camera not available for capture.", "Camera Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private boolean initializeCamera() {
        try {
            capture = new VideoCapture(0);
            frame = new Mat();
            
            if (!capture.isOpened()) {
                // Try alternative camera index
                capture = new VideoCapture(1);
                if (!capture.isOpened()) {
                    System.out.println("Camera not detected or not available!");
                    return false;
                }
            }
            
            // Test if we can read a frame
            if (!capture.read(frame)) {
                System.out.println("Cannot read from camera!");
                capture.release();
                capture = null;
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error initializing camera: " + e.getMessage());
            capture = null;
            return false;
        }
    }

    private void showCameraError() {
        JOptionPane.showMessageDialog(this, 
            "Camera is not available or cannot be accessed.\n\n" +
            "Possible solutions:\n" +
            "• Check if camera is connected\n" +
            "• Make sure no other application is using the camera\n" +
            "• Check camera permissions\n" +
            "• Restart the application", 
            "Camera Not Available", 
            JOptionPane.ERROR_MESSAGE);
        cleanupAndClose();
    }

    private void startCameraThread() {
        capturing = true;
        cameraThread = new Thread(() -> {
            while (capturing && capture != null && capture.isOpened()) {
                try {
                    if (capture.read(frame)) {
                        if (!frame.empty()) {
                            ImageIcon image = new ImageIcon(matToBufferedImage(frame));
                            SwingUtilities.invokeLater(() -> {
                                cameraScreen.setIcon(image);
                            });
                        }
                    } else {
                        // If we can't read frame, stop the loop
                        System.out.println("Cannot read frame from camera");
                        break;
                    }
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    System.out.println("Camera thread interrupted");
                    break;
                } catch (Exception e) {
                    System.err.println("Error in camera thread: " + e.getMessage());
                    break;
                }
            }
            // Clean up when thread ends
            if (capture != null) {
                capture.release();
            }
        });
        cameraThread.start();
    }

    public void cleanupAndClose() {
        // Show closing dialog
        LoadingDialog closingDialog = new LoadingDialog(this, "Closing Camera");
        SwingUtilities.invokeLater(() -> closingDialog.showDialog());
        // Stop camera thread and release
        new Thread(() -> {
            capturing = false;
            if (cameraThread != null && cameraThread.isAlive()) {
                try { cameraThread.join(300); } catch (InterruptedException ignored) {}
            }
            if (capture != null && capture.isOpened()) {
                capture.release();
            }
            SwingUtilities.invokeLater(() -> {
                closingDialog.closeDialog();
                dispose();
            });
        }).start();
    }

    private BufferedImage matToBufferedImage(Mat matrix) {
        try {
            MatOfByte mob = new MatOfByte();
            Imgcodecs.imencode(".jpg", matrix, mob);
            byte[] byteArray = mob.toArray();
            return ImageIO.read(new ByteArrayInputStream(byteArray));
        } catch (Exception e) {
            System.err.println("Error converting Mat to BufferedImage: " + e.getMessage());
            // Return a blank image if conversion fails
            return new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        }
    }

    @Override
    public void dispose() {
        capturing = false;
        if (cameraThread != null && cameraThread.isAlive()) {
            try { cameraThread.join(300); } catch (InterruptedException ignored) {}
        }
        if (capture != null && capture.isOpened()) {
            capture.release();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new WebcamCapture());
    }
}

class CaptureImagePreview extends JFrame {
    private final WebcamCapture parentWindow;
    private final Object logbookFormPanel;
    public CaptureImagePreview(BufferedImage image, WebcamCapture parentWindow, Object logbookFormPanel) {
        super("Captured Image Preview");
        this.parentWindow = parentWindow;
        this.logbookFormPanel = logbookFormPanel;
        setLayout(new BorderLayout());
        setSize(400, 400);
        setLocationRelativeTo(null);

        JLabel imageLabel = new JLabel(new ImageIcon(image));
        add(imageLabel, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        JButton retake = new JButton("Retake");

        buttons.add(save);
        buttons.add(cancel);
        buttons.add(retake);
        add(buttons, BorderLayout.SOUTH);

        save.addActionListener(e -> {
            try {
                File dir = new File("CaptureImage");
                if (!dir.exists()) dir.mkdirs();
                File output = new File(dir, "captured_" + System.currentTimeMillis() + ".png");
                ImageIO.write(image, "png", output);
                
                // Update the form panel if available
                if (logbookFormPanel != null) {
                    try {
                        // Use SwingUtilities.invokeLater to ensure UI updates happen on EDT
                        SwingUtilities.invokeLater(() -> {
                            try {
                                java.lang.reflect.Method updateMethod = logbookFormPanel.getClass().getMethod("updatePhotoImage", String.class);
                                updateMethod.invoke(logbookFormPanel, output.getAbsolutePath());
                                System.out.println("[DEBUG] Photo updated: " + output.getAbsolutePath());
                            } catch (Exception ex) {
                                System.err.println("Error updating form panel: " + ex.getMessage());
                                ex.printStackTrace();
                            }
                        });
                    } catch (Exception ex) {
                        System.err.println("Error scheduling form panel update: " + ex.getMessage());
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Image saved to: " + output.getAbsolutePath());
                dispose();
                if (parentWindow != null) parentWindow.cleanupAndClose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        cancel.addActionListener(e -> {
            dispose();
            if (parentWindow != null) parentWindow.cleanupAndClose();
        });

        retake.addActionListener(e -> {
            dispose();
            if (parentWindow != null) parentWindow.cleanupAndClose();
            new WebcamCapture(logbookFormPanel);
        });

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
