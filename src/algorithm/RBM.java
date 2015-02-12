/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.statistics.StatisticsHandler;
import matrices.operations.CalculatedMatrixFactory;
import org.jblas.FloatMatrix;
import rbm.MainFrame;

/**
 *
 * @author Skrzypek
 */
public class RBM extends Thread {


    private CalculatedMatrixFactory cmf;

    private MainFrame mainFrame;
    private boolean filterMovies;
    SelectionHelperType selectionHelperType;
    StatisticsHandler statisticsHandler;
    RBMHelper rbmHelper;
    RBMRepository repository;

    public RBM(RBMRepository repository, MainFrame mainFrame) {
        this.repository = repository;
        this.cmf = new CalculatedMatrixFactory();
        this.mainFrame = mainFrame;
        statisticsHandler = new StatisticsHandler();
        rbmHelper = new RBMHelper(repository);
    }

    @Override
    public void run() {
        executeForAll(filterMovies, selectionHelperType);
    }

    public void executeForAll(boolean entropy, SelectionHelperType selectionHelperType) {
        // starts from 1 because of movie numbering
        for (int i = 1; i < repository.getConcepts() + 1; i++) {
            System.out.println("Film nr: " + i);
            rbmHelper.setUpEntropyCalculator();
            statisticsHandler.setMainInfoMovieId(i - 1);
            mainFrame.setProgress("Film nr " + i + " z " + repository.getConcepts());
            long start = System.currentTimeMillis();
            int similar = recognizeMovie(i, entropy, selectionHelperType);
            long end = System.currentTimeMillis();
            System.out.println("------------");
            float time = (end - start) / 1000;
            mainFrame.setOther("Ostatni film byl jednym z: " + similar);
        }
        //statistics
        statisticsHandler.handleStatistics(repository.getFeatures(), repository.getOrder(), selectionHelperType, repository.getQuestions());
    }

    /**
     * recognize movie using questions selection based on RBM
     *
     * @param movieId
     * @param filterMovies
     * @param selectionHelperType
     * @return
     */
    public int recognizeMovie(int movieId, boolean filterMovies, SelectionHelperType selectionHelperType) {
        rbmHelper.setSelectionHelperType(selectionHelperType);
        rbmHelper.setFilterMovies(filterMovies);
        System.out.println(movieId);
        rbmHelper.generateXMatrix(movieId - 1);
        rbmHelper.generateVMatrix();
        rbmHelper.generateAnswerArrays();
        rbmHelper.calculateBasicEntropy();
        int j = 0;
        while (j < repository.getQuestions() && rbmHelper.getActiveMovies() > 1) {
            rbmHelper.calculateH1();
            rbmHelper.calculateV2();
            rbmHelper.removeAnswered();
            rbmHelper.includeEntropy();
            System.out.println("before");
            long s1 = System.currentTimeMillis();
            funkcjaKuby();
            long e1 = System.currentTimeMillis();
            System.out.println("after");
            System.out.println(e1-s1);
//            calculateFreeEnergy(vMatrix);
            rbmHelper.askQuestion(j);
            j++;
        }
        int similiar = rbmHelper.calculateAnswers(statisticsHandler);

        return similiar;

    }

    public void setFilterMovies(boolean filterMovies) {

        repository.setFilterMovies(filterMovies);
    }

    public void setSelectionHelperType(SelectionHelperType selectionHelperType) {
        repository.setSelectionHelperType(selectionHelperType);
    }

    private void funkcjaKuby() {
        float ne = -calculateFreeEnergy(repository.getVMatrix());
        float[] entropies = new float[repository.getFeatures()];
        for (int i = 0; i < repository.getFeatures(); i++) {
            FloatMatrix withOne = repository.getVMatrix().dup();
            withOne.put(i, 0, 1f);

            float pe = -calculateFreeEnergy(withOne);
//            System.out.println(ne);
            float mi = calculateMI(pe , ne);
            float o1 = (float) (-mi * Math.log10(mi));
            float o2 = 1 - mi;
            float o3 = (float) Math.log10(1 - mi);
            float o4 = o2 * o3;
            float res = o1 - o4;
            entropies[i] = res;
//            System.out.println(res);
        }
        System.out.println("s");
    }

    public static float calculateMI(float pp, float pm) {
        float denominator = (float) (1 + Math.exp(pm - pp));
        return 1 / denominator;
    }

    private float calculateFreeEnergy(FloatMatrix currentMatrix) {
        FloatMatrix vCopy = currentMatrix.dup();
        // -b^t*x
        FloatMatrix t1 = repository.getA().transpose().mmul(vCopy);

        float sum = 0;
        int c = repository.getW().columns;
        for (int i = 0; i < c; i++) {
            // W.j ^t
            FloatMatrix column = repository.getW().getColumn(i);
            FloatMatrix m2 = column.transpose();
            //m2 * x
            FloatMatrix m3 = m2.mmul(vCopy);
            float x1 = m3.get(0, 0);
            float x2 = repository.getB().get(i, 0);
            float x3 = x1 + x2;
            float res = (float) Math.log10(1 + Math.exp(x3));
            sum += res;
        }
        float btx = -t1.get(0, 0);
        return btx - sum;

    }

}
