package com.aml.spamfilter.common;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;


public class DatasetCreatorTest {

    private static final String TEST_DATASET_NAME = "testDataSet";
    private static final String TEST_EMAIL_FOLDER_LOCATION = "src/test/resources/data/";
    private static final String TEST_TOKENS_FILE_LOCATION = "src/test/resources/common/tokens.txt";
    private static final String TEST_EMAIL_CLASSIFICATION_INDEX_FILE = "src/test/resources/common/index";
    private static final int TEST_EMAIL_COUNT = 4;
    private static final String TEST_OUTPUT_DATASET_FILE_NAME = "dataset.txt";
    private static final String EXPECTED_OUTPUT_DATASET_FILE_NAME = "src/test/resources/common/expected_dataset.txt";

    @Before
    public void setUp() throws IOException {
        boolean status = Files.deleteIfExists(new File(TEST_OUTPUT_DATASET_FILE_NAME).toPath());
        if (status) {
            System.out.println("Cleaning up previous test run outputs (" + TEST_OUTPUT_DATASET_FILE_NAME + ") as part of setup");
        }
    }

    @Test
    public void testCreateDataSet() throws IOException {
        // Arrange
        DatasetCreator datasetCreator = new DatasetCreator()
                .withDatasetName(TEST_DATASET_NAME)
                .withEmailFolderLocation(TEST_EMAIL_FOLDER_LOCATION)
                .withTokensFileLocation(TEST_TOKENS_FILE_LOCATION)
                .withEmailClassificationIndexFile(TEST_EMAIL_CLASSIFICATION_INDEX_FILE)
                .withEmailCount(TEST_EMAIL_COUNT)
                .withOutputDataSetFileName(TEST_OUTPUT_DATASET_FILE_NAME);

        // Act
        datasetCreator.createDataSet();

        // Assert
        assertEquals("The generated dataset does not match expected dataset !",
                FileUtils.readFileToString(new File(EXPECTED_OUTPUT_DATASET_FILE_NAME), "utf-8").trim(),
                FileUtils.readFileToString(new File(TEST_OUTPUT_DATASET_FILE_NAME), "utf-8").trim());
    }
}
