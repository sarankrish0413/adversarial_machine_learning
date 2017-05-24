package com.aml.spamfilter;

import weka.core.Instances;

import java.util.Arrays;

/**
 * This class computes probability density estimates and returns a double array where each element is weight of
 * that particular instance
 * Created by saranyakrishnan on 5/21/17.
 */
public class HistogramWeightEstimator implements WeightEstimator {

    @Override
    public double[] estimateWeights(Instances dataset) {
        double[][] histogram = createHistogram(dataset);

        double[] weights = new double[dataset.numInstances()];
        for (int weightIndex = 0; weightIndex < weights.length; weightIndex++) {
            weights[weightIndex] = Arrays.stream(histogram[weightIndex]).sum();
        }

        return weights;
    }

    private double[][] createHistogram(Instances data) {
        int instancesCount = data.numInstances();
        int attrCount = data.numAttributes();
        int[][] histogramArray = new int[instancesCount][attrCount];
        for (int instanceIndex = 0; instanceIndex < instancesCount; instanceIndex++) {
            for (int attributeIndex = 0; attributeIndex < attrCount; attributeIndex++) {
                int bin = (int) data.instance(instanceIndex).value(attributeIndex);
                histogramArray[instanceIndex][bin]++;
            }

        }

        return normalizeHistogramValues(histogramArray, attrCount);
    }

    private double[][] normalizeHistogramValues(int[][] histogramArray, int attrCount) {
        double[][] featureMatrix = new double[histogramArray.length][2];
        //Copy the histogram intensity value to feature matrix
        for (int mailIndex = 0; mailIndex < histogramArray.length; mailIndex++) {
            for (int intensityIndex = 0; intensityIndex < 2; intensityIndex++) {
                featureMatrix[mailIndex][intensityIndex] = histogramArray[mailIndex][intensityIndex] / (attrCount * 1.0);
            }
        }
        return featureMatrix;
    }

}
