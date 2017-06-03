package com.aml.spamfilter.weightedbagging;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.meta.Bagging;
import weka.core.Instances;

/**
 * Created by saranyakrishnan on 5/21/17.
 */
public class WeightedBagging {

    /** The type of estimator to use (kernel, historgram..etc) */
    private WeightEstimator weightEstimator;

    /** The training Dataset */
    private Instances dataset;

    /** The test data set */
    private Instances testDataset;

    /** Weka bagger to implement bagging */
    private Bagging bagger;

    public WeightedBagging withWeightEstimator(WeightEstimator weightEstimator) {
        this.weightEstimator = weightEstimator;
        return this;
    }

    public WeightedBagging withDataset(Instances dataset) {
        this.dataset = dataset;
        return this;
    }

    public WeightedBagging withTestDataset(Instances testDataset) {
        this.testDataset = testDataset;
        return this;
    }

    /**
     * Gets the estimated weights and uses it to do bagging
     * @throws Exception When classification fails
     */
    public void classifySpamOrHam() throws Exception {
        double[] weights = weightEstimator.estimateWeights(dataset);
        applyWeight(weights);

        NaiveBayes naiveBayes = new NaiveBayes();
        bagger = new Bagging();
        bagger.setClassifier(naiveBayes);
        bagger.setSeed(1);
        bagger.setNumIterations(10);
        bagger.setCalcOutOfBag(false);
        bagger.setNumExecutionSlots(1);
        bagger.setBagSizePercent(100);
        bagger.setBatchSize("100");
        bagger.setNumDecimalPlaces(2);
        bagger.setOutputOutOfBagComplexityStatistics(false);
        bagger.setRepresentCopiesUsingWeights(true);
        bagger.setStoreOutOfBagPredictions(false);
        bagger.buildClassifier(dataset);
    }

    private void applyWeight(double[] weights) {
        for (int i = 0; i < weights.length; i++) {
            dataset.instance(i).setWeight(weights[i]);
        }
    }

    /**
     * Compute Area under ROC curve
     * @return Returns double value in range of 0 to 1
     * @throws Exception When AUC computation fails
     */
    public Double computeAUC() throws Exception {
        Evaluation eval = new Evaluation(dataset);
        eval.evaluateModel(bagger, testDataset);

        ThresholdCurve tc = new ThresholdCurve();
        int classIndex = 1;
        Instances result = tc.getCurve(eval.predictions(), classIndex);
        return ThresholdCurve.getROCArea(result);
    }

}
