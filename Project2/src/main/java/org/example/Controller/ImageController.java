package org.example.Controller;
import org.apache.commons.imaging.ImageWriteException;
import org.example.Model.ImageModel;
import org.example.View.CropDialog;
import org.example.View.ImageView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageController {
    private final ImageModel model;
    private final ImageView view;
    private final CropDialog cropDialog;

    public ImageController(ImageModel model, ImageView view) {
        this.model = model;
        this.view = view;
        this.cropDialog = new CropDialog(view);
        cropDialog.addListener(model);
        view.setVisible(true);
    }
    public void init(){
        view.addOpenFileButtonListener(e -> handleFileOpen());
        view.addExitButtonListener( e -> System.exit(0));
        view.addGrayscaleButtonListener(e -> handleGrayscaleOperation());
        view.addDitherButtonListener(e -> handleDitheringOperation());
        view.addAutoLevelButtonListener(e -> handleAutoLevelOperation());
        view.addExportButtonListener(e -> handleExportOperation());
        view.addCropButtonListener(e -> handleCropOperation());
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
        BufferedImage autoLeveledImage = model.getAutoLevelImage();
        if (autoLeveledImage != null) {
            view.updateImageWithAutoLevel(originalImage, autoLeveledImage);
        } else {
            view.showError("No image loaded for auto leveling.");
        }
    }

    private void handleExportOperation() { // New method to handle exporting the image
        BufferedImage currentImage = model.getCurrentImage();
        if (currentImage == null) {
            view.showError("No image loaded to export.");
            return;
        }

        File file = view.showExportFileDialog();
        if (file != null) {
            try {
                model.saveImage(currentImage, file); // Delegate the save action to the model
            } catch (IOException | ImageWriteException ex) {
                view.showError("Failed to export image: " + ex.getMessage());
            }
        }
    }
    private void handleCropOperation() {
        cropDialog.setVisible(true);

        BufferedImage croppedImage = model.getCropImage();
        if (croppedImage != null) {
            view.updateImage(croppedImage);
        } else {
            view.showError("Invalid crop area.");
        }
    }
}
