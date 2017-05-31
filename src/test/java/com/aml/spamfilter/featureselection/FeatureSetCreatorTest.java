package com.aml.spamfilter.featureselection;


import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class FeatureSetCreatorTest {

    private static final String TEST_EMAIL_FOLDER_LOCATION = "src/test/resources/data/";
    private static final String TEST_TOKENS_FILE_LOCATION = "src/test/resources/featureselection/tokens.txt";
    private static final String TEST_EMAIL_CLASSIFICATION_INDEX_FILE = "src/test/resources/featureselection/index";
    private static final int TEST_EMAIL_COUNT = 4;
    private static final String TEST_FEATURESET_OUTPUT_FILE_LOCATION = "featureset.txt";
    private static final int TEST_OUTPUT_FEATURESET_COUNT = 1;
    private static final String EXPECTED_OUTPUT_FEATURESET_FILE_NAME = "src/test/resources/featureselection/expected_featureset.txt";

    @Before
    public void setUp() throws IOException {
        boolean status = Files.deleteIfExists(new File(TEST_FEATURESET_OUTPUT_FILE_LOCATION).toPath());
        if (status) {
            System.out.println("Cleaning up previous test run outputs (" + TEST_FEATURESET_OUTPUT_FILE_LOCATION + ") as part of setup");
        }
    }

    @Test
    public void testCreateFeatureSet() throws Exception {
        // Arrange
        FeatureSetCreator featureSetCreator = new FeatureSetCreator()
                .withEmailFolderLocation(TEST_EMAIL_FOLDER_LOCATION)
                .withTokensFileLocation(TEST_TOKENS_FILE_LOCATION)
                .withFeatureSetOutputFileLocation(TEST_FEATURESET_OUTPUT_FILE_LOCATION)
                .withEmailClassificationIndexFile(TEST_EMAIL_CLASSIFICATION_INDEX_FILE)
                .withEmailCount(TEST_EMAIL_COUNT)
                .withOutputFeatureSetCount(TEST_OUTPUT_FEATURESET_COUNT);

        // Act
        featureSetCreator.createFeatureSet();

        // Assert
        assertEquals("The generated featureset does not match expected featureset !",
                FileUtils.readFileToString(new File(EXPECTED_OUTPUT_FEATURESET_FILE_NAME), "utf-8"),
                FileUtils.readFileToString(new File(TEST_FEATURESET_OUTPUT_FILE_LOCATION), "utf-8"));

    }

}
