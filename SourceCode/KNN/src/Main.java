import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //**Import the traning dataset
        int firstColumn = 1, lastColumn = 28;
        int rows = -1;  //Number of row to import, "-1" means import all rows 
        KNN knn = new KNN("data2_train.csv", firstColumn, lastColumn, rows);
        knn.dataOverview();

        //**Balancing the training dataset
        //knn.undersampling();
        int pMinkowski = 2;
        knn.smote(500, pMinkowski);
        knn.dataOverview();

        //Import the testing dataset
        BufferedReader bReader = new BufferedReader(new FileReader("data2_test.csv"));
        String line = bReader.readLine(); //skip header
        List<Datapoint> testSet = new ArrayList<>();
        //for (int i = 0; i < 500; i++) { line = bReader.readLine();
        while ((line = bReader.readLine()) != null){
            String data[] = line.split(",");
            List<Double> attribute = new ArrayList<>();
            boolean isFraud = (data[data.length - 1].equals("\"1\"") || data[data.length - 1].equals("1"));
            for (int j = firstColumn; j <= lastColumn; j++) {
                attribute.add(Double.parseDouble(data[j]));
            }
            Datapoint o = new Datapoint(attribute, isFraud);
            testSet.add(o);
        }
        bReader.close();

        //**Test different values of p (for the Minkowski distance function) and k
        knn.test(testSet, 1, 10, 500, 500, 1);

        //**Print predictions
        knn.predict(testSet, 50, 2);
    }
}

