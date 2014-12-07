/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.ADVERSE;
import static matrices.operations.MatrixOperation.COMPARE;
import static matrices.operations.MatrixOperation.DIF_ABS;
import static matrices.operations.MatrixOperation.EXP;
import static matrices.operations.MatrixOperation.INCREMENT;
import static matrices.operations.MatrixOperation.INVERSE;
import static matrices.operations.MatrixOperation.ONES;
import static matrices.operations.MatrixOperation.RANDOM;
import static matrices.operations.MatrixOperation.SUM_COLUMNS;
import static matrices.operations.MatrixOperation.SUM_ROWS;
import org.jblas.FloatMatrix;
import rbm.MainFrame;

/**
 *
 * @author Skrzypek
 */
public class RBMtrain extends Thread {

    // number of epochs
    private final int epochs;
    // weights matrix
    // hidden units
    // number of hidden
    private final int hiddenUnits;
    // size of minibatch
    private final int minibatchSize;
    // training rate
    private double trainingRate;
    // number of training concepts
    private final int concepts;
    // numbet of features
    private final int features;
    // training data
    private final FloatMatrix trainingSet;
    // start time, end Time
    Long startTime;
    Long endTime;
    double duration;
    float alpha;
    FloatMatrix weights;
//    FloatMatrix matrixA;
//    FloatMatrix matrixB;
    FloatMatrix probabilities;
    FloatMatrix mXTemp;
    FloatMatrix mi;
    FloatMatrix h2;
    FloatMatrix x2;
    FloatMatrix mi2;
    FloatMatrix xVal;
    FloatMatrix hiddenMatrix;
    int bParameter;
    private boolean calcError;
    private ArrayList<Node> errors;
    private StringBuilder trainInfo;
    private String newLine;

    MainFrame mainFrame;

    int count = 0;

    private final CalculatedMatrixFactory cmf;
    private String dataSetName;

    public FloatMatrix getProbabilities() {
        return this.probabilities;
    }

    public FloatMatrix getHidden() {
        return this.hiddenMatrix;
    }

    public FloatMatrix getWeights() {
        return this.weights;
    }

    public RBMtrain(FloatMatrix trainingSet, FloatMatrix xVal, int hiddenUnits,
            int epochs, int minibatch, float alpha, MainFrame mainFrame) {
        cmf = new CalculatedMatrixFactory();
        this.trainingSet = trainingSet;
        this.xVal = xVal;
        this.hiddenUnits = hiddenUnits;
        this.epochs = epochs;
        this.minibatchSize = minibatch;
        this.alpha = alpha;
        this.concepts = trainingSet.rows;
        this.features = trainingSet.columns;
        randomWeights();
        randomHidden();
        randomProbabilities();
        System.out.println("Constructed");
        bParameter = (int) (this.concepts / this.minibatchSize);
        this.mainFrame = mainFrame;
        calcError = false;
        errors = new ArrayList<Node>();
        trainInfo = new StringBuilder();
        newLine = System.getProperty("line.separator");
    }

    private void randomWeights() {
        weights = new FloatMatrix(features, hiddenUnits);
        for (int i = 0; i < features; i++) {
            for (int j = 0; j < hiddenUnits; j++) {
                weights.put(i, j, MOUtil.randomGaussian() * 0.01f);
            }
        }
    }

    private void randomProbabilities() {
        probabilities = new FloatMatrix(features, 1);
        for (int i = 0; i < features; i++) {
            probabilities.put(i, 0, MOUtil.randomGaussian() * 0.01f);
        }
    }

    private void randomHidden() {
        hiddenMatrix = new FloatMatrix(hiddenUnits, 1);
        for (int i = 0; i < hiddenUnits; i++) {
            hiddenMatrix.put(i, 0, MOUtil.randomGaussian() * 0.01f);
        }
    }

    @Override
    public void run() {
        trainRBM();
    }

