# Fraud Credit Card Detection - Group 17



## Overview


![scikit-learn](https://img.shields.io/badge/scikit--learn-%23F7931E.svg?style=for-the-badge&logo=scikit-learn&logoColor=white)
![Keras](https://img.shields.io/badge/Keras-%23D00000.svg?style=for-the-badge&logo=Keras&logoColor=white)
![NumPy](https://img.shields.io/badge/numpy-%23013243.svg?style=for-the-badge&logo=numpy&logoColor=white)
![Matplotlib](https://img.shields.io/badge/Matplotlib-%23ffffff.svg?style=for-the-badge&logo=Matplotlib&logoColor=black)
![Pandas](https://img.shields.io/badge/pandas-%23150458.svg?style=for-the-badge&logo=pandas&logoColor=white)
![Static Badge](https://img.shields.io/badge/HUST-project-red)


![alt text](Docs/Credit-card-fraud-top.jpg)


This project is focused on the detection of fraudulent activities in credit card transactions. We utilize various machine learning models to classify transactions as either fraudulent or legitimate based on transactional data. 

The primary objective of this project is to enhance the security of credit card transactions. By accurately identifying fraudulent transactions, we can prevent unauthorized activities and safeguard the financial assets of cardholders.

## Models Used

In this project, we tried to implement and evaluate the performance of the following machine learning models:

- **Logistic Regression**
- **Decision Trees**
- **Random Forest**
- **Support Vector Machine**
- **K-nearest neighbors**
- **Multi-layer Perception**
- **Deep Neural Networks**

# Results

Here is th summary of the results we obtained from the models:

| Methods | Accuracy | Precision | F1-Score | Recall |
|---------|----------|-----------|----------|--------|
| LR      | 0.96     | 0.93      | 0.96     | 0.98   |
| SVM     | 0.95     | 0.95      | 0.94     | 0.95   |
| DT      | 1.0      | 1.0       | 1.0      | 1.0    |
| RF      | 1.0      | 1.0       | 1.0      | 1.0    |
| KNN     | 0.95     | 0.98      | 0.91     | 0.84   |
| ANN     | 0.99     | 0.64      | 0.69     | 0.75   |
| DNN     | 0.89     | 0.90      | 0.82     | 0.75   |

## Run the project

1. Clone the Repository

   Clone this repository to your local machine using the following command:

   ```bash
   git clone https://github.com/chutrunganh/Fraud-Credit-Card-Detection-Group-17.git
    ```

2. Install Dependencies

   Check the `requirement.txt` file. It lists 
all Python libraries required for running the code. Ensure that you have Python 3.6 or higher installed.

3. Dataset

   All the Datasets we used located in the `Dataset` directory. Due to different paths depending on the environment, you may need to change the path to the dataset in the code.

4. Run the Code

   - To see the dataset1, dataset2 characteristic, use `Data Chracteristic.ipynb`

   - To see the dataset3 characteristic and preprocessing, see `dataset3` directory

   - **To run and evaluate models, use `RunModels.ipynb` file**. Also, we have a detail KNN version writen in Java, check the `KNN/src`

   -  If you want to run this on Google Colab, use this link to see the models Source Code "https://drive.google.com/file/d/1mfH6CoZBWxSbvjLXWtGxheA6CVklN7uE/view?usp=sharing"




**Some Notes**

*Ensure that the version of the scikit-learn == 1.2.2, while running in google colab, it might return no errors, but 
there might be error on local machine due to incompatible version with SMOTE library we use, detail here: https://stackoverflow.com/questions/76593906/how-to-resolve-cannot-import-name-missingvalues-from-sklearn-utils-param-v*



*Also, for testing purposes, ***we strongly recommend choosing Undersampling technique module*** in the source code as it significantly reduces the 
runtime (With some models like MLP, DNN, SVM can take more than 30 min for each hyperparameter tuning part with SMOTE technique). However, please note 
that Undersampling may not provide reliable evaluation results.*


# Documentation

Detailed about this project can be found in our `Report ML 20232.pdf` file in the Docs directory.

# Contributors

This project was developed by Group 17 for the Machine Learning course IT3910E, semester 20232 at Ha Noi University of Science and Technology.

| Name                       | ID       |
|----------------------------|----------|
| Vũ Hoàng Nhật Anh (Leader) | 20225471 |
| Chu Trung Anh              | 20225564 |
| Trần Nhật Minh             | 20225511 |
| Trần Nam Tuấn Vượng        | 20225540 |




