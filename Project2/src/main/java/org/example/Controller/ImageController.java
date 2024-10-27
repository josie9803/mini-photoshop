package org.example.Controller;
import org.example.Model.ImageModel;
import org.example.View.ImageView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageController {
    private final ImageModel model;
    private final ImageView view;

    public ImageController(ImageModel model, ImageView view) {
        this.model = model;
        this.view = view;
        view.setVisible(true);
    }
    public void init(){
        view.addOpenFileButtonListener(e -> handleFileOpen());
        view.addExitButtonListener( e -> System.exit(0));
        view.addGrayscaleButtonListener(e -> handleGrayscaleOperation());
        view.addDitherButtonListener(e -> handleDitheringOperation());
        view.addAutoLevelButtonListener(e -> handleAutoLevelOperation());
    }
    private void handleFileOpen() {
        File selectedFile = view.showOpenFileDialog();
        if (selectedFile != null) {
            try {
                System.out.println("Selected file: " + selectedFile.getAbsolutePath());
                model.readBmpFile(selectedFile);
                BufferedImage image = model.getImage();
                view.updateImage(image);
            } catch (IOException e) {
                view.showError("Error loading BMP file: " + e.getMessage());
            }
        }
    }
    private void handleGrayscaleOperation() {
        BufferedImage originalImage = model.getImage();
        BufferedImage grayscaleImage = model.getGrayscaleImage();

        if (originalImage != null && grayscaleImage != null) {
            view.updateImageWithGrayScale(originalImage, grayscaleImage);
        } else {
            view.showError("No image loaded. Please open a BMP file first.");
        }
    }
    private void handleDitheringOperation() {
        BufferedImage grayscaleImage = model.getGrayscaleImage();
        BufferedImage ditheredImage = model.getDitheredImage();

        if (grayscaleImage != null && ditheredImage != null) {
            view.updateImageWithDithering(grayscaleImage, ditheredImage);
        } else {
            view.showError("No image loaded. Please open a BMP file first.");
        }
    }
    private void handleAutoLevelOperation(){
        BufferedImage originalImage = model.getImage();
        BufferedImage autoLeveledImage = model.autoLevel();
        if (autoLeveledImage != null) {
            view.updateImageWithAutoLevel(originalImage, autoLeveledImage);
        } else {
            view.showError("No image loaded for auto leveling.");
        }
    }
}
