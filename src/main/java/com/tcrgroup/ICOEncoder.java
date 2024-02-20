package com.tcrgroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ICOEncoder {

    public static void write(byte[] byteArray, File outputFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // Начало записи заголовка ICO-файла
            fos.write(new byte[]{0, 0, 1, 0, 1, 0});

            // Запись количества изображений (одно изображение в данном случае)
            fos.write(new byte[]{1, 0});

            // Получение размеров изображения
            int size = byteArray.length;

            // Запись размера данных изображения
            fos.write(new byte[]{(byte) size, (byte) (size >> 8), (byte) (size >> 16), (byte) (size >> 24)});

            // Запись данных изображения
            fos.write(byteArray);
        }
    }
}
