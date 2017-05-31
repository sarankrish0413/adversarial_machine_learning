package com.aml.spamfilter.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Takes in the index file that has classifification for each email stating
 * if its spam or ham and converts them into a map.
 *
 * Created by saranyakrishnan on 5/21/17.
 */
class EmailClassifier {

    /** Location of index file where we have a list of all email file names and its corresponding class (spam or ham) */
    private String emailClassificationIndexFile;

    public EmailClassifier withEmailClassificationIndexFile(String emailClassificationIndexFile) {
        this.emailClassificationIndexFile = emailClassificationIndexFile;
        return this;
    }

    public Map<String, String> getEmailClassificationFromIndexFile() throws IOException {
        Map<String, String> emailFileNameToClassification = new HashMap<>();
        /*
           emailClassificationIndexFile will be in this format
           inmail.1 spam
           inmail.2 ham
           inmail.3 spam
         */
        List<String> lines = Files.readAllLines(Paths.get(emailClassificationIndexFile));
        for (String line : lines) {
            String[] parts = line.split("\\s");
            emailFileNameToClassification.put(parts[1].toLowerCase(), parts[0].toLowerCase());
        }
        return emailFileNameToClassification;
    }

}
