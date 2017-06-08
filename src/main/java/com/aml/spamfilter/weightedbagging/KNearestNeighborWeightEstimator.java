package com.aml.spamfilter.weightedbagging;

import weka.classifiers.lazy.IBk;
import weka.core.Instances;

/**
 * Created by saranyakrishnan on 6/3/17.
 */
public class KNearestNeighborWeightEstimator implements WeightEstimator {

    @Override
    public double[] estimateWeights(Instances dataset) throws Exception {
        double probabilityEstimates[][] = calculateProbabilityEstimates(dataset);
        double[] weightEstimates = new double[dataset.numInstances()];
        for (int instanceIndex = 0; instanceIndex < dataset.numInstances(); instanceIndex++) {
            double sum = 0;
            for (int attributeIndex = 0; attributeIndex < dataset.get(instanceIndex).numAttributes(); attributeIndex++) {
                sum += probabilityEstimates[instanceIndex][(int) dataset.instance(instanceIndex).value(attributeIndex)];
            }
            weightEstimates[instanceIndex] = sum / dataset.instance(instanceIndex).numAttributes();
        }
        return weightEstimates;
    }

    private double[][] calculateProbabilityEstimates(Instances dataset) throws Exception {
        int K = (int) Math.sqrt(dataset.numInstances()) / 2;
        IBk ibkModel = new IBk(K);
        ibkModel.buildClassifier(dataset);
        double[][] probabilityEstimates = new double[dataset.numInstances()][2];

        for (int instance = 0; instance < dataset.numInstances(); instance++){
            probabilityEstimates[instance] = ibkModel.distributionForInstance(dataset.instance(instance));
        }

        return normalizeKNearestNeighborValues(probabilityEstimates, dataset.numAttributes());
    }

    private double[][] normalizeKNearestNeighborValues(double[][] knearestNeighbor, int attrCount) {
        double[][] featureMatrix = new double[knearestNeighbor.length][2];

        for (int instanceIndex = 0; instanceIndex < knearestNeighbor.length; instanceIndex++) {
            for (int attributeIndex = 0; attributeIndex < 2; attributeIndex++) {
                featureMatrix[instanceIndex][attributeIndex] = knearestNeighbor[instanceIndex][attributeIndex] / (attrCount * 1.0);
            }
        }

        return featureMatrix;
    }

}
