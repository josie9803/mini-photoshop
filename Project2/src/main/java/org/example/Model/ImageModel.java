package org.example.Model;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageModel {
    private BufferedImage image;
    public void readBmpFile(File bmpFile) throws IOException {
        // Use Apache Commons Imaging to read the BMP file
        try {
            image = Imaging.getBufferedImage(bmpFile);
        } catch (ImageReadException e) {
            throw new RuntimeException(e);
        }
        if (image == null) {
            throw new IOException("Invalid BMP file.");
        }
    }
    public BufferedImage getImage(){
        return image;
    }

    public BufferedImage getGrayscaleImage() {
        if (image == null) return null;
        BufferedImage grayImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color c = new Color(image.getRGB(x, y));
                int red = (int) (c.getRed()*0.299);
                int green = (int) (c.getGreen()*0.587);
                int blue = (int) (c.getBlue()*0.114);
                Color newColor = new Color(red+green+blue, red+green+blue, red+green+blue);
                grayImage.setRGB(x, y, newColor.getRGB());
            }
        }
        return grayImage;
    }


    public BufferedImage getDitheredImage() {
        BufferedImage grayImage = getGrayscaleImage();
        if (grayImage == null) return null;

        int[][] bayerMatrix = {
                {15, 7, 13, 5},
                {3, 11, 1, 9},
                {12, 4, 14, 6},
                {0, 8, 2, 10}
        };
        int matrixSize = bayerMatrix.length;
        BufferedImage ditheredImage = new BufferedImage(grayImage.getWidth(), grayImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < grayImage.getHeight(); y++) {
            for (int x = 0; x < grayImage.getWidth(); x++) {
                int grayValue = new Color(grayImage.getRGB(x, y)).getRed(); // grayscale, so R=G=B
                int threshold = (bayerMatrix[y % matrixSize][x % matrixSize] * 255) / (matrixSize * matrixSize);

                // Apply threshold to get dithered pixel
                int color = (grayValue > threshold) ? 255 : 0;
                ditheredImage.setRGB(x, y, new Color(color, color, color).getRGB());
            }
        }

        return ditheredImage;
    }

    public BufferedImage autoLevel() {
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        int minIntensity = 255;
        int maxIntensity = 0;

        // Step 1: Find the min and max intensity values
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int intensity = (color.getRed() + color.getGreen() + color.getBlue()) / 3; // Grayscale intensity
                if (intensity < minIntensity) minIntensity = intensity;
                if (intensity > maxIntensity) maxIntensity = intensity;
            }
        }

        BufferedImage autoLeveledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Step 3: Apply auto-leveling to each pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = clamp(autoLevelIntensity(color.getRed(), minIntensity, maxIntensity)) ;
                int green = clamp(autoLevelIntensity(color.getGreen(), minIntensity, maxIntensity));
                int blue = clamp(autoLevelIntensity(color.getBlue(), minIntensity, maxIntensity));

                // Set new color in auto-leveled image
                Color newColor = new Color(red, green, blue);
                autoLeveledImage.setRGB(x, y, newColor.getRGB());
            }
        }

        return autoLeveledImage;
    }

    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private int autoLevelIntensity(int colorValue, int minIntensity, int maxIntensity) {
        // Map intensity to the full 0-255 range
        return (colorValue - minIntensity) * 255 / (maxIntensity - minIntensity);
    }

}
