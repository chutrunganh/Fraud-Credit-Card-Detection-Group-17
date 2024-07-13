import java.io.*;
import java.util.*;
import java.util.Map.*;

public class KNN {
    private List<Datapoint> trainData0 = new ArrayList<>();
    private List<Datapoint> trainData1 = new ArrayList<>();
    private List<Datapoint> trainData = new ArrayList<>();
    private String[] attributeNames;
    private int attributeCount;

    private int isBalanced = NOT_BALANCED;
    private static final int NOT_BALANCED = -100;
    private static final int SOURCE = 100;
    private static final int UNDERSAMPLING = 101;
    private static final int SMOTE = 102;

    private static final int TRUE_POSITIVE = 51;
    private static final int TRUE_NEGATIVE = 52;
    private static final int FALSE_POSITIVE = 53;
    private static final int FALSE_NEGATIVE = 54
    ;
    private static final Double INFINITY = Double.MAX_VALUE;
    //private int pMinkowski;

    //Constructor
    KNN(String directory, int firstColumn, int lastColumn, int rows){
        try {
            BufferedReader bReader = new BufferedReader(new FileReader(directory));
            String line = bReader.readLine();
            bReader.close();
            String[] values = line.split(",");
            String[] values2 = new String[lastColumn - firstColumn + 1];
            for (int i = 0; i < values2.length; i++) {
                values2[i] = values[firstColumn + i];
            }
            this.attributeNames = values2;
            this.attributeCount = attributeNames.length;
            readTrainData(directory, firstColumn, lastColumn, rows);
            trainDataReset();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    //Make prediction
    public void test(List<Datapoint> testSet, int pMin, int pMax, int kMax, int smoteSize, int weightPower) throws UnfitDataException{
        try {
            FileWriter writer = new FileWriter("results.csv");
            writer.write("p,k,precision0,recall0,f1-score_0,support0,precision1,recall1,f1-score_1,support1,accuracy,total\n");
            for (int p = pMin; p <= pMax; p++) {
                
                if (smoteSize > 0) {
                    smote(smoteSize, p);
                } else {
                    if (isBalanced != UNDERSAMPLING) {
                        undersampling();
                    }
                }
                int[] truePositives = new int[kMax];
                Arrays.fill(truePositives, 0);
                int[] trueNegatives = new int[kMax];
                Arrays.fill(trueNegatives, 0);
                int[] falsePositives = new int[kMax];
                Arrays.fill(falsePositives, 0);
                int[] falseNegatives = new int[kMax];
                Arrays.fill(falseNegatives, 0);

                for (Datapoint datapoint : testSet) {
                    List<Integer> predictions = testP(datapoint, p, kMax, weightPower);
                    for (int k = 0; k < kMax; k++) {
                        int prediction = predictions.get(k);
                        switch (prediction) {
                            case TRUE_POSITIVE:
                                truePositives[k]++;
                                break;
                            case TRUE_NEGATIVE:
                                trueNegatives[k]++;
                                break;
                            case FALSE_POSITIVE:
                                falsePositives[k]++;
                                break;
                            case FALSE_NEGATIVE:
                                falseNegatives[k]++;
                            default:
                                break;
                        }
                    }
                }

                for (int k = 0; k < kMax ; k++) {
                    StringBuffer stas = new StringBuffer();
                    stas.append(p + "," + (k + 1) + ",");
                    stas.append(writeStatistic(truePositives[k], trueNegatives[k], falsePositives[k], falseNegatives[k]) + "\n");
                    writer.write(stas.toString());
                    writer.flush();
                }
                System.out.println("Finished testing p = " + p + ". Results printed to results.csv.");
            }
            writer.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Integer> testP(Datapoint o, int p, int kMax, int weightPower) throws UnfitDataException{
        List<Integer> predictions = new ArrayList<>();
        SortedMap<Double, Datapoint> nearestNeighbors = getNeighbors(o, kMax, p);
        double fraudScore = 0;
        for (int k = 1; k <= kMax; k++) {
            Datapoint neighbor = nearestNeighbors.get(nearestNeighbors.firstKey());
            Double distance = nearestNeighbors.firstKey();

            if (neighbor.isFraud()) {
                fraudScore += 1 / Math.pow(distance, weightPower);
            } else {
                fraudScore -= 1 / Math.pow(distance, weightPower);
            }
            boolean prediction = fraudScore > 0;
            if (prediction == o.isFraud()) {
                if (prediction) {
                    predictions.add(TRUE_POSITIVE);
                } else {
                    predictions.add(TRUE_NEGATIVE);
                }
            } else {
                if (prediction) {
                    predictions.add(FALSE_POSITIVE);
                } else {
                    predictions.add(FALSE_NEGATIVE);
                }
            }
            nearestNeighbors.remove(nearestNeighbors.firstKey());
        }       
        return predictions;
    }
    private String writeStatistic(int truePositive, int trueNegative, int falsePositive, int falseNegative){
        int support0 = trueNegative + falsePositive;
        int support1 = truePositive + falseNegative;
        int total = support0 + support1;
        double predictedNegative = trueNegative + falseNegative;
        double predictedPositive = truePositive + falsePositive;

        double accuracy = (truePositive + trueNegative) / (double)total;
        double precision0 = trueNegative / predictedNegative;
        double precision1 = truePositive / predictedPositive;
        double recall0 = trueNegative / (double)support0;
        double recall1 = truePositive / (double)support1;
        double f1Score0 = 2 * (precision0 * recall0) / (precision0 + recall0);
        double f1Score1 = 2 * (precision1 * recall1) / (precision1 + recall1);

        String results = String.format("%.2f,%.2f,%.2f,%s,%.2f,%.2f,%.2f,%s,%.2f,%s", 
                precision0, recall0, f1Score0, support0, 
                precision1, recall1, f1Score1, support1, 
                accuracy, total);
        return results;
    }
    ///

    public void predict(List<Datapoint> testSet,int k, int p) throws UnfitDataException{
        try {
            FileWriter writer = new FileWriter("predictions.csv");
            writer.write("index,predicted_class,correct_class\n");
            writer.flush();
            int index = 1;
            for (Datapoint datapoint : testSet) {   
                int pred = (predict(datapoint, k, p)? 1 : 0);
                int corr = (datapoint.isFraud()? 1 : 0);
                writer.write(String.format("%s, %s, %s\n", index, pred, corr));
                writer.flush();
                index++;
            }
            System.out.println("Predictions printed to predictions.csv.");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public boolean predict(Datapoint o, int numberOfNeighbors, int pMinkowski) throws UnfitDataException{
        return score(o, numberOfNeighbors, pMinkowski) >= 0;
    }
    public double score(Datapoint o, int numberOfNeighbors, int pMinkowski) throws UnfitDataException{
        SortedMap<Double, Datapoint> nearestNeighbors = getNeighbors(o, numberOfNeighbors, pMinkowski);
        double fraudScore = 0;
        for (Entry<Double, Datapoint> entry : nearestNeighbors.entrySet()) {
            Datapoint neighbor = entry.getValue();
            Double distance = entry.getKey();
            if (distance == 0) {
                if (neighbor.isFraud()) {
                    return INFINITY;
                }
                return -INFINITY;
            }

            if (neighbor.isFraud()) {
                fraudScore += 1 / Math.pow(distance, 1);
            } else {
                fraudScore -= 1 / Math.pow(distance, 1);
            }
        }
        return fraudScore;
    }

    private SortedMap<Double, Datapoint> getNeighbors(Datapoint o, int k, int pMinkowski) throws UnfitDataException{
        return getNeighbors(o, k, trainData, pMinkowski);
    }
    private SortedMap<Double, Datapoint> getNeighbors(Datapoint o, int k, List<Datapoint> set, int pMinkowski) throws UnfitDataException{
        if (o.getAttributes().size() != attributeCount) {
            throw new UnfitDataException("Data does not fit the model");
        }
        SortedMap<Double, Datapoint> nearestNeighbors = new TreeMap<Double, Datapoint>();

        //Make a list of k nearest neighbors
        for (Datapoint newNeighbor : set) {
            Double newDistance = distanceBetween(o, newNeighbor, pMinkowski);
            //Fill up the list of neighbors
            if (nearestNeighbors.size() < k) {
                nearestNeighbors.put(newDistance, newNeighbor);

            //Update the list of neighbors
            } else if (newDistance < nearestNeighbors.lastKey()) {
                nearestNeighbors.remove(nearestNeighbors.lastKey());
                nearestNeighbors.put(newDistance, newNeighbor);
            }
        }

        return nearestNeighbors;
    }
    private double distanceBetween(Datapoint o1, Datapoint o2, int pMinkowski){
        if (o1.equals(o2)) {
            return 0;
        }
        if (o1.getAttributes().size() != o2.getAttributes().size()) {
            return -1;
        }
        double distance = 0;
        for (int i = 0; i < o1.getAttributes().size(); i++) {
            distance += Math.pow(Math.abs(o1.getAttributes().get(i) - o2.getAttributes().get(i)), pMinkowski);
        }
        return Math.pow(distance, 1 / (double)pMinkowski);
    }   

    //Add Observation
    public void addObservation (Datapoint ob) throws UnfitDataException{
        if (ob.getAttributes().size() != attributeCount) {
            throw new UnfitDataException("Data does not fit the model");
        }
        
        if (ob.isFraud()) {
            trainData1.add(ob);
        } else {
            trainData0.add(ob);
        }
        trainDataReset();
    }

    //Read Traning data
    private void readTrainData (String directory, int firstColumn, int lastColumn, int rows){
        BufferedReader bReader;
        try {
            bReader = new BufferedReader(new FileReader(directory));
            bReader.readLine(); //skip header row
            if (rows > 0) {
                for (int row = 1; row < rows; row++) {
                    readTrainDataNextLine(bReader.readLine(), firstColumn);
                }
            } else {
                String line;
                while ((line = bReader.readLine()) != null) {
                    readTrainDataNextLine(line, firstColumn);
                }
            }
            bReader.close();

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    private void readTrainDataNextLine(String line, int firstColumn){
        String valuesString[] = line.split(",");
        Double valuesDouble[] = new Double[attributeCount];
        for (int i = 0; i < valuesDouble.length; i++) {
            valuesDouble[i] = Double.parseDouble(valuesString[firstColumn + i]);
        }

        List<Double> valuesList = Arrays.asList(valuesDouble);
        boolean isFraud = (valuesString[valuesString.length - 1].equals("\"1\"") || valuesString[valuesString.length - 1].equals("1"));
        Datapoint ob = new Datapoint(valuesList, isFraud);
        //trainDataFull.add(ob);
        if (isFraud) {
            trainData1.add(ob);
        } else {
            trainData0.add(ob);
        }
    }
    
    //Handle imbalance data
    private void trainDataReset(){
        trainData = new ArrayList<>();
        for (Datapoint observation : trainData0) {
            trainData.add(observation);
        }
        for (Datapoint observation : trainData1) {
            trainData.add(observation);
        }
        isBalanced = NOT_BALANCED;
        if (trainData0.size() == trainData1.size()) {
            isBalanced = SOURCE;
        }
    }

    public void undersampling(){
        List<Datapoint> big, small;
        if (trainData0.size() > trainData1.size()) {
            big = trainData0;
            small = trainData1;
        } else if (trainData0.size() < trainData1.size()){
            big = trainData1;
            small = trainData0;
        } else {
            System.out.println("The dataset is already balanced.");
            return;
        }

        trainData = new ArrayList<>();
        for (Datapoint observation : small) {
            trainData.add(observation);
        }
        Collections.shuffle(big);
        for (int i = 0; i < small.size(); i++) {
            trainData.add(big.get(i));
        }
        System.out.println("Finished undersampling. Each class has " + small.size() + " datapoints.");
        isBalanced = UNDERSAMPLING;
    }

    public void smote(int size, int p) throws UnfitDataException{
        List<Datapoint> big, small;
        if (trainData0.size() > trainData1.size()) {
            big = trainData0;
            small = trainData1;
        } else if (trainData0.size() < trainData1.size()){
            big = trainData1;
            small = trainData0;
        } else {
            System.out.println("The dataset is already balanced.");
            return;
        }

        trainData = new ArrayList<>();

        Collections.shuffle(big);
        for (int i = 0; i < size; i++) {
            trainData.add(big.get(i));
        }

        Collections.shuffle(small);
        List<Datapoint> smallCopy = new ArrayList<>();
        for (Datapoint ob : small) {
            smallCopy.add(ob);
        }
        outerloop:
        for (Datapoint ob : small) {
            //int k = Math.ceilDiv(size, small.size());
            SortedMap<Double, Datapoint> neighbors = getNeighbors(ob, 3, small, p);
            for (Entry<Double, Datapoint> entry : neighbors.entrySet()) {
                List<Double> attribute0 = ob.getAttributes();
                List<Double> attribute1 = entry.getValue().getAttributes();
                List<Double> newAttributes = new ArrayList<>();
                for (int i = 0; i < attribute0.size(); i++) {
                    Double x0 = attribute0.get(i);
                    Double x1 = attribute1.get(i);
                    Double newAttribute = x0 + Math.random() * Math.abs(x0 - x1);
                    newAttributes.add(newAttribute);
                }
                smallCopy.add(new Datapoint(newAttributes, ob.isFraud()));
                if (smallCopy.size() >= size) {
                    break outerloop;
                }
            }
        }
        for (int i = 0; i < size; i++) {
            trainData.add(smallCopy.get(i));
        }

        System.out.println("Finished oversampling with SMOTE. Each class has " + size + " datapoints.");
        isBalanced = SMOTE;
    }

    //Accessors
    public String getAttributeNamesString(){
        return Arrays.asList(attributeNames).toString();
    }
    public void dataOverview(){
        String answer = "";
        int records = 0;
        int isFraud = 0, notFraud = 0;
        if (trainData.size() == 0) {
            records = trainData0.size() + trainData1.size();
            isFraud = trainData1.size();
            notFraud = trainData0.size();
        } else {
            records = trainData.size();
            for (Datapoint ob : trainData) {
                if (ob.isFraud()) {
                    isFraud++;
                } else {
                    notFraud++;
                }
            }
        }

        answer += (records) + " datapoints, ";
        answer += attributeCount + " attributes. ";
        answer += isFraud + " datapoints are fraud, " + notFraud + " datapoints are not fraud. ";
        switch (isBalanced) {
            case SOURCE:
                answer += ("The dataset is balanced by source.");
                break;
            case UNDERSAMPLING:
                answer += ("The dataset is balanced by undersampling.");
                break;
            case SMOTE:
                answer += ("The dataset is balanced by oversampling with SMOTE.");
                break;
            default:
                answer += ("The dataset is not balanced.");
                break;
        }
        System.out.println(answer);
    }


}
