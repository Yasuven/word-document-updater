package org.example;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.example.Main.logger;

/*
This function extracts all the text after the hard-coded headers:
First it looks for the hard-coded header.
Then it grabs all the text
and stores the header as a key and the text as the value.
It Returns the map.
 */

public class Extract {
    public static Map<String, String> ExtractContents(File docx, String Location) {
        Map<String, String> Contents = new LinkedHashMap<>();

        try {
            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(docx);
            MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
            List<Object> textNodes = documentPart.getJAXBNodesViaXPath("//w:t", true);

            StringBuilder currentSectionText = new StringBuilder();
            String currentHeader = null;
            for (Object obj : textNodes) {

                if (obj instanceof JAXBElement<?> elem) {
                    Object value = elem.getValue();

                    if (value instanceof Text textElement) {
                        String currentText = textElement.getValue();
                        if (currentText.equals("Makromajanduslik taust")) {
                            if (currentHeader != null) {
                                Contents.put(currentHeader, currentSectionText.toString().trim());
                            }
                            currentHeader = currentText;
                            currentSectionText.setLength(0);
                        } else if (currentText.equals("Eesti kinnisvaraturg")) {
                            if (currentHeader != null) {
                                Contents.put(currentHeader, currentSectionText.toString().trim());
                            }
                            currentHeader = currentText;
                            currentSectionText.setLength(0);
                        } else if (Location.equals("Õismäe") && currentText.equals("Õismäe linnaosa korteriturg")) {
                            if (currentHeader != null) {
                                Contents.put(currentHeader, currentSectionText.toString().trim());
                            }
                            currentHeader = currentText;
                            currentSectionText.setLength(0);
                        } else if (Location.equals("Kristiine") && currentText.equals("Kristiine linnaosa korteriturg")) {
                            if (currentHeader != null) {
                                Contents.put(currentHeader, currentSectionText.toString().trim());
                            }
                            currentHeader = currentText;
                            currentSectionText.setLength(0);
                        } else {
                            if (currentHeader != null) {
                                currentSectionText.append(currentText).append(" ");
                            }
                        }
                    }
                }
            }
            if (currentHeader != null) {
                logger.info("Saving key {}, from {}\n as value \"{}\"\n", currentHeader, docx, currentSectionText.toString().trim());
                Contents.put(currentHeader, currentSectionText.toString().trim());
            }
            return Contents;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
