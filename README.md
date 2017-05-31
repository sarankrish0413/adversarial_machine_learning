# **Weighted Bagging Weight Estimator**

This project explores different weight estimation techniques that can be used for weighted bagging.

* Histogram
* Kernel Density Estimation 
* K-nearest Neighbor

## **Steps to create feature set**
We have to create a small feature set to reduce the compuational complexity before we enter into weighted bagging. Please follow these steps.

1. `mvn clean install`
2. `cd target`
3. **Usage**:
   `java -cp <jar> <classname> emailFolderLocation tokensFileLocation featureSetOutputFileLocation emailClassificationIndexFile emailCount outputFeaturesetCount`
4. **Example**:
   `java -Xmx12G -XX:ParallelGCThreads=8 -XX:ConcGCThreads=2 -XX:MaxGCPauseMillis=200 -XX:+UseG1GC -XX:G1HeapRegionSize=32m -cp weighted-bagging-weight-estimator.jar com.aml.spamfilter.FeatureSetCreator "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/data/" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/tokens.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/output_features.txt" "/Machine_Learning_Adversarial/Project/Implementation/testing_feature_creator/index" 1000 20000`   
5. **Arguments**   

    * **emailFolderLocation**: Location of data folder in [TREC 2007](http://plg.uwaterloo.ca/~gvcormac/treccorpus07/) dataset
    * **tokensFileLocation**: Location of tokens generated by spamassasin naive bayes filter. This needs to be grabbed by changing spamassasin source code
    * **featureSetOutputFileLocation**: Where output feature set needs to be generated
    * **emailClassificationIndexFile**: File with each lines in format "spam|ham <email_file_name>". See unit test resources for more details
    * **emailCount**: Count of emails to consider for computation
    * **outputFeaturesetCount**: How many features to generate (or create)
   
## **Development and Testing**
After every commit, full test suite is run to validate the build using bitbucket pipelines.
All test resources and expected outputs are in test/resources/.
Make sure you run tests from root directory of project and NOT target directory.

### **Run all tests**
`mvn test`

### **Run single tests**

#### **Examples**
`mvn -Dtest=FeatureSetCreatorTest test`

`mvn -Dtest=DatasetCreatorTest test`