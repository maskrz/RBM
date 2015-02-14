/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private float[] actuallEntropy;
    private ArrayList<Integer> unknownAnswers;
    int rows;
    int columns;

    public EntropyCalculator(FloatMatrix movies) {
        this.movies = movies;
        rows = movies.rows;
        columns = movies.columns;
        actuallEntropy = new float[columns];
        unknownAnswers = new ArrayList<Integer>();
        for (int i = 0; i < columns; i++) {
            unknownAnswers.add(i);
        }
        initializeMatches();
    }

    /**
     * remove movie with given id from matching movies list
     *
     * @param id
     */
    protected void removeMovie(int id) {
        if (matchingMovies[id]) {
            matchingMovies[id] = false;
            setMatchingMoviesAmount(getMatchingMoviesAmount() - 1);
        }
    }

    /**
     * initialize all arrays - matching movies, calculated values set matching
     * movies amount to all movies amount
     */
    protected void initializeMatches() {
        setMatchingMovies(new boolean[rows]);
        Arrays.fill(getMatchingMovies(), true);
        setCalculatedValues(new int[columns]);
        Arrays.fill(getCalculatedValues(), -1);
        setMatchingMoviesAmount(rows);
    }

    /**
     * calculate logarithm with base 2 from given x
     *
     * @param x
     * @return
     */
    protected float calcLogarithm(float x) {
//        return (float) Math.log10(x);
        return (float) (Math.log(x) / Math.log(2));
    }

    /**
     * check if movie match to answers
     *
     * @param id
     * @return
     */
    public boolean isMatchedMovie(int id) {
        return matchingMovies[id];
    }

    /**
     * calculate number of positive answers of given question for all matching
     * movies
     *
     * @param questionId
     * @return
     */
    protected int positiveAnswers(int questionId) {
        int sum = 0;
        for (int i = 0; i < rows; i++) {
            if (matchingMovies[i]) {
                sum += movies.get(i, questionId);
            }
        }
        return sum;
    }

    /**
     * calculate entropy of given question based on matching movies list if
     * entropy is 0, then set value of given on calculated values
     *
     * @param questionId
     * @return
     */
    public float calculateEntropyForQuestion(int questionId) {
        float positive = positiveAnswers(questionId);

        if (positive == 0.0 || matchingMoviesAmount == positive) {
            getCalculatedValues()[questionId] = positive == 0.0 ? 0 : 1;
            getUnknownAnswers().remove(new Integer(questionId));
            return 0;
        }
        float px1 = positive / matchingMoviesAmount;
        float px0 = 1 - px1;
        float logpx1 = calcLogarithm(px1);
        float logpx0 = calcLogarithm(px0);
        float ans = -px1 * logpx1 - px0 * logpx0;
        if(ans == 1) {
//            System.out.println("WOW");
        }
        return ans;
    }

    /**
     * calculate Entropy for all questions
     */
    public void calculateForAllQuestions() {
        for (int i = 0; i < columns; i++) {
            actuallEntropy[i] = calculateEntropyForQuestion(i);
        }
    }

    /**
     * filter movies list - remove movies which do not match answers then
     * extract new answers
     *
     * @param questionId
     * @param answer
     */
    public void filterMovies(int questionId, int answer) {
//        System.out.println(matchingMoviesAmount);
        for (int i = 0; i < rows; i++) {
            if ((getMovies().get(i, questionId) != answer) && (getMovies().get(i, questionId) != 0.5)) {
                removeMovie(i);
            }
        }
        calculateForAllQuestions();
//        System.out.println(matchingMoviesAmount);
    }

    /**
     * create and return array of answers - if question has not been yet
     * answered, then the value is -1
     *
     * @return
     */
    public int[] answeredQuestions() {
        int[] answered = new int[columns];
        Arrays.fill(answered, -1);
        for (int i = 0; i < columns; i++) {
            if (getCalculatedValues()[i] >= 0) {
                answered[i] = getCalculatedValues()[i];
            }
        }
        return answered;
    }

    /**
     * calculate and return number of answered questions
     *
     * @return
     */
    public int answeredQuestionsAmount() {
        int sum = 0;
        for (int i = 0; i < columns; i++) {
            if (getCalculatedValues()[i] >= 0) {
                sum++;
            }
        }
        return sum;
    }

    /**
     * generate ArrayList with ids of answered questions
     *
     * @return
     */
    public List<Integer> getAnsweredQuestionsIds() {
        List<Integer> answered = new ArrayList<Integer>();
        for (int i = 0; i < columns; i++) {
            if (getCalculatedValues()[i] >= 0) {
                answered.add(i);
            }
        }
        return answered;
    }

    /**
     *
     * @return calculated entropy for movie with given id
     */
    public float getEntropyForFeature(int id) {
        return actuallEntropy[id];
    }

    public int positiveAnswersAmount() {
        int sum = 0;

        for (int i = 0; i < columns; i++) {
            if (getCalculatedValues()[i] == 1) {
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

    public float[] getActuallEntropy() {
        return actuallEntropy;
    }

    public ArrayList<Integer> getUnknownAnswers() {
        return unknownAnswers;
    }

    public void setUnknownAnswers(ArrayList<Integer> unknownAnswers) {
        this.unknownAnswers = unknownAnswers;
    }
}
