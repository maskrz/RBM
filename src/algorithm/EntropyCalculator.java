/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.Arrays;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class EntropyCalculator {

    private FloatMatrix movies;
    private boolean[] matchingMovies;
    private int[] calculatedValues;
    private int matchingMoviesAmount;
    int rows;
    int columns;

    public EntropyCalculator(FloatMatrix movies) {
        this.movies = movies;
        rows = movies.rows;
        columns = movies.columns;
        initializeMatches();
    }

    protected void removeMovie(int id) {
        if (matchingMovies[id]) {
            matchingMovies[id] = false;
            setMatchingMoviesAmount(getMatchingMoviesAmount() - 1);
        }
    }

    protected void initializeMatches() {
        setMatchingMovies(new boolean[rows]);
        Arrays.fill(getMatchingMovies(), true);
        setCalculatedValues(new int[columns]);
        Arrays.fill(getCalculatedValues(), -1);
        setMatchingMoviesAmount(rows);
    }

    protected float calcLogarithm(float x) {
        return (float) (Math.log(x) / Math.log(2));
    }

    public boolean isMatchedMovie(int id) {
        return matchingMovies[id];
    }

    protected int positiveAnswers(int questionId) {
        int sum = 0;
        for (int i = 0; i < rows; i++) {
            if (matchingMovies[i]) {
                sum += movies.get(i, questionId);
            }
        }
        return sum;
    }

    public float calculateEntropyForQuestion(int questionId) {
        float positive = positiveAnswers(questionId);

        if (positive == 0.0 || matchingMoviesAmount == positive) {
//            System.out.println("ANSWERED QUESTION!" + questionId);
            getCalculatedValues()[questionId] = positive == 0.0 ? 0 : 1;
            return 0;
        }
        float px1 = positive / matchingMoviesAmount;
        float px0 = 1 - px1;
        float logpx1 = calcLogarithm(px1);
        float logpx0 = calcLogarithm(px0);
        float ans = -px1 * logpx1 - px0 * logpx0;
        return ans;
    }

    public void calculateForAllQuestions() {
        for (int i = 0; i < columns; i++) {
            calculateEntropyForQuestion(i);
        }
    }

    public void filterMovies(int questionId, int answer) {
        for (int i = 0; i < rows; i++) {
            if (getMovies().get(i, questionId) != answer) {
                removeMovie(i);
            }
        }
        calculateForAllQuestions();
    }

    public int[] answeredQuestions() {
        int[] answered = new int[columns];
        Arrays.fill(answered, -1);
//        System.out.println(Arrays.toString(getCalculatedValues()));
        int j = 0;
        int sum = 0;
        for (int i = 0; i < columns; i ++) {
            if (getCalculatedValues()[i] >= 0) {
                answered[j] = getCalculatedValues()[i];
                sum++;
            }
        }
        return answered;
    }

    public int answeredQuestionsAmount() {
        int sum = 0;
        for (int i = 0; i < columns; i ++) {
            if (getCalculatedValues()[i] >= 0) {
                sum++;
            }
        }
        return sum;
    }

    public FloatMatrix getMovies() {
        return movies;
    }

    public void setMovies(FloatMatrix movies) {
        this.movies = movies;
    }

    public boolean[] getMatchingMovies() {
        return matchingMovies;
    }

    public void setMatchingMovies(boolean[] matchingMovies) {
        this.matchingMovies = matchingMovies;
    }

    public int getMatchingMoviesAmount() {
        return matchingMoviesAmount;
    }

    public void setMatchingMoviesAmount(int matchingMoviesAmount) {
        this.matchingMoviesAmount = matchingMoviesAmount;
    }

    public int[] getCalculatedValues() {
        return calculatedValues;
    }

    public void setCalculatedValues(int[] calculatedValues) {
        this.calculatedValues = calculatedValues;
    }
}
