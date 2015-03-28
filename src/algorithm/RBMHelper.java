/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.choice.QuestionChoiceStrategy;
import algorithm.selection.SelectionStrategy;
import algorithm.statistics.StatisticsHandler;
import java.util.ArrayList;
import java.util.Arrays;
import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.ZEROS;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RBMHelper {

    private CalculatedMatrixFactory cmf;
    private RBMRepository repository;
    FloatMatrix temp;

    public RBMHelper(RBMRepository repository) {
        this.cmf = new CalculatedMatrixFactory();
        this.repository = repository;
        temp = new FloatMatrix(repository.getFeatures(), 0);
    }

    public void generateXMatrix(int movieId) {
        float[][] result = new float[1][repository.getFeatures()];
        for (int i = 0; i < repository.getFeatures(); i++) {
            result[0][i] = repository.getDataSet().get(movieId, i);
        }
        repository.setXMatrix(new FloatMatrix(result));
    }

    public void generateVMatrix() {
        FloatMatrix zeros = cmf.singleMatrixOperation(new FloatMatrix(repository.getFeatures(), 1), ZEROS);
        repository.setVMatrix(zeros);
    }

    public void setUpEntropyCalculator() {
        repository.setEntropyCalculator(new EntropyCalculator(repository.getDataSet().dup()));
    }

    void generateAnswerArrays() {
        int[] ids = new int[repository.getQuestions()];
        int[] answered = new int[repository.getFeatures()];
        Arrays.fill(ids, -1);
        Arrays.fill(answered, -1);
        repository.setIds(ids);
        repository.setAnswered(answered);
    }

    void calculateBasicEntropy() {
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().calculateForAllQuestions();
        }
    }

    void setFilterMovies(boolean entropy) {
        repository.setFilterMovies(entropy);
    }

    void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        repository.setSelectionStrategy(selectionStrategy);
    }

    void setQuestionChoiceStrategy(QuestionChoiceStrategy questionChoiceStrategy) {
        repository.setQuestionChoiceStrategy(questionChoiceStrategy);
    }

    public int calculateAnswers(StatisticsHandler statisticsHandler, int questions) {
        int[] matches = new int[repository.getConcepts()];
        Arrays.fill(matches, -1);
        int actuall = 0;
        for (int i = 0; i < repository.getConcepts(); i++) {
            int j = 0;
            boolean match = true;
            while (j < repository.getFeatures()) {
                if (repository.getAnswered()[j] != -1 && (repository.getDataSet().get(i, j) != repository.getAnswered()[j] && repository.getDataSet().get(i, j) != 0.5f)) {
                    match = false;
                }
                j++;
            }
            if (match) {
                matches[actuall] = i;
                actuall++;
            }
        }
//        System.out.println("Matches: " + actuall);
        String matchedMovies = "";
        for (int i = 0; i < actuall; i++) {
            matchedMovies += matches[i] + " ";
        }
        statisticsHandler.addMatchedMoviesInfo(actuall, matchedMovies, questions);
//        System.out.println(matchedMovies);
        return actuall;
    }

    int getUnknownAsweresAmount() {
        return repository.getEntropyCalculator().getUnknownAnswers().size();
    }

    ArrayList<Integer> getUnknownAsweres() {
        return repository.getEntropyCalculator().getUnknownAnswers();
    }

    int getAnsweredAmount() {
        return repository.getEntropyCalculator().answeredQuestionsAmount();
    }

    int positiveAnswersAmount() {
        return repository.getEntropyCalculator().positiveAnswersAmount();
    }

    private void updateVMatrix() {
        for (int i = 0; i < repository.getFeatures(); i++) {
            float calculatedValue = repository.getEntropyCalculator().getCalculatedValues()[i];
            if (calculatedValue >= 0) {
                repository.getVMatrix().put(i, 0, calculatedValue);
            }
        }
    }

    public int countPositive(FloatMatrix matrix) {
        int counter = 0;
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                if (matrix.get(i, j) == 1) {
                    counter++;
                }
            }
        }
        return counter;
    }

    void process(int questionId) {
        FloatMatrix result = repository.getSelectionStrategy().calculateVisible(repository);
        int id = repository.getQuestionChoiceStrategy().selectQuestion(result);
        repository.getIds()[questionId] = id;
        float answer = repository.getXMatrix().get(0, id);
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().getCalculatedValues()[id] = (int) answer;
        }
        repository.getAnswered()[id] = (int) answer;
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().filterMovies(id, (int) answer);
        }
        repository.getVMatrix().put(id, 0, answer);

        repository.getOrder()[id].add(questionId);
        if (repository.getFilterMovies()) {
            repository.setAnswered(repository.getEntropyCalculator().answeredQuestions());
            updateVMatrix();
        }
    }
}
