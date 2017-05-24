package com.aml.spamfilter;

import org.apache.commons.io.FileUtils;
import weka.core.Instances;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * This class takes in the dataset, good words (ham), poisoning percentage
 * and poisons the dataset.
 *
 * Created by saranyakrishnan on 5/21/17.
 */
public class DatasetPoisoner {

    /** temp folder to copy all emails and poison */
    private static final String TEMP_FOLDER = "/tmp/temp_folder_to_delete/";

    /** Location of emails folder */
    private String emailFolderLocationToPoison;

    /** percentage of emails to poison */
    private int poisoningPercentage;

    /** Location of file that contains all good (ham) words */
    private String goodWordsFileLocation;

    /** Location of file that has the classification of emailfilename -> (spam of ham) */
    private String emailClassificationIndexFile;

    /** Location of file that has the list of attributes (features) */
    private String featureSetFileLocation;

    /** Count of emails to poison */
    private int emailCount;

    public DatasetPoisoner withEmailFolderLocationToPoison(String emailFolderLocationToPoison) {
        this.emailFolderLocationToPoison = emailFolderLocationToPoison;
        return this;
    }

    public DatasetPoisoner withPercentageToPoison(int poisoningPercentage) {
        this.poisoningPercentage = poisoningPercentage;
        return this;
    }

    public DatasetPoisoner withGoodWordsFileLocation(String goodWordsFileLocation) {
        this.goodWordsFileLocation = goodWordsFileLocation;
        return this;
    }

    public DatasetPoisoner withEmailClassificationIndexFile(String emailClassificationIndexFile) {
        this.emailClassificationIndexFile = emailClassificationIndexFile;
        return this;
    }

    public DatasetPoisoner withFeatureSetFileLocation(String featureSetFileLocation) {
        this.featureSetFileLocation = featureSetFileLocation;
        return this;
    }

    public DatasetPoisoner withEmailCount(int emailCount) {
        this.emailCount = emailCount;
        return this;
    }

    public Instances poisonDataset() throws IOException {
        deleteTempDirectory();
        copyAllEmailsToTempDirectory();

        EmailClassifier emailClassifier = new EmailClassifier().withEmailClassificationIndexFile(emailClassificationIndexFile);
        Map<String, String> emailFileNameToClassification = emailClassifier.getEmailClassificationFromIndexFile();

        List<String> goodWords = Files.readAllLines(Paths.get(goodWordsFileLocation));
        int countOFEmailsToPoison = (int) (((poisoningPercentage * 1.0) / 100) * emailCount);

        int emailIndex = 1;
        int emailsPoisoned = 0;
        while ((emailsPoisoned < countOFEmailsToPoison) && (emailIndex < emailCount)) {
            String emailFileName = TEMP_FOLDER + "inmail." + emailIndex;
            if (emailFileNameToClassification.get("inmail." + emailIndex).equals("spam")) { // Only poison spam emails, ignore ham
                File emailToPoison = new File(emailFileName);
                FileUtils.writeLines(emailToPoison, goodWords);
                emailsPoisoned++;
            }
            emailIndex++;
        }

        Instances poisonedDataSet = createDatasetFromTempFolder();
        poisonedDataSet.setClassIndex(poisonedDataSet.numAttributes() - 1);
        deleteTempDirectory(); // Cleanup for safety
        return poisonedDataSet;
    }

    private Instances createDatasetFromTempFolder() throws IOException {
        new DatasetCreator()
                .withDatasetName("poisoned_data_set_with" + poisoningPercentage + "_percentage")
                .withEmailFolderLocation(TEMP_FOLDER)
                .withTokensFileLocation(featureSetFileLocation) // featurset is subset of tokens learnt from salearn (spamassasin)
                .withEmailClassificationIndexFile(emailClassificationIndexFile)
                .withEmailCount(emailCount)
                .withOutputDataSetFileName("temp_dataset.txt")
                .createDataSet();

        File tempDataset = new File("temp_dataset.txt");
        InputStream targetStream = new FileInputStream(tempDataset);
        Instances data = new Instances(new BufferedReader(new InputStreamReader(targetStream, "UTF-8")));
        FileUtils.forceDelete(tempDataset); // The dataset file is not needed anymore as its in data variable now
        return data;
    }

    private void copyAllEmailsToTempDirectory() throws IOException {
        FileUtils.copyDirectory(new File(emailFolderLocationToPoison), new File(TEMP_FOLDER));
    }

    private void deleteTempDirectory() throws IOException {
        FileUtils.deleteDirectory(new File(TEMP_FOLDER));
    }
}
