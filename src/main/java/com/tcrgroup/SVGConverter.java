package com.tcrgroup;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SVGConverter {

    public static void convertToSVG(BufferedImage image, File outputFile) throws IOException {
        DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
        Document document = domImpl.createDocument(null, "svg", null);
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        Graphics2D g2d = svgGenerator;
        g2d.drawImage(image, 0, 0, null);

        try (FileWriter writer = new FileWriter(outputFile)) {
            svgGenerator.stream(writer, true);
        }
    }
}
