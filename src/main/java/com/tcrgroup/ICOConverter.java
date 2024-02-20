package com.tcrgroup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class ICOConverter {

    public static void convertToICO(BufferedImage image, File outputFile) throws IOException {
        // Создание массива байтов для хранения изображения
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteStream); // Запись изображения в формат PNG

        // Запись массива байтов в файл ICO
        byte[] byteArray = byteStream.toByteArray();
        ICOEncoder.write(byteArray, outputFile);
    }
}
