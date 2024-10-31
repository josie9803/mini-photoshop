package org.example.View;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageView extends JFrame {
    private JMenuItem openFileItem;
    private JMenuItem exitItem;
    private JMenuItem grayscaleItem;
    private JMenuItem ditherItem;
    private JMenuItem autoLevelItem;
    private JMenuItem exportItem;

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

        JMenu coreOperationsMenu = new JMenu("Core Operations");
        openFileItem = new JMenuItem("Open File");
        styleButtonItem(openFileItem);
        coreOperationsMenu.add(openFileItem);

        grayscaleItem = new JMenuItem("Grayscale");
        styleButtonItem(grayscaleItem);
        coreOperationsMenu.add(grayscaleItem);

        ditherItem = new JMenuItem("Ordered Dithering");
        styleButtonItem(ditherItem);
        coreOperationsMenu.add(ditherItem);

        autoLevelItem = new JMenuItem("Auto Level");
        styleButtonItem(autoLevelItem);
        coreOperationsMenu.add(autoLevelItem);

        JMenu optionalOperationsMenu = new JMenu("Optional Operations");
        exportItem = new JMenuItem("Export Image");
        styleButtonItem(exportItem);
        optionalOperationsMenu.add(exportItem);

        exitItem = new JMenuItem("Exit");
        styleButtonItem(exitItem);
        coreOperationsMenu.add(exitItem);

        styleMenu(coreOperationsMenu);
        styleMenu(optionalOperationsMenu);
        menuBar.add(coreOperationsMenu);
        menuBar.add(optionalOperationsMenu);
        setJMenuBar(menuBar);
    }
    private void styleMenu(JMenu menu) {
        menu.setOpaque(true);
        menu.setPreferredSize(new Dimension(200, 40));
    }

    private void styleButtonItem(JMenuItem menuItem) {
        menuItem.setOpaque(true);
        menuItem.setPreferredSize(new Dimension(200, 40));
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
    public void addExportButtonListener(ActionListener listener) {
        exportItem.addActionListener(listener);
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
    public File showExportFileDialog() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("BMP Image", "bmp"));

        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".bmp")) {
                file = new File(file.getAbsolutePath() + ".bmp");
            }
            return file;
        }
        return null;
    }
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
