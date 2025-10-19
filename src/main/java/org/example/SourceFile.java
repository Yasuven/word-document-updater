package org.example;

import java.io.File;
import java.util.Map;


/*
Checks which document contains the header
returns the name of the document which contains the header

This function is for extracting information for logging
 */
public class SourceFile {
    public static String SourceFileGet(String header, Map<String, String> BaseEconomy, Map<String, String> LocationFile, Map<String, String> OverviewsFile, String KnowledgeBaseEconomy, String locationKnowledgeBase, String GeneralOverviews) {
        String sourceFile = "";

        if (BaseEconomy.containsKey(header)) {
            sourceFile = new File(KnowledgeBaseEconomy).getName();
        } else if (LocationFile.containsKey(header)) {
            sourceFile = new File(locationKnowledgeBase).getName();
        } else if (OverviewsFile.containsKey(header)) {
            sourceFile = new File(GeneralOverviews).getName();
        }
        return sourceFile;
    }
}

