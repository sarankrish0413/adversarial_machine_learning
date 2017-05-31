package com.aml.spamfilter.common;

import org.junit.Test;
import weka.core.Instances;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * This tests the functionality of poisoning
 *
 * Created by saranyakrishnan on 5/31/17.
 */
public class DatasetPoisonerTest {
    private static final String TEST_DATASET_NAME = "testDataSet";
    private static final String TEST_EMAIL_FOLDER_LOCATION = "src/test/resources/data/";
    private static final String TEST_TOKENS_FILE_LOCATION = "src/test/resources/common/tokens.txt";
    private static final String TEST_EMAIL_CLASSIFICATION_INDEX_FILE = "src/test/resources/common/index";
    private static final int TEST_EMAIL_COUNT = 4;
    private static final String TEST_GOOD_WORDS_FILE_NAME = "src/test/resources/common/goodwords.txt";
    private static final String TEMP_FOLDER_TO_POISON = "src/test/resources/common/tmp/temp_folder_to_poison/";

    @Test
    public void testPoisonDataSet() throws IOException {
        // Arrange
        DatasetPoisoner datasetPoisoner = new DatasetPoisoner()
                .withEmailClassificationIndexFile(TEST_EMAIL_CLASSIFICATION_INDEX_FILE)
                .withEmailCount(TEST_EMAIL_COUNT)
                .withEmailFolderLocationToPoison(TEST_EMAIL_FOLDER_LOCATION)
                .withFeatureSetFileLocation(TEST_TOKENS_FILE_LOCATION)
                .withPercentageToPoison(40)
                .withGoodWordsFileLocation(TEST_GOOD_WORDS_FILE_NAME)
                .withTempFolderToPoisonDataset(TEMP_FOLDER_TO_POISON);
        List<String> expectedPoisonedInstanceData = Arrays.asList("0.0", "0.0", "1.0", "1.0", "1.0", "1.0", "1.0");

        // Act
        Instances poisonedDataset = datasetPoisoner.poisonDataset();

        // Assert
        assertThat(poisonedDataset.size(), is(3));
        assertThat(poisonedDataset.numAttributes(), is(8));
        IntStream.range(0, expectedPoisonedInstanceData.size()).forEach(i -> assertThat(poisonedDataset.get(0).value(i), is(Double.valueOf(expectedPoisonedInstanceData.get(i)))));
    }
}
