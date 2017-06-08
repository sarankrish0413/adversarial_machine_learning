package com.aml.spamfilter.experiment;


import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ExperimentRunnerTest {

    private static final String TEST_EMAIL_FOLDER_LOCATION = "src/test/resources/experiment/data/";
    private static final String TEST_GOOD_WORDS_FILE_LOCATION = "src/test/resources/experiment/goodwords.txt";
    private static final String TEST_EMAIL_CLASSIFICATION_INDEX_FILE = "src/test/resources/experiment/index";
    private static final String TEST_OUTPUT_DATASET_FILE = "src/test/resources/experiment/outputdataset.arff";
    private static final String TEST_DATASET_FILE = "src/test/resources/experiment/testdataset.arff";
    private static final int TEST_EMAIL_COUNT = 12;
    private static final String TEST_FEATURESET_FILE_LOCATION = "src/test/resources/experiment/featureset.txt";

    @Test
    public void testRunExperiment() throws Exception {
        // Arrange
        ExperimentRunner experimentRunner = new ExperimentRunner()
                .withEmailFolderLocation(TEST_EMAIL_FOLDER_LOCATION)
                .withFeatureSetFileLocation(TEST_FEATURESET_FILE_LOCATION)
                .withEmailClassificationIndexFile(TEST_EMAIL_CLASSIFICATION_INDEX_FILE)
                .withEmailCount(TEST_EMAIL_COUNT)
                .withOutputDataSetFileName(TEST_OUTPUT_DATASET_FILE)
                .withGoodWordsFileLocation(TEST_GOOD_WORDS_FILE_LOCATION)
                .withTestDatasetFileLocation(TEST_DATASET_FILE)
                .withTempFolderToPoisonDataset("src/test/resources/experiment/tmp/");

        // Act
        experimentRunner.createDataSet();

        Map<String, List<Double>> results = experimentRunner.runExperiment();

        // Assert
        assertTrue(results.containsKey("histogram"));
        assertTrue(results.containsKey("kernel_estimator"));
        assertTrue(results.containsKey("knn_estimator"));
    }

}
