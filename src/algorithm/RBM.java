/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.statistics.StatisticsHandler;
import matrices.operations.CalculatedMatrixFactory;
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
        while (j < repository.getQuestions() && rbmHelper.getUnknownAsweresAmount() > 1) {
            rbmHelper.calculateH1();
            rbmHelper.calculateV2();
            rbmHelper.removeAnswered();
            rbmHelper.includeEntropy();
//            System.out.println("before");
//            long s1 = System.currentTimeMillis();
            rbmHelper.askQuestion(j);
//            long e1 = System.currentTimeMillis();
//            System.out.println("after");
//            System.out.println(e1-s1);
            System.out.println(j + " -- " + rbmHelper.getAnsweredAmount() + " -- "+ rbmHelper.positiveAnswersAmount());
//            System.out.println(rbmHelper.getUnknownAsweresAmount());
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
}
