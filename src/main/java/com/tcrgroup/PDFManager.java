package com.tcrgroup;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFManager {
    public static void convertToPDF(File imageFile, File pdfFile) throws IOException {
        try {
            Document document = new Document();
            FileOutputStream fos = new FileOutputStream(pdfFile);
            PdfWriter.getInstance(document, fos);
            document.open();

            Image image = Image.getInstance(imageFile.getPath());
            image.scaleToFit(document.getPageSize());
            document.add(image);

            document.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
