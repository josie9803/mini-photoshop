package org.example.Model;
import org.apache.commons.imaging.ImageFormats;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.example.View.CropDialog;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageModel implements CropDialog.CropListener{
    private BufferedImage image;
    private BufferedImage cropImage;
    private int imageWidth;
    private int imageHeight;
    public enum ImageState {
        ORIGINAL, GRAYSCALE, ORDERED_DITHER, AUTO_LEVEL, CROPPED
    }
    private static final int RED = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
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
            case CROPPED -> getCropImage();
            default -> image;
        };
    }
    public BufferedImage getCropImage() {
        return cropImage;
    }
    @Override
    public void onCrop(int x, int y, int width, int height) {
//        System.out.println("Requested crop: x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);
//        System.out.println("Image dimensions: width=" + imageWidth + ", height=" + imageHeight);
        if (image == null) {
            System.out.println("No image loaded.");
            cropImage = null;
            return;
        }

        if (x < 0 || y < 0 || x + width > imageWidth || y + height > imageHeight) {
            System.out.println("Crop area out of bounds.");
            cropImage = null;
            return;
        }

        cropImage = image.getSubimage(x, y, width, height);
        setCurrentState(ImageState.CROPPED);
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

        int[] minRGB = {255, 255, 255};
        int[] maxRGB = {0, 0, 0};

        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                minRGB[RED] = Math.min(minRGB[RED], red);
                minRGB[GREEN] = Math.min(minRGB[GREEN], green);
                minRGB[BLUE] = Math.min(minRGB[BLUE], blue);

                maxRGB[RED] = Math.max(maxRGB[RED], red);
                maxRGB[GREEN] = Math.max(maxRGB[GREEN], green);
                maxRGB[BLUE] = Math.max(maxRGB[BLUE], blue);
            }
        }

        BufferedImage autoLeveledImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                red = stretch(red, minRGB[0], maxRGB[0]);
                green = stretch(green, minRGB[1], maxRGB[1]);
                blue = stretch(blue, minRGB[2], maxRGB[2]);

                Color newColor = new Color(red, green, blue);
                autoLeveledImage.setRGB(x, y, newColor.getRGB());
            }
        }

        return autoLeveledImage;
    }
    private int stretch(int value, int min, int max) {
        if (max == min) {
            return value; // Avoid division by zero if all values are the same
        }
        return Math.min(255, Math.max(0, (value - min) * 255 / (max - min)));
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
