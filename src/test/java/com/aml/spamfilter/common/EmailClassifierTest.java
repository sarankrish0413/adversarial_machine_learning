package com.aml.spamfilter.common;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;


public class EmailClassifierTest {
    private static final String TEST_EMAIL_CLASSIFICATION_INDEX_FILE = "src/test/resources/common/index";

    @Test
    public void testGetEmailClassificationFromIndexFile() throws IOException {
        // Arrange
        EmailClassifier emailClassifier = new EmailClassifier().withEmailClassificationIndexFile(TEST_EMAIL_CLASSIFICATION_INDEX_FILE);

        // Act
        Map<String, String> emailClassificationFromIndexFile = emailClassifier.getEmailClassificationFromIndexFile();

        // Assert
        assertThat(emailClassificationFromIndexFile, hasEntry("inmail.1", "spam"));
        assertThat(emailClassificationFromIndexFile, hasEntry("inmail.2", "ham"));
        assertThat(emailClassificationFromIndexFile, hasEntry("inmail.3", "spam"));
        assertThat(emailClassificationFromIndexFile, hasEntry("inmail.4", "spam"));
    }
}