    public void trainRBM() {
        count = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        trainInfo.append("-- Train: ").append(dateFormat.format(date))
                .append(" --").append(newLine);
        trainInfo.append("Data Set: ").append(dataSetName).append(newLine).append(newLine);
        trainInfo.append("Concepts: ").append(concepts).append(newLine);
        trainInfo.append("Features: ").append(features).append(newLine);
        trainInfo.append("Epochs: ").append(epochs).append(newLine);
        trainInfo.append("Hidden units: ").append(hiddenUnits).append(newLine);
        trainInfo.append("Minibatch size: ").append(minibatchSize).append(newLine);
        trainInfo.append("Alpha: ").append(alpha).append(newLine);
        trainInfo.append(newLine);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < epochs; i++) {
            long startE = System.currentTimeMillis();
            int epochLog = i + 1;
            System.out.println("Epoch: " + i + " of " + epochs);
            mainFrame.setProgress("Epoka " + epochLog + " z " + epochs);
            trainEpoch();
            long endE = System.currentTimeMillis();
            double d = (endE - startE) / 1000;
            System.out.println("Epoch time: " + d);
            mainFrame.setOther("Czas ostatniej epoki: " + d);
            if (calcError) {
                float error = calculateError(i+1);
                calcError = false;
                mainFrame.setError(error + "");
            }
        }
        calculateError(epochs);
        endTime = System.currentTimeMillis();
        duration = (endTime - startTime) / 1000;
        trainInfo.append("Duration: ").append(duration).append(newLine);
        trainInfo.append("Epoch avarage: ").append(duration / epochs).append(newLine).append(newLine);
        logErrors();
        System.out.println(duration);
        System.out.println(count);
        mainFrame.setOther("Czas uczenia: " + duration);
        mainFrame.saveAll();
    }

    private void trainEpoch() {
        int[] batchID = new int[bParameter * minibatchSize];
        for (int i = 0; i < minibatchSize; i++) {
            fillBatch(batchID);
        }
        for (int i = 0; i < bParameter; i++) {
            long startE = System.currentTimeMillis();
            System.out.println("Batch: " + i + " of " + bParameter);
            calculateParameters(batchID, i);
            long endE = System.currentTimeMillis();
            double d = (endE - startE) / 1000;
            System.out.println("Batch time: " + d);
        }
    }

    private void fillBatch(int[] batchID) {
        for (int i = 0; i < minibatchSize; i++) {
            ArrayList<Integer> temp = new ArrayList<>();
            for (int j = 0; j < bParameter;) {
                temp.add(j++);
            }
            Collections.shuffle(temp);
            int c = 0;
            for (int j : temp) {
                batchID[bParameter * i + c] = j;
                c++;
            }
        }
    }

    private void calculateParameters(int[] batchID, int index) {

//            long startE = System.currentTimeMillis();
//            System.out.println("Calculating parameters: " + j + " of " + bParameter);
        calculateXTemp(batchID, index);
//            cmf.saveMatrix(mXTemp, "mXTemp.txt");
//            System.out.println("Xtemp calculated");
        calculateMI();
//            cmf.saveMatrix(mi, "mi.txt");
//            System.out.println("MI calculated");
        calculateH2();
//            cmf.saveMatrix(h2, "h2.txt");
//            System.out.println("H2 calculated");
        calculateX2();
//            cmf.saveMatrix(x2, "x2.txt");
//            System.out.println("X2 calculated");
        calculateMI2();
//            cmf.saveMatrix(mi2, "mi2.txt");
//            System.out.println("MI2 calculated");
        calculateW();
//            cmf.saveMatrix(weights, "w.txt");
//            System.out.println("W calculated");
        calculateA();
//            cmf.saveMatrix(probabilities, "a.txt");
//            System.out.println("A calculated");
        calculateB();
//            cmf.saveMatrix(hiddenMatrix, "b.txt");
        System.out.println("B calculated");
        count++;
//            calculateError();
//            System.out.println("Error calculated");
//            long endE = System.currentTimeMillis();
//            double d = (endE - startE) / 1000;
//            System.out.println("Time: " + d);

    }

    private void calculateXTemp(int[] batchID, int j) {
        float[][] t = generateXTemp(batchID, j);
        mXTemp = new FloatMatrix(t);
    }

    private float[][] generateXTemp(int[] batchID, int index) {

        float[][] result = new float[minibatchSize][features];
        int counter = 0;
        int limit = bParameter * minibatchSize;

        for (int i = 0; i < limit; i++) {
            if (batchID[i] == index) {
                for (int j = 0; j < features; j++) {
                    result[counter][j] = trainingSet.get(i, j);
                }
//                System.out.println(Arrays.toString(result[counter]));
                counter++;
            }
        }
        return result;
    }

    private void calculateMI() {
        mi = cmf.multipleMatrixOperations(
                cmf.repeatMatrix(hiddenMatrix.transpose(), minibatchSize)
                .add(mXTemp.mmul(weights)),
                ADVERSE, EXP, INCREMENT, INVERSE);
    }

    private void calculateH2() {
        h2 = cmf.twoMatricesOperation(
                mi,
                cmf.singleMatrixOperation(mi, RANDOM),
                COMPARE);
    }

    private void calculateX2() {
        FloatMatrix temp = cmf.multipleMatrixOperations(
                cmf.repeatMatrix(probabilities.transpose(), minibatchSize).
                add(h2.mmul(weights.transpose())),
                ADVERSE, EXP, INCREMENT, INVERSE);
        x2 = cmf.twoMatricesOperation(
                temp,
                cmf.singleMatrixOperation(temp, RANDOM),
                COMPARE);
    }

    private void calculateMI2() {
        mi2 = cmf.multipleMatrixOperations(
                cmf.repeatMatrix(hiddenMatrix.transpose(), minibatchSize).
                add(x2.mmul(weights)),
                ADVERSE, EXP, INCREMENT, INVERSE);

    }

    private void calculateW() {
        float par = (float) (alpha / minibatchSize);
        weights = weights.add(
                (mXTemp.transpose().mmul(mi).sub(x2.transpose().mmul(mi2))).mmul(par));
    }

    private void calculateA() {
        float par = (float) (alpha / minibatchSize);
        probabilities = probabilities.add(cmf.singleMatrixOperation(
                mXTemp.sub(x2),
                SUM_COLUMNS)
                .transpose().mmul(par));
    }

    private void calculateB() {
        float par = (float) (alpha / minibatchSize);
        hiddenMatrix = hiddenMatrix.add(cmf.singleMatrixOperation(
                mi.sub(mi2),
                SUM_COLUMNS)
                .transpose().mmul(par));

    }

    private float calculateError(int i) {
        calculateMIOError();
        calculateX2Error();
        float error = errorValue();
        errors.add(new Node(i, error));
        System.out.println("error: " + error);
        return error;
    }

    private void calculateMIOError() {
        mi = cmf.multipleMatrixOperations(
                hiddenMatrix.mmul(cmf.singleMatrixOperation(new FloatMatrix(1, concepts), ONES))
                .transpose()
                .add(xVal.mmul(weights)),
                ADVERSE, EXP, INCREMENT, INVERSE);
    }

    private void calculateX2Error() {
        x2 = cmf.multipleMatrixOperations(
                probabilities.mmul(cmf.singleMatrixOperation(new FloatMatrix(1, concepts), ONES))
                .transpose()
                .add(mi.mmul(weights.transpose())),
                ADVERSE, EXP, INCREMENT, INVERSE);
    }

    private float errorValue() {
        float par = (float) (1.0 / (features * concepts));
        float error = cmf.multipleMatrixOperations(
                cmf.twoMatricesOperation(xVal, x2, DIF_ABS), SUM_COLUMNS, SUM_ROWS).get(0, 0);
        return par * error;
    }

    public void requestErrorCalculation() {
        calcError = true;
    }

    public ArrayList<Node> getErrors() {
        return errors;
    }

    private void logErrors() {
        trainInfo.append("-- Reconstruction errors --").append(newLine);
        for (Node node : errors) {
            trainInfo.append(node.getEntry()).append(newLine);
        }
    }

    public String getInfo() {
        return trainInfo.toString();
    }

    public void setDataSetName(String dataSetname) {
        this.dataSetName = dataSetname;
    }

    private class Node {
        private final int key;
        private final float value;

        public Node(int key, float value) {
            this.key = key;
            this.value = value;
        }

        public String getEntry() {
            return "Epoch: " + key + ", error: " + value;
        }
    }
}
