package org.example.Model;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageModel {
    private BufferedImage image;
    private int imageWidth;
    private int imageHeight;
    public enum ImageState {
        ORIGINAL, GRAYSCALE, ORDERED_DITHER, AUTO_LEVEL
    }
    private ImageState currentState;
    public void readBmpFile(File bmpFile) throws IOException {
        try {
            image = Imaging.getBufferedImage(bmpFile);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }
        if (image == null) {
            throw new IOException("Invalid BMP file.");
        }
        imageWidth = image.getWidth();
        imageHeight = image.getHeight();
        currentState = ImageState.ORIGINAL;
    }
    public BufferedImage getImage(){
        return image;
    }
    public BufferedImage getCurrentImage() {
        return switch (currentState) {
            case GRAYSCALE -> getGrayscaleImage();
            case ORDERED_DITHER -> getDitheredImage();
            case AUTO_LEVEL -> getAutoLevelImage();
            default -> image;
        };
    }
    public BufferedImage getCropImage(int x, int y, int width, int height){
        BufferedImage cropImage = null;
        if (image != null) {
            cropImage = image.getSubimage(x, y, width, height);
        }
        return cropImage;
    }
    public void setCurrentState(ImageState state) {
        this.currentState = state;
    }

    public BufferedImage getGrayscaleImage() {
        if (image == null) return null;
        BufferedImage grayImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color c = new Color(image.getRGB(x, y));
                int red = (int) (c.getRed()*0.299);
                int green = (int) (c.getGreen()*0.587);
                int blue = (int) (c.getBlue()*0.114);
                Color newColor = new Color(red+green+blue, red+green+blue, red+green+blue);
                grayImage.setRGB(x, y, newColor.getRGB());
            }
        }
        setCurrentState(ImageState.GRAYSCALE);
        return grayImage;
    }

    public BufferedImage getDitheredImage() {
        BufferedImage grayImage = getGrayscaleImage();
        if (grayImage == null) return null;

        int[][] ditherMatrix = {
                {0, 8, 2, 10},
                {12, 4, 14, 6},
                {3, 11, 1, 9},
                {15, 7, 13, 5},
        };
        int matrixSize = ditherMatrix.length;
        BufferedImage ditheredImage = new BufferedImage(grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < grayImage.getWidth(); x++) {
            for (int y = 0; y < grayImage.getHeight(); y++) {
                int input = new Color(grayImage.getRGB(x, y)).getRed();
                int scaledInput = input * matrixSize * matrixSize / 256;
                int i = x % matrixSize;
                int j = y % matrixSize;
                int dither = (ditherMatrix[i][j]);

                int output = (scaledInput > dither) ? 255 : 0;
                ditheredImage.setRGB(x, y, new Color(output, output, output).getRGB());
            }
        }
        setCurrentState(ImageState.ORDERED_DITHER);
        return ditheredImage;
    }

    public BufferedImage getAutoLevelImage() {
        if (image == null) {
            return null;
        }

        int minIntensity = 255;
        int maxIntensity = 0;

        // Step 1: Find the min and max intensity values
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color color = new Color(image.getRGB(x, y));
                int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3; // Grayscale intensity
                if (intensity < minIntensity) minIntensity = intensity;
                if (intensity > maxIntensity) maxIntensity = intensity;
            }
        }

        BufferedImage autoLeveledImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        // Step 3: Apply auto-leveling to each pixel
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = clamp(autoLevelIntensity(color.getRed(), minIntensity, maxIntensity)) ;
                int green = clamp(autoLevelIntensity(color.getGreen(), minIntensity, maxIntensity));
                int blue = clamp(autoLevelIntensity(color.getBlue(), minIntensity, maxIntensity));

                // Set new color in auto-leveled image
                Color newColor = new Color(red, green, blue);
                autoLeveledImage.setRGB(x, y, newColor.getRGB());
            }
        }
        setCurrentState(ImageState.AUTO_LEVEL);
        return autoLeveledImage;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private int autoLevelIntensity(int colorValue, int minIntensity, int maxIntensity) {
        // Map intensity to the full 0-255 range
        return (colorValue - minIntensity) * 255 / (maxIntensity - minIntensity);
    }

    public void saveImage(BufferedImage editedImage, File file) throws IOException, ImageWriteException {
        if (editedImage != null) {
            Imaging.writeImage(editedImage, file, ImageFormats.BMP);
        }
    }
}
