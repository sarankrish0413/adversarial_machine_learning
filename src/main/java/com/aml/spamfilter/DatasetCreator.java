package com.aml.spamfilter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
        @relation trec2007

        @attribute "generic" {false, true}
        @attribute "cialis" {false, true}
        @attribute "branded" {false, true}
        @attribute "feel" {false, true}
        @attribute "spam_or_ham_class" {spam, ham}

        @data
        false,false,true,true,spam
        false,false,false,false,ham
        false,false,false,false,spam
        false,false,false,false,spam
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
        EmailClassifier emailClassifier = new EmailClassifier()
                .withEmailClassificationIndexFile(emailClassificationIndexFile);
        Map<String, String> emailFileNameToClassification = emailClassifier.getEmailClassificationFromIndexFile();

        for (int i = 1; i <= emailCount; i++) {
            String emailFileName = "inmail." + i;
            addDataRecord(emailFileName, printWriter, tokens, emailFileNameToClassification);

            // Show progress on count of emails processed
            if (i % 100 == 0) {
                System.out.println(i + " emails processed.");
            }
        }
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
