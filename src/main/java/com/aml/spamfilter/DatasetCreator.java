package com.aml.spamfilter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Creates a dataset in weka format
 *
 * @author saranyakrishnan
 */
public class DatasetCreator {

    /** Dataset name */
    private String datasetName;

    /** Location of file that has list of tokens learnt by spamassasin sa-lear */
    private String tokensFileLocation;

    /** Location of index file where we have a list of all email file names and its corresponding class (spam or ham) */
    private String emailClassificationIndexFile;

    /** Count of emails we want to choose */
    private int emailCount;

    /** Location of folder that has all the emails */
    private String emailFolderLocation;

    /** Output dataset file name */
    private String outputDatasetFileName;


    /*
        Example file format
        --------------------
        @relation datasetname

        @attribute token1 numeric
        @attribute token2 numeric
        @attribute token3 {val1, val2, val3}
        @attribute token4 {TRUE, FALSE}
        @attribute token5 {yes, no}

        @data
        85,85,val1,FALSE,no
        80,90,val2,TRUE,no
        83,86,val3,FALSE,yes

    */

    public void createDataSet() {
        try (PrintWriter printWriter = new PrintWriter(new File(outputDatasetFileName))) {
            // Header
            printWriter.println("@relation " + datasetName);
            printWriter.println();

            // Get the list of tokens from token file and add it as attributes to output
            List<String> tokens = addAttributes(printWriter);
            printWriter.println();

            // Add data records
            addData(printWriter, tokens);
            printWriter.println();
        } catch (IOException e) {
            throw new RuntimeException("Cannot create dataset file", e);
        }
    }

    private List<String> addAttributes(PrintWriter printWriter) throws IOException {
        List<String> tokens = Files.readAllLines(Paths.get(tokensFileLocation));
        tokens.stream().forEach(token -> printWriter.println("@attribute \"" + token.replace("\"", "\\\"") + "\" {false, true}"));
        printWriter.println("@attribute \"spam_or_ham_class\" {spam, ham}");
        return tokens;
    }

    private void addData(PrintWriter printWriter, List<String> tokens) throws IOException {
        printWriter.println("@data");

        // Example
        // inmail.1 -> spam
        // inmail.2 -> ham
        Map<String, String> emailFileNameToClassification = getEmailClassificationFromIndexFile();

        for (int i = 1; i <= emailCount; i++) {
            String emailFileName = "inmail." + i;
            addDataRecord(emailFileName, printWriter, tokens, emailFileNameToClassification);

            // Show progress on count of emails processed
            if (i % 100 == 0) {
                System.out.println(i + " emails processed.");
            }
        }
    }

    private Map<String, String> getEmailClassificationFromIndexFile() throws IOException {
        Map<String, String> emailFileNameToClassification = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(emailClassificationIndexFile));
        for (String line : lines) {
            String[] parts = line.split("\\s");
            emailFileNameToClassification.put(parts[1].toLowerCase(), parts[0].toLowerCase());
        }
        return emailFileNameToClassification;
    }

    private void addDataRecord(String emailFileName, PrintWriter printWriter, List<String> tokens, Map<String, String> emailFileNameToClassification) throws IOException {
        String emailContent = FileUtils.readFileToString(new File(emailFolderLocation + emailFileName), StandardCharsets.UTF_8);
        tokens.stream().forEach(token -> printWriter.print(emailContent.contains(token) + ","));
        printWriter.print(emailFileNameToClassification.get(emailFileName) + "\n");
    }

    public DatasetCreator withEmailFolderLocation(String emailFolderLocation) {
        this.emailFolderLocation = emailFolderLocation;
        return this;
    }

    public DatasetCreator withTokensFileLocation(String tokensFileLocation) {
        this.tokensFileLocation = tokensFileLocation;
        return this;
    }

    public DatasetCreator withEmailClassificationIndexFile(String emailClassificationIndexFile) {
        this.emailClassificationIndexFile = emailClassificationIndexFile;
        return this;
    }

    public DatasetCreator withEmailCount(int emailCount) {
        this.emailCount = emailCount;
        return this;
    }

    public DatasetCreator withOutputDataSetFileName(String outputDatasetFileName) {
        this.outputDatasetFileName = outputDatasetFileName;
        return this;
    }

    public DatasetCreator withDatasetName(String datasetName) {
        this.datasetName = datasetName;
        return this;
    }

}
