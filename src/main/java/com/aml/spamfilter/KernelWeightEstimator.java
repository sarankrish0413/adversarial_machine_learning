package com.aml.spamfilter;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;

import java.lang.reflect.Field;
import java.util.Random;

/**
 * Probability density estimates based on kernel density estimation
 *
 * Created by saranyakrishnan on 5/21/17.
 */
public class KernelWeightEstimator implements WeightEstimator {

    @Override
    public double[] estimateWeights(Instances dataset) throws Exception {
        double[] probabilityEstimates = calculateProbabilityEstimates(dataset);
        double[] weightEstimates = new double[dataset.numInstances()];
        for (int instanceIndex = 0; instanceIndex < dataset.numInstances(); instanceIndex++) {
            for (int attributeIndex = 0; attributeIndex < dataset.get(instanceIndex).numAttributes(); attributeIndex++) {
                weightEstimates[instanceIndex] += probabilityEstimates[(int) dataset.instance(instanceIndex).value(attributeIndex)];
            }
        }
        return weightEstimates;
    }

    private double[] calculateProbabilityEstimates(Instances dataset) throws Exception {
        LibSVM libsvmModel = new LibSVM();
        libsvmModel.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
        libsvmModel.setGamma(0.00005);
        libsvmModel.setCost(1.5);
        libsvmModel.setProbabilityEstimates(true);

        // train classifier
        libsvmModel.buildClassifier(dataset);
        Evaluation evaluation = null;
        evaluation = new Evaluation (dataset);
        evaluation.crossValidateModel(libsvmModel, dataset, 10, new Random(1));


        // extract trained model via reflection, type is libsvm.svm_model
        // see https://github.com/cjlin1/libsvm/blob/master/java/libsvm/svm_model.java
        svm_model model = getModel(libsvmModel);


        svm_node x[] = model.SV[0]; // let's try the first data pt in problem
        double[] prob_estimates = new double[2];
        svm.svm_predict_probability(model, x, prob_estimates);
        return prob_estimates;

    }
    private svm_model getModel(LibSVM svm) throws IllegalAccessException, NoSuchFieldException {
        Field modelField = svm.getClass().getDeclaredField("m_Model");
        modelField.setAccessible(true);
        return (svm_model) modelField.get(svm);
    }
}
