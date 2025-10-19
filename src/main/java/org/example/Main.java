package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        //Map of locations that have filenames as keys
        Map<String, String> knowledgeBaseMap = Map.of("Õismäe", "teadmistebaas_õismäe_2025.docx", "Kristiine", "teadmistebaas_kristiine_2025.docx");

        String GeneralOverviews = "teadmistebaas_Üldülevaated_Eesti_Tln_Harjumaa_2025.docx";
        String KnowledgeBaseEconomy = "teadmistebaas_majandus_2025.docx";
        String FileName = "TN Järveotsa tee 25-15 Õismäe.docx";
        //String FileName = "TN Tulika tn 20-5 Kristiine.docx";
        Set<String> allowedLocations = Set.of("Õismäe", "Kristiine");

        logger.info("Opening file with name {}.", FileName);

        List<String> MainFile = Location.LocationFinder(new File(FileName));
        String Location = null;

        //Checking for the location of the house/apartment
        for (String word : MainFile) {
            if (allowedLocations.contains(word)) {
                Location = word;
                break;
            }
        }

        //If the map doesn't contain the location that is inside the document
        //Exit program
        if (Location == null) {
            logger.info("Location not found, exiting program.");
            System.exit(0);
        }

        String locationKnowledgeBase = knowledgeBaseMap.get(Location);

        Comparing.CompareDocuments(FileName, KnowledgeBaseEconomy, locationKnowledgeBase, GeneralOverviews, Location);
    }
}