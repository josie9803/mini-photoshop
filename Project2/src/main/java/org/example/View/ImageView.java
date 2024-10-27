package org.example.View;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageView extends JFrame {
    private JMenuItem openFileItem;
    private JMenuItem exitItem;
    private JMenuItem grayscaleItem;
    private JMenuItem ditherItem;
    private JMenuItem autoLevelItem;

    public ImageView() {
        setTitle("BMP Viewer");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        getContentPane().add(imageLabel, BorderLayout.CENTER);

        createMenuBar();
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        openFileItem = new JMenuItem("Open File");
        styleButtonItem(openFileItem, new Color(165, 182, 141));

        exitItem = new JMenuItem("Exit");
        styleButtonItem(exitItem, new Color(160, 35, 52));

        grayscaleItem = new JMenuItem("Grayscale");
        styleButtonItem(grayscaleItem, new Color(100, 149, 237));

        ditherItem = new JMenuItem("Ordered Dithering");
        styleButtonItem(ditherItem, new Color(123, 104, 238));

        autoLevelItem = new JMenuItem("Auto Level");
        styleButtonItem(autoLevelItem, new Color(100, 149, 237));

        menuBar.add(openFileItem);
        menuBar.add(exitItem);
        menuBar.add(grayscaleItem);
        menuBar.add(ditherItem);
        menuBar.add(autoLevelItem);
        setJMenuBar(menuBar);
    }

    private void styleButtonItem(JMenuItem menuItem, Color color) {
        menuItem.setBorder(new CompoundBorder(new LineBorder(Color.DARK_GRAY, 1),
                new EmptyBorder(5, 20, 5, 10)));
        menuItem.setBackground(color);
        menuItem.setOpaque(true);
        menuItem.setPreferredSize(new Dimension(50, 50));

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(color.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(color);
            }
        });
    }

    public void updateImage(BufferedImage image) {
        if (image != null) {
            getContentPane().removeAll();
            JLabel singleImageLabel = new JLabel(new ImageIcon(image));
            singleImageLabel.setHorizontalAlignment(JLabel.CENTER);
            getContentPane().add(singleImageLabel, BorderLayout.CENTER);

            revalidate();
            repaint();
        }
    }
    public void updateImageWithGrayScale(BufferedImage original, BufferedImage grayscale) {
        if (original != null && grayscale != null) {
            getContentPane().removeAll();
            JPanel panel = new JPanel(new GridLayout(1, 2));

            JLabel originalLabel = new JLabel(new ImageIcon(original));
            originalLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(originalLabel);

            JLabel grayscaleLabel = new JLabel(new ImageIcon(grayscale));
            grayscaleLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(grayscaleLabel);

            getContentPane().add(panel, BorderLayout.CENTER);
            revalidate();
            repaint();
        }
    }

    public void updateImageWithDithering(BufferedImage grayscaleImage, BufferedImage ditheredImage) {
        getContentPane().removeAll();
        JPanel panel = new JPanel(new GridLayout(1, 2));

        JLabel grayscaleLabel = new JLabel(new ImageIcon(grayscaleImage));
        grayscaleLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(grayscaleLabel);

        JLabel ditheredLabel = new JLabel(new ImageIcon(ditheredImage));
        ditheredLabel.setHorizontalAlignment(JLabel.CENTER);
        panel.add(ditheredLabel);

        getContentPane().add(panel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
    public void updateImageWithAutoLevel(BufferedImage originalImage, BufferedImage autoLeveledImage) {
        // Create icons for the original and auto-leveled images
        ImageIcon originalIcon = new ImageIcon(originalImage);
        ImageIcon autoLeveledIcon = new ImageIcon(autoLeveledImage);

        JPanel imagePanel = new JPanel(new GridLayout(1, 2));

        JLabel originalLabel = new JLabel(originalIcon);
        originalLabel.setHorizontalAlignment(JLabel.CENTER);
        JLabel autoLeveledLabel = new JLabel(autoLeveledIcon);
        autoLeveledLabel.setHorizontalAlignment(JLabel.CENTER);

        imagePanel.add(originalLabel);
        imagePanel.add(autoLeveledLabel);

        getContentPane().removeAll();
        getContentPane().add(imagePanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }



    public void addOpenFileButtonListener(ActionListener listener) {
        openFileItem.addActionListener(listener);
    }

    public void addExitButtonListener(ActionListener listener) {
        exitItem.addActionListener(listener);
    }

    public void addGrayscaleButtonListener(ActionListener listener) {
        grayscaleItem.addActionListener(listener);
    }
    public void addDitherButtonListener(ActionListener listener) {
        ditherItem.addActionListener(listener);
    }
    public void addAutoLevelButtonListener(ActionListener listener) {
        autoLevelItem.addActionListener(listener);
    }

    public File showOpenFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("BMP Images", "bmp"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
