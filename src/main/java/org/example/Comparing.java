package org.example;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Text;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.example.Main.logger;


/*
This function does two things, it updates the document's date to today,
then it starts comparing the main document's text to the headers inside the map "updates".

If it finds a match, it compares the text inside the document to the values inside the map.
When it detects that they're different, the text from the map is written over the main document.
This repeats until "Turuväärtus" stops the loop.
It logs all the information that was changed, then saves the file as "modified_{filename}"
 */

public class Comparing {
    public static void CompareDocuments(String FileName, String KnowledgeBaseEconomy, String locationKnowledgeBase, String GeneralOverviews, String Location) {
        try {
            //Stays false until regex finds date and changes it to true.
            //Else it will stay looping until the end of the code.
            boolean newDateAdded = false;
            //Pattern the date is written as
            String newDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM.dd.yyyy"));

            Map<String, String> BaseEconomy = Extract.ExtractContents(new File(KnowledgeBaseEconomy), Location);
            Map<String, String> LocationFile = Extract.ExtractContents(new File(locationKnowledgeBase), Location);
            Map<String, String> OverviewsFile = Extract.ExtractContents(new File(GeneralOverviews), Location);

            //Combines all files into one for simpler looping
            Map<String, String> updates = new LinkedHashMap<>();
            updates.putAll(BaseEconomy);
            updates.putAll(LocationFile);
            updates.putAll(OverviewsFile);

            WordprocessingMLPackage wordPackage = WordprocessingMLPackage.load(new File(FileName));
            MainDocumentPart documentPart = wordPackage.getMainDocumentPart();
            List<Object> textNodes = documentPart.getJAXBNodesViaXPath("//w:t", true);

            String currentHeader = null;
            List<Text> sectionTexts = new ArrayList<>();

            for (Object obj : textNodes) {
                if (obj instanceof JAXBElement<?> elem) {
                    Object value = elem.getValue();

                    if (value instanceof Text textElement) {
                        String text = textElement.getValue();

                        //Stops the loop when finds Turuväärtus
                        if (text.startsWith("Turuväärtus")) break;

                        //Updates the documents date when NewDateAdded is false and kuupäev: is found within the text
                        if (!newDateAdded && text.contains("kuupäev:")) {
                            text = text.replaceAll("Hindamise kuupäev: \\s*\\d{2}\\.\\d{2}\\.\\d{4}", "Hindamise kuupäev: " + newDate);
                            textElement.setValue(text);
                            newDateAdded = true;
                        }

                        if (updates.containsKey(text)) {
                            if (currentHeader != null) {
                                String newText = updates.get(currentHeader);
                                for (Text t : sectionTexts) {
                                    String sourceFile = SourceFile.SourceFileGet(currentHeader, BaseEconomy, LocationFile, OverviewsFile, KnowledgeBaseEconomy, locationKnowledgeBase, GeneralOverviews);
                                    logger.info("Updating \"{}\"\n to \"{}\"\n from the file {}\n", t.getValue(), newText, sourceFile);
                                    t.setValue("");
                                }
                                if (!sectionTexts.isEmpty()) {
                                    sectionTexts.getFirst().setValue(newText);
                                }
                            }
                            currentHeader = text;
                            sectionTexts.clear();
                            continue;
                        }

                        //Collects all sections for the current line
                        if (currentHeader != null) {
                            sectionTexts.add(textElement);
                        }
                    }
                }
            }
            if (currentHeader != null && updates.containsKey(currentHeader)) {
                String newText = updates.get(currentHeader);
                for (Text t : sectionTexts) {
                    String sourceFile = SourceFile.SourceFileGet(currentHeader, BaseEconomy, LocationFile, OverviewsFile, KnowledgeBaseEconomy, locationKnowledgeBase, GeneralOverviews);
                    logger.info("Updating \"{}\"\n to \"{}\"\n from the file {}\n", t.getValue(), newText, sourceFile);
                    t.setValue("");
                }
                if (!sectionTexts.isEmpty()) {
                    sectionTexts.getFirst().setValue(newText);
                }
            }

            String outputName = "modified_" + new File(FileName).getName();
            logger.info("Updating document's valuation date to {}\n", newDate);
            logger.info("Saving modified file with new data as {}\n", outputName);
            wordPackage.save(new File(outputName));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
