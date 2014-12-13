/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.ADVERSE;
import static matrices.operations.MatrixOperation.CUMSUM;
import static matrices.operations.MatrixOperation.EXP;
import static matrices.operations.MatrixOperation.INCREMENT;
import static matrices.operations.MatrixOperation.INVERSE;
import static matrices.operations.MatrixOperation.NORMALIZE;
import static matrices.operations.MatrixOperation.ZEROS;
import org.jblas.FloatMatrix;
import rbm.MainFrame;

/**
 *
 * @author Skrzypek
 */
public class RBM extends Thread {

    // number of questions
    private int questions;
    // number of concepts (movies)
    private int concepts;
    // numbet of features
    private int features;
    //sprasity of correct answers
    private double q;
    //hidden units
    private int hiddenUnits;
    // epoches
    private int epochs;
    // size of minibatch
    private int minibatch;
    // alpha
    private double alpha;

    private FloatMatrix dataSet;

    private FloatMatrix w;
    private FloatMatrix a;
    private FloatMatrix b;
    private FloatMatrix xMatrix;
    private FloatMatrix vMatrix;
    private double acc;

    private CalculatedMatrixFactory cmf;
    private FloatMatrix h1;
    private FloatMatrix v2;

    private MainFrame mainFrame;
    StringBuilder mainInfo;
    private String newLine;
    private Node[] order;
    EntropyCalculator entropyCalculator;
    private boolean filterMovies;
    SelectionHelperType selectionHelperType;

    public RBM(FloatMatrix a, FloatMatrix b, FloatMatrix w, int questions, FloatMatrix dataSet, MainFrame mainFrame) {
        this.a = a;
        this.b = b;
        this.w = w;
        this.questions = questions;
        this.cmf = new CalculatedMatrixFactory();
        this.dataSet = dataSet;
        this.mainFrame = mainFrame;
        this.entropyCalculator = new EntropyCalculator(dataSet.copy(dataSet));
        mainInfo = new StringBuilder();
        newLine = System.getProperty("line.separator");
        setParameters();
        this.order = new Node[features];
        for (int i = 0; i < features; i++) {
            order[i] = new Node();
        }
    }

    @Override
    public void run() {
        executeForAll(filterMovies, selectionHelperType);
    }

    public void executeForAll(boolean entropy, SelectionHelperType selectionHelperType) {
        // starts from 1 because of movie numbering
        this.selectionHelperType = selectionHelperType;
        for (int i = 1; i < concepts + 1; i++) {
            System.out.println("Film nr: " + i);
            this.entropyCalculator = new EntropyCalculator(dataSet.copy(dataSet));
            
            mainInfo.append(i - 1).append(newLine);
            mainFrame.setProgress("Film nr " + i + " z " + concepts);
            long start = System.currentTimeMillis();
            int similar = recognizeMovie(i, entropy, selectionHelperType);
            long end = System.currentTimeMillis();
            System.out.println("------------");
            float time = (end - start) / 1000;
            mainFrame.setOther("Ostatni film byl jednym z: " + similar);
        }
        System.out.println(mainInfo.toString());
        //statistics
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < features; i++) {
            sb.append(i).append(";");
            //sb.append(vector.features.get(i).getQuestion()).append(";");
            sb.append(order[i].getListLength()).append(";");
            sb.append(order[i]).append(System.getProperty("line.separator"));
        }
        Date time = new Date();
        File f = new File("statistics"+time.getTime()+".txt");
        File info = new File("mainInfo"+time.getTime()+".txt");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            if (!info.exists()) {
                info.createNewFile();
            }
            PrintWriter writer = new PrintWriter(f);
            writer.printf(sb.toString());
            writer.close();
            writer = new PrintWriter(info);
            writer.printf(mainInfo.toString());
            writer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * recognize movie using questions selection based on RBM
     * 
     * @param movieId
     * @param filterMovies
     * @return
     */
    public int recognizeMovie(int movieId, boolean filterMovies, SelectionHelperType selectionHelperType) {
        xMatrix = new FloatMatrix(generateXMatrix(movieId - 1));
        vMatrix = cmf.singleMatrixOperation(new FloatMatrix(features, 1), ZEROS);
        int[] ids = new int[questions];
        int[] answered = new int[features];
        Arrays.fill(ids, -1);
        Arrays.fill(answered, -1);
        if (filterMovies) {
            entropyCalculator.calculateForAllQuestions();
        }
        for (int j = 0; j < questions; j++) {
            h1 = calculateH1();
            v2 = calculateV2();
            removeAnswered(answered);
            includeEntropy();
            FloatMatrix px = cmf.singleMatrixOperation(v2, NORMALIZE);
            cmf.saveMatrix(px, "add.txt");
            double r = Math.random();
            FloatMatrix cumsum = cmf.singleMatrixOperation(px, CUMSUM);
            FloatMatrix l = lessThan(cumsum, r);
            int id = (int) sum(l);
            ids[j] = id;
            if (filterMovies) {
                entropyCalculator.getCalculatedValues()[id] = (int) xMatrix.get(0, id);
            }
//            System.out.println(Arrays.toString(ids));
            answered[id] = (int) xMatrix.get(0, id);
            if (filterMovies){
                entropyCalculator.filterMovies(id, (int) xMatrix.get(0, id));
            }
            vMatrix.put(id, 0, xMatrix.get(0, id));
            order[id].add(j);
            if (filterMovies) {
                answered = entropyCalculator.answeredQuestions();
            }
        }
        int similiar = calculateAnswers(answered);

        return similiar;

    }

