package com.aml.spamfilter;

import weka.core.Instances;

/**
 * WeightEstimator is a contract which HistogramWeightEstimator and KernelWeightEstimator abides.
 * Created by saranyakrishnan on 5/21/17.
 */
public interface WeightEstimator {
    double[] estimateWeights(Instances dataset) throws Exception;
}
