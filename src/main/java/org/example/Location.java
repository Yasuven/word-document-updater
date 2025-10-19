package org.example;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
This function extracts all the text inside the main document
Splits it using whitespaces then adds it all into an existing list
returns into Main.java where it is used to search for the location
 */

public class Location {
    public static List<String> LocationFinder(File docx) {
        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(docx);
            MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
            List<Object> textNodes = documentPart.getJAXBNodesViaXPath("//w:t", true);

            List<String> words = new ArrayList<>();
            for (Object obj : textNodes) {

                if (obj instanceof JAXBElement<?> elem) {
                    Object value = elem.getValue();

                    if (value instanceof Text textElement) {
                        String currentText = textElement.getValue();

                        words.addAll(Arrays.asList(currentText.split("\\s+")));
                    }
                }
            }
            return words;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
