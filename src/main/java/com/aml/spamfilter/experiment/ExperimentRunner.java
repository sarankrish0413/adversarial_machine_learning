package com.aml.spamfilter.experiment;

import com.aml.spamfilter.common.DatasetCreator;
import com.aml.spamfilter.common.DatasetPoisoner;
import com.aml.spamfilter.graphs.VisualGraphCreator;
import com.aml.spamfilter.weightedbagging.HistogramWeightEstimator;
import com.aml.spamfilter.weightedbagging.KernelWeightEstimator;
import com.aml.spamfilter.weightedbagging.WeightEstimator;
import com.aml.spamfilter.weightedbagging.WeightedBagging;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the main component which I use to run the experiment for various inputs.
 *
 * Created by saranyakrishnan on 5/20/17.
 */
public class ExperimentRunner {

    /** Location of file that has featureset generated by FeatureSetCreator which is selected subset of salearn (spamassasin) tokens*/
    private String featureSetFileLocation;

    /** Location of index file where we have a list of all email file names and its corresponding class (spam or ham) */
    private String emailClassificationIndexFile;

    /** Number of emails we want to choose */
    private int emailCount;

    /** Location of folder that has all the emails */
    private String emailFolderLocation;

    /** Name of outputDatasetFileName */
    private String outputDatasetFileName;

    /** The location of file containing all good (ham) words. This will be used to poison the dataset */
    private String goodWordsFileLocation;

    /** Test dataset file (should be arff */
    private String testDatasetFileLocation;

    /** Temp folder to copy emails and poison */
    private String tempFolderToPoisonDataset;



    public static void main(String[] commandLineArguments) throws Exception {
        ExperimentRunner experimentRunner = new ExperimentRunner()
                .withEmailFolderLocation(commandLineArguments[0])
                .withFeatureSetFileLocation(commandLineArguments[1])
                .withEmailClassificationIndexFile(commandLineArguments[2])
                .withEmailCount(Integer.parseInt(commandLineArguments[3]))
                .withOutputDataSetFileName(commandLineArguments[4])
                .withGoodWordsFileLocation(commandLineArguments[5])
                .withTestDatasetFileLocation(commandLineArguments[6])
                .withTempFolderToPoisonDataset(commandLineArguments[7]);

        experimentRunner.createDataSet();

        Map<String, List<Double>> results = experimentRunner.runExperiment();

        System.out.println("AUC Values = [" + results + "]");

        experimentRunner.chartResults(results);
    }

    public ExperimentRunner withEmailFolderLocation(String emailFolderLocation) {
        this.emailFolderLocation = emailFolderLocation;
        return this;
    }

    public ExperimentRunner withFeatureSetFileLocation(String featureSetFileLocation) {
        this.featureSetFileLocation = featureSetFileLocation;
        return this;
    }

    public ExperimentRunner withEmailClassificationIndexFile(String emailClassificationIndexFile) {
        this.emailClassificationIndexFile = emailClassificationIndexFile;
        return this;
    }

    public ExperimentRunner withEmailCount(int emailCount) {
        this.emailCount = emailCount;
        return this;
    }

    public ExperimentRunner withOutputDataSetFileName(String outputDatasetFileName) {
        this.outputDatasetFileName = outputDatasetFileName;
        return this;
    }

    private ExperimentRunner withGoodWordsFileLocation(String goodWordsFileLocation) {
        this.goodWordsFileLocation = goodWordsFileLocation;
        return this;
    }

    private ExperimentRunner withTestDatasetFileLocation(String testDatasetFileLocation) {
        this.testDatasetFileLocation = testDatasetFileLocation;
        return this;
    }

    private ExperimentRunner withTempFolderToPoisonDataset(String tempFolderToPoisonDataset) {
        this.tempFolderToPoisonDataset = tempFolderToPoisonDataset;
        return this;
    }

