package com.tcrgroup;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class Main extends JFrame {
    private JLabel imageLabel;
    private JComboBox<String> formatComboBox;
    private File outputDir;
    private File inputFile;

    public Main() {
        setTitle("Image Converter");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        imageLabel = new JLabel("Drop Image Here", SwingConstants.CENTER);
        imageLabel.setTransferHandler(new TransferHandler() {
            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferHandler.TransferSupport support) {
                if (!canImport(support)) return false;

                Transferable transferable = support.getTransferable();
                try {
                    java.util.List<File> files = (java.util.List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        loadImage(files.get(0));
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(imageLabel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        formatComboBox = new JComboBox<>(new String[]{"JPG", "JPEG", "PDF"});
        bottomPanel.add(formatComboBox);

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(e -> convertImage());
        bottomPanel.add(convertButton);

        panel.add(bottomPanel, BorderLayout.SOUTH);
        add(panel);

        // Создание папки "IMG Converter" в папке "Мои документы"
        File documentsDir = new File(System.getProperty("user.home"), "Documents");
        outputDir = new File(documentsDir, "IMG Converter");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private void loadImage(File file) {
        inputFile = file;
        try {
            BufferedImage img = ImageIO.read(file);
            ImageIcon icon = new ImageIcon(img);
            imageLabel.setIcon(icon);
            imageLabel.setText(null);
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading image", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void convertImage() {
        ImageIcon icon = (ImageIcon) imageLabel.getIcon();
        if (icon != null && inputFile != null) {
            try {
                BufferedImage img = ImageIO.read(inputFile);
                File outputFile = new File(outputDir, "output." + formatComboBox.getSelectedItem().toString().toLowerCase());
                if (formatComboBox.getSelectedItem().toString().equalsIgnoreCase("jpg") || formatComboBox.getSelectedItem().toString().equalsIgnoreCase("jpeg")) {
                    BufferedImage convertedImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
                    convertedImg.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
                    if (ImageIO.write(convertedImg, "jpg", outputFile)) {
                        JOptionPane.showMessageDialog(this, "Image converted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error converting image", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (formatComboBox.getSelectedItem().toString().equalsIgnoreCase("pdf")) {
                    File pdfFile = new File(outputDir, "output.pdf");
                    PDFManager.convertToPDF(inputFile, pdfFile);
                    JOptionPane.showMessageDialog(this, "Image converted to PDF successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    if (ImageIO.write(img, formatComboBox.getSelectedItem().toString(), outputFile)) {
                        JOptionPane.showMessageDialog(this, "Image converted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Error converting image", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error converting image", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No image loaded", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
