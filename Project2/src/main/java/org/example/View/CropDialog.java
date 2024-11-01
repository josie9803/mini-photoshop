package org.example.View;
import javax.swing.*;
import java.awt.*;

public class CropDialog extends JDialog {
    private final JTextField xField;
    private final JTextField yField;
    private final JTextField widthField;
    private final JTextField heightField;
    private CropListener cropListener;

    public CropDialog(Frame owner) {
        super(owner, "Crop Input", true);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("X Coordinate:"));
        xField = new JTextField();
        add(xField);

        add(new JLabel("Y Coordinate:"));
        yField = new JTextField();
        add(yField);

        add(new JLabel("Crop Width:"));
        widthField = new JTextField();
        add(widthField);

        add(new JLabel("Crop Height:"));
        heightField = new JTextField();
        add(heightField);

        JButton okButton = new JButton("OK");
        add(okButton);
        okButton.addActionListener(e -> {
            int x = Integer.parseInt(xField.getText());
            int y = Integer.parseInt(yField.getText());
            int width = Integer.parseInt(widthField.getText());
            int height = Integer.parseInt(heightField.getText());
            if (cropListener != null) {
                notifyListeners(x, y, width, height); // Notify listener
            }
            dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        add(cancelButton);

        pack();
        setLocationRelativeTo(owner);
    }

    public void addListener(CropListener listener) {
        this.cropListener = listener;
    }

    private void notifyListeners(int x, int y, int width, int height) {
        cropListener.onCrop(x, y, width, height);
    }
    public interface CropListener {
        void onCrop(int x, int y, int width, int height);
    }
}