    private void setParameters() {
        this.features = a.rows;
        this.concepts = dataSet.rows;
        acc = 0;
    }

    private float[][] generateXMatrix(int row) {
        float[][] result = new float[1][features];
        for (int i = 0; i < features; i++) {
            result[0][i] = dataSet.get(row, i);
        }
        return result;
    }

    private FloatMatrix calculateH1() {
        FloatMatrix result = cmf.multipleMatrixOperations(
                b.transpose().add(vMatrix.transpose().mmul(w)),
                ADVERSE, EXP, INCREMENT, INVERSE)
                .transpose();
        return result;
    }

    private FloatMatrix calculateV2() {
        FloatMatrix result = cmf.multipleMatrixOperations(
                a.add(w.mmul(h1)),
                ADVERSE, EXP, INCREMENT, INVERSE);
        return result;
    }

    private double sum(FloatMatrix m) {
        double result = 0;
        for (int i = 0; i < m.rows; i++) {
            for (int j = 0; j < m.columns; j++) {
                result += m.get(i, j);
            }
        }
        return result;
    }

    private FloatMatrix lessThan(FloatMatrix matrix, double parameter) {
        FloatMatrix result = new FloatMatrix(matrix.rows, matrix.columns);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result.put(i, j, matrix.get(i, j) < parameter ? 1 : 0);
            }
        }
        return result;
    }

    private double[] generateIDs() {
        double[] ids = new double[features];
        for (int i = 0; i < features; i++) {
            ids[i] = -1;
        }
        return ids;
    }

    private FloatMatrix selectV2(FloatMatrix v2, double[] ids) {
        FloatMatrix result = v2.copy(v2);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] != -1) {
                result.put((int) ids[i], 0, 0);
            }
        }
        return result;
    }

    private FloatMatrix generateVIds(FloatMatrix vMatrix, double[] ids) {
        FloatMatrix result = new FloatMatrix(questions, 1);
        for (int i = 0; i < questions; i++) {
            result.put(i, 0, vMatrix.get((int) ids[i], 0));
        }
        return result;
    }

    private float minimumValue(FloatMatrix matrix) {
        float min = Float.MAX_VALUE;
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                min = matrix.get(i, j) < min ? matrix.get(i, j) : min;
            }
        }
        return min;
    }

    private FloatMatrix generateTemp(FloatMatrix matrix, double min) {
        FloatMatrix result = new FloatMatrix(matrix.rows, matrix.columns);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result.put(i, j, matrix.get(i, j) == min ? 1 : 0);
            }
        }
        return result;
    }

    public double sumCon(FloatMatrix matrix, int n) {
        double result = 0;
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                if (matrix.get(i, j) > 0 && j == n) {
                    result += 1;
                }
            }
        }
        return result;
    }

    public double lengthCon(FloatMatrix matrix) {
        double result = 0;
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                if (matrix.get(i, j) > 0) {
                    result += 1;
                }
            }
        }
        return result;
    }

    private void removeAnswered(int[] answered) {
        int j = 0;
        for (int i = 0; i < features; i ++) {
            if (answered[i] != -1) {
                v2.put(i, 0, 0);
                j++;
            }
        }
    }

    private int calculateAnswers(int[] answered) {
        int[] matches = new int[concepts];
        Arrays.fill(matches, -1);
        int actuall = 0;
        for (int i = 0; i < concepts; i++) {
            int j = 0;
            boolean match = true;
            while (j < features) {
                if (answered[j] != -1 && dataSet.get(i, j) != answered[j]) {
                    match = false;
                }
                j++;
            }
            if (match) {
                matches[actuall] = i;
                actuall++;
            }
        }
        System.out.println("Matches: " + actuall);
        String matchedMovies = "";
        for (int i = 0; i < actuall; i++) {
            matchedMovies += matches[i] + " ";
        }
        mainInfo.append(actuall).append(newLine);
        mainInfo.append(matchedMovies).append(newLine);
        System.out.println(matchedMovies);
        return actuall;
    }

    private int countAnswered(int[] answered) {
        int counter = 0;
        for (int i : answered) {
            if (i != -1) counter ++;
        }
        return counter;
    }

    private void includeEntropy() {
        switch (selectionHelperType) {
            case NONE:
                break;
            case MULTIPLE:
                for (int i = 0; i < features; i ++) {
                    v2.put(i, 0, v2.get(i, 0) * entropyCalculator.getEntropyForFeature(i));
                }
                break;
            case ADD:
                for (int i = 0; i < features; i ++) {
                    v2.put(i, 0, v2.get(i, 0) + entropyCalculator.getEntropyForFeature(i));
                }
                break;
            case ONLY_ENTROPY:
                for (int i = 0; i < features; i ++) {
                    v2.put(i, 0, entropyCalculator.getEntropyForFeature(i));
                }
                break;
        }
    }

    private class Node {

        ArrayList<Integer> list;

        public Node() {
            this.list = new ArrayList<Integer>();
        }

        public void add(int e) {
            list.add(e);
        }

        public int getListLength() {
            return list.size();
        }

        public String toString() {
            String result = "";
            for (Integer i : list) {
                result = result + i + ";";
            }
            return result;
        }
    }
}
