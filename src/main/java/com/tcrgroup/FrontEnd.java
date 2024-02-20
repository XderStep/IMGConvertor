package com.tcrgroup;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

public class FrontEnd extends JFrame {
    private JLabel imageLabel;
    private JLabel logoLabel;
    private JComboBox<String> formatComboBox;
    private File outputDir;
    private File inputFile;
    private String lastFormat;
    private JPanel conversionPanel;
    private JPanel inputPanel;
    private JTextField xTextField;
    private JTextField yTextField;
    private boolean isImageLoaded = false;

    public FrontEnd() {
        setTitle("Image Converter");
        setSize(1000, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Установка положения окна по центру экрана
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screenSize.width - this.getWidth()) / 2;
        int y = (screenSize.height - this.getHeight()) / 2;
        this.setLocation(x, y);

        // Основная панель с разделителем
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setResizeWeight(0.2);

        // Левая часть: разделена на верхнюю часть с логотипом и нижнюю часть с кнопками конвертации
        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftSplitPane.setResizeWeight(0.7);

        // Верхняя часть левой части: панель с логотипом
        JPanel topPanel = new JPanel(new BorderLayout());
        logoLabel = new JLabel();
        ImageIcon logoIcon = new ImageIcon("logo.png"); // Путь к логотипу
        logoLabel.setIcon(logoIcon);
        topPanel.add(logoLabel, BorderLayout.CENTER);
        leftSplitPane.setTopComponent(topPanel);

        // Нижняя часть левой части: панель с полями X и Y и кнопками конвертации
        JPanel bottomLeftPanel = new JPanel(new BorderLayout());
        inputPanel = new JPanel(new GridLayout(3, 2));
        JLabel xLabel = new JLabel("X:");
        xTextField = new JTextField();
        JLabel yLabel = new JLabel("Y:");
        yTextField = new JTextField();
        inputPanel.add(xLabel);
        inputPanel.add(xTextField);
        inputPanel.add(yLabel);
        inputPanel.add(yTextField);
        bottomLeftPanel.add(inputPanel, BorderLayout.NORTH);

        conversionPanel = new JPanel(new GridLayout(7, 1, 5, 5));
        JButton jpgButton = new JButton("JPG");
        jpgButton.addActionListener(e -> {
            lastFormat = "JPG";
        });
        conversionPanel.add(jpgButton);
        JButton jpegButton = new JButton("JPEG");
        jpegButton.addActionListener(e -> {
            lastFormat = "JPEG";
        });
        conversionPanel.add(jpegButton);
        JButton pdfButton = new JButton("PDF");
        pdfButton.addActionListener(e -> {
            lastFormat = "PDF";
        });
        conversionPanel.add(pdfButton);
        JButton pngButton = new JButton("PNG");
        pngButton.addActionListener(e -> {
            lastFormat = "PNG";
        });
        conversionPanel.add(pngButton);
        JButton icoButton = new JButton("ICO");
        icoButton.addActionListener(e -> {
            lastFormat = "ICO";
        });
        conversionPanel.add(icoButton);
        JButton svgButton = new JButton("SVG");
        svgButton.addActionListener(e -> {
            lastFormat = "SVG";
        });
        conversionPanel.add(svgButton);
        JButton bmpButton = new JButton("BMP");
        bmpButton.addActionListener(e -> {
            lastFormat = "BMP";
        });
        conversionPanel.add(bmpButton);
        bottomLeftPanel.add(conversionPanel, BorderLayout.SOUTH);

        leftSplitPane.setBottomComponent(bottomLeftPanel);
        mainSplitPane.setLeftComponent(leftSplitPane);

        // Правая часть: разделена на верхнюю часть с полем Drag&Drop и нижнюю часть с кнопкой "Convert" и прогресс-баром
        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        rightSplitPane.setResizeWeight(0.8);

        JPanel dropPanelRight = new JPanel(new BorderLayout());
        imageLabel = new JLabel("Перетащите файл", SwingConstants.CENTER);
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
                        isImageLoaded = true;
                        toggleControlsVisibility();
                    }
                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                }
                return true;
            }
        });
        imageLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dropPanelRight.add(imageLabel, BorderLayout.CENTER);
        rightSplitPane.setTopComponent(dropPanelRight);

        JPanel bottomRightPanel = new JPanel(new BorderLayout());

        JPanel convertPanel = new JPanel(new BorderLayout());
        convertPanel.setBorder(BorderFactory.createEmptyBorder(25, 5, 25, 5));
        JButton convertButton = new JButton("Convert");
        convertButton.setPreferredSize(new Dimension(100, 40));
        convertPanel.add(convertButton, BorderLayout.CENTER);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 25, 5));
        bottomRightPanel.add(progressBar, BorderLayout.CENTER);
        progressBar.setPreferredSize(new Dimension(10, 2));

        progressBar.setStringPainted(true);

        bottomRightPanel.add(convertPanel, BorderLayout.NORTH);
        rightSplitPane.setBottomComponent(bottomRightPanel);

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isImageLoaded) {
                    progressBar.setValue(0);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            int progress = 0;
                            while (progress < 100) {
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException ex) {
                                    ex.printStackTrace();
                                }
                                progress += 5;
                                progressBar.setValue(progress);
                            }
                            convertImage();
                            progressBar.setValue(0);
                        }
                    }).start();
                } else {
                    JOptionPane.showMessageDialog(null, "Перетащите изображение для конвертации", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainSplitPane.setRightComponent(rightSplitPane);
        add(mainSplitPane);

        setVisible(true);
        toggleControlsVisibility();
    }

    private void toggleControlsVisibility() {
        inputPanel.setVisible(isImageLoaded);
        conversionPanel.setVisible(isImageLoaded);
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
            JOptionPane.showMessageDialog(this, "Не удалось загрузить изображение", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void convertImage() {
        if (inputFile != null && lastFormat != null) {
            String outputDirPath = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "IMGConverter";
            File outputDir = new File(outputDirPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            String outputFileName = inputFile.getName().replaceFirst("[.][^.]+$", "") + "." + lastFormat.toLowerCase();
            File outputFile = new File(outputDirPath, outputFileName);

            try {
                BufferedImage inputImage = ImageIO.read(inputFile);
                int width = Integer.parseInt(xTextField.getText());
                int height = Integer.parseInt(yTextField.getText());
                BufferedImage convertedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g2d = convertedImage.createGraphics();
                g2d.drawImage(inputImage, 0, 0, width, height, null);
                g2d.dispose();

                if (lastFormat.equalsIgnoreCase("PDF")) {
                    Document document = new Document();
                    PdfWriter.getInstance(document, new FileOutputStream(outputFile));
                    document.open();
                    Image pdfImage = Image.getInstance(convertedImage, null);
                    document.add(pdfImage);
                    document.close();
                } else if (lastFormat.equalsIgnoreCase("SVG")) {
                    SVGConverter.convertToSVG(convertedImage, outputFile);
                } else if (lastFormat.equalsIgnoreCase("ICO")) {
                    ICOConverter.convertToICO(convertedImage, outputFile);
                } else {
                    ImageIO.write(convertedImage, lastFormat, outputFile);
                }

                JOptionPane.showMessageDialog(null, "Файл успешно сконвертирован и сохранен в " + outputFile.getAbsolutePath(), "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException | DocumentException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Не удалось сконвертировать файл", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(FrontEnd::new);
    }
}
