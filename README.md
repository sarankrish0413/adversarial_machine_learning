# **Weighted Bagging Weight Estimator**

This project explores different weight estimation techniques that can be used for weighted bagging.

* Histogram
* Kernel Density Estimation
* K-Nearest Neighbor

Please refer the [wiki](https://bitbucket.org/sarankuw/adversarial_machine_learning/wiki/Home) for detailed design.
# **Steps to run experiment**

1. Create Tokens Using SpamAssasin filter or use the attached file called "tokens.txt"
1. Create feature set
1. Run experiment using feature set

## **Steps to create feature set**
I have to create a small feature set to reduce the compuational complexity before we enter into weighted bagging. Please follow these steps.

1. `mvn -q clean install`
2. `cd target`
3. **Usage**:
   `java -cp weighted-bagging-weight-estimator.jar com.aml.spamfilter.featureselection.FeatureSetCreator emailFolderLocation tokensFileLocation featureSetOutputFileLocation emailClassificationIndexFile emailCount outputFeaturesetCount`
4. **Example**:
   `java -Xmx12G -XX:ParallelGCThreads=8 -XX:ConcGCThreads=2 -XX:MaxGCPauseMillis=200 -XX:+UseG1GC -XX:G1HeapRegionSize=32m \-cp weighted-bagging-weight-estimator.jar com.aml.spamfilter.featureselection.FeatureSetCreator "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/data/" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/tokens.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/output_features.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/index" 1000 20000`   
5. **Arguments**   

    * **emailFolderLocation**: Location of data folder in [TREC 2007](http://plg.uwaterloo.ca/~gvcormac/treccorpus07/) dataset
    * **tokensFileLocation**: Location of tokens generated by spamassasin naive bayes filter. This needs to be grabbed by changing spamassasin source code or use the attached file (tokens.txt)
    * **featureSetOutputFileLocation**: Where output feature set needs to be generated
    * **emailClassificationIndexFile**: File with each lines in format "spam|ham <email_file_name>". See unit test resources for more details
    * **emailCount**: Count of emails to consider for computation
    * **outputFeaturesetCount**: How many features to generate (or create)

## **Steps to run experiment using feature set**
Now we can run the experiment using the feature set created in previous step. Please follow these steps.

1. `mvn -q clean install`
2. `cd target`
3. **Usage**:
   `java -cp weighted-bagging-weight-estimator.jar com.aml.spamfilter.experiment.ExperimentRunner emailFolderLocation featureSetFileLocation emailClassificationIndexFile emailCount outputDatasetFileName goodWordsFileName testDatasetFileLocation tempFolderToPoisonDataset`
4. **Example**:
   `java -Xmx12G -XX:ParallelGCThreads=8 -XX:ConcGCThreads=2 -XX:MaxGCPauseMillis=200 -XX:+UseG1GC -XX:G1HeapRegionSize=32m -cp weighted-bagging-weight-estimator.jar com.aml.spamfilter.experiment.ExperimentRunner "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/data/" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/output_features.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/index" 2500 "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/output_dataset.arff" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/good_words_5k.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/test_dataset.arff" "/tmp/tempfolder_play/"`


## **Development and Testing**
After every commit, full test suite is run to validate the build using bitbucket pipelines.
Surefire plugin is used to generate test reports
All test resources and expected outputs are in test/resources/.
Make sure you run tests from root directory of project and NOT target directory.

### **Run all tests**
`mvn -q test`

### **Run single tests**

#### **Examples**
`mvn -q -Dtest=FeatureSetCreatorTest test`

`mvn -q -Dtest=DatasetCreatorTest test`