    private void createDataSet() {
        new DatasetCreator()
                .withDatasetName("aml_course_project_data_set")
                .withEmailFolderLocation(emailFolderLocation)
                .withTokensFileLocation(featureSetFileLocation) // featurset is subset of tokens learnt from salearn (spamassasin)
                .withEmailClassificationIndexFile(emailClassificationIndexFile)
                .withEmailCount(emailCount)
                .withOutputDataSetFileName(outputDatasetFileName)
                .createDataSet();
    }

    public Map<String, List<Double>> runExperiment() throws Exception {
        // Measure of AUC for all probability density methods in experiment
        // AUC == Area under ROC Curve
        Map<String, List<Double>> probabilityDensityFunctionNameToAUC = new HashMap<>();
        probabilityDensityFunctionNameToAUC.put("histogram", new ArrayList<>()); // initialize with empty lists
        probabilityDensityFunctionNameToAUC.put("kernel_estimator", new ArrayList<>()); // initialize with empty lists

        File testDatasetFile = new File(testDatasetFileLocation);
        InputStream targetStream = new FileInputStream(testDatasetFile);
        Instances testDataset = new Instances(new BufferedReader(new InputStreamReader(targetStream, "UTF-8")));
        testDataset.setClassIndex(testDataset.numAttributes() - 1);

        for (int poisoningPercentage = 0; poisoningPercentage <= 40; poisoningPercentage += 4) {
            System.out.println("Poisoning " + poisoningPercentage + " percentage of dataset");
            DatasetPoisoner datasetPoisoner = new DatasetPoisoner()
                    .withEmailFolderLocationToPoison(emailFolderLocation)
                    .withPercentageToPoison(poisoningPercentage)
                    .withGoodWordsFileLocation(goodWordsFileLocation)
                    .withEmailClassificationIndexFile(emailClassificationIndexFile)
                    .withFeatureSetFileLocation(featureSetFileLocation)
                    .withEmailCount(emailCount)
                    .withTempFolderToPoisonDataset(tempFolderToPoisonDataset);

            Instances poisonedDataset = datasetPoisoner.poisonDataset();

            double aucForHistogram = computeAUCForHistogram(new Instances(poisonedDataset), testDataset);
            double aucForKernelEstimator = computeAUCForKernelEstimator(new Instances(poisonedDataset), testDataset);

            probabilityDensityFunctionNameToAUC
                    .get("histogram")
                    .add(aucForHistogram); // Add the value to list for plotting graph in future

            probabilityDensityFunctionNameToAUC
                    .get("kernel_estimator")
                    .add((aucForKernelEstimator)); // Add the value to list for plotting graph in future
        }

        return probabilityDensityFunctionNameToAUC;
    }

    private Double computeAUCForHistogram(Instances poisonedDataset, Instances testDataset) throws Exception {
        WeightEstimator weightEstimator = new HistogramWeightEstimator();



        WeightedBagging weightedBagging = new WeightedBagging()
                .withWeightEstimator(weightEstimator)
                .withDataset(poisonedDataset)
                .withTestDataset(testDataset);

        weightedBagging.classifySpamOrHam();

        return weightedBagging.computeAUC();
    }

    private Double computeAUCForKernelEstimator(Instances poisonedDataset, Instances testDataset) throws Exception {
        WeightEstimator weightEstimator = new KernelWeightEstimator();

        WeightedBagging weightedBagging = new WeightedBagging()
                .withWeightEstimator(weightEstimator)
                .withDataset(poisonedDataset)
                .withTestDataset(testDataset);

        weightedBagging.classifySpamOrHam();


        return weightedBagging.computeAUC();
    }

    private void chartResults(Map<String, List<Double>> results) {
        VisualGraphCreator visualGraphCreator = new VisualGraphCreator("Weighted Bagging (Ensemble Size = 10)")
                .withData(results)
                .withStepValue(4);
        visualGraphCreator.drawGraph();
    }

}
