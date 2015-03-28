/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.choice.QuestionChoiceStrategy;
import algorithm.selection.SelectionStrategy;
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
    SelectionStrategy selectionStrategy;
    QuestionChoiceStrategy questionChoiceStrategy;
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
        executeForAll(filterMovies, selectionStrategy, questionChoiceStrategy);
    }

    public void executeForAll(boolean entropy, SelectionStrategy selectionStrategy, QuestionChoiceStrategy questionChoiceStrategy) {
        // starts from 1 because of movie numbering
        for (int i = 1; i < repository.getConcepts() + 1; i++) {
//            System.out.println("Film nr: " + i);
            rbmHelper.setUpEntropyCalculator();
            statisticsHandler.setMainInfoMovieId(i - 1);
            mainFrame.setProgress("Film nr " + i + " z " + repository.getConcepts());
            long start = System.currentTimeMillis();
            int similar = recognizeMovie(i, entropy, selectionStrategy, questionChoiceStrategy);
            long end = System.currentTimeMillis();
//            System.out.println("------------");
            float time = (end - start) / 1000;
            mainFrame.setOther("Ostatni film byl jednym z: " + similar);
        }
        //statistics
        statisticsHandler.handleStatistics(repository.getFeatures(), repository.getOrder(), selectionStrategy, questionChoiceStrategy, repository.getQuestions());
    }

    /**
     * recognize movie using questions selection based on RBM
     *
     * @param movieId
     * @param filterMovies
     * @param selectionHelperType
     * @return
     */
    public int recognizeMovie(int movieId, boolean filterMovies, SelectionStrategy selectionStrategy, QuestionChoiceStrategy questionChoiceStrategy) {
        rbmHelper.setSelectionStrategy(selectionStrategy);
        rbmHelper.setQuestionChoiceStrategy(questionChoiceStrategy);
        rbmHelper.setFilterMovies(filterMovies);
        rbmHelper.generateXMatrix(movieId - 1);
        rbmHelper.generateVMatrix();
        rbmHelper.generateAnswerArrays();
        rbmHelper.calculateBasicEntropy();
        int j = 0;
        while (j < repository.getQuestions() && rbmHelper.getUnknownAsweresAmount() > 1) {
//            System.out.println("Pytanie: " + j);
            rbmHelper.process(j);
//            System.out.println(j + " -- " + rbmHelper.getAnsweredAmount() + " -- " + rbmHelper.positiveAnswersAmount());
//            System.out.println(rbmHelper.getUnknownAsweresAmount());
            j++;
        }
        int similiar = rbmHelper.calculateAnswers(statisticsHandler, j);
//        System.out.println(j);

        return similiar;

    }

    public void setFilterMovies(boolean filterMovies) {
        this.filterMovies = filterMovies;
        repository.setFilterMovies(filterMovies);
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
        repository.setSelectionStrategy(selectionStrategy);
    }

    public void setQuestionChoiceStrategy(QuestionChoiceStrategy questionChoiceStrategy) {
        this.questionChoiceStrategy = questionChoiceStrategy;
        repository.setQuestionChoiceStrategy(questionChoiceStrategy);
    }
}
