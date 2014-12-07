/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm;

import junit.framework.Assert;
import org.jblas.FloatMatrix;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Skrzypek
 */
public class EntropyCalculatorTest {

    private FloatMatrix movies;
    private EntropyCalculator entropyCalculator;
    public EntropyCalculatorTest() {
    }

    @Before
    public void setUp() {
        float[][] array = {
            {1, 1, 1, 0, 0, 0, 1, 0, 1, 0},
            {1, 1, 0, 0, 1, 1, 0, 0, 1, 0},
            {1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
            {1, 1, 0, 0, 0, 0, 1, 0, 1, 1},
            {1, 0, 1, 1, 0, 0, 1, 0, 1, 1}};
        this.movies = new FloatMatrix(array);
        this.entropyCalculator = new EntropyCalculator(movies);
    }

    @Test
    public void testRemoveMovie() {
        System.out.println("removeMovie");
        int id = 3;
        entropyCalculator.removeMovie(id);
        Assert.assertEquals(5, entropyCalculator.getMatchingMoviesAmount());
        Assert.assertFalse(entropyCalculator.isMatchedMovie(id));
    }

    @Test
    public void testLogarithm() {
        System.out.println("calcLogarithm");
        Assert.assertEquals(3.321, entropyCalculator.calcLogarithm(10), 0.1);
        Assert.assertEquals(0, entropyCalculator.calcLogarithm(1), 0.1);
        Assert.assertEquals(2, entropyCalculator.calcLogarithm(4), 0.1);
        Assert.assertEquals(-1, entropyCalculator.calcLogarithm(0.5f), 0.1);
    }

    @Test
    public void testLogarithm_0() {
        System.out.println("calcLogarithm_0");
        Assert.assertEquals(Float.NEGATIVE_INFINITY, entropyCalculator.calcLogarithm(0), 0.1);
    }

    @Test
    public void testPositiveAnswers() {
        System.out.println("positiveAnswers");
        Assert.assertEquals(6, entropyCalculator.positiveAnswers(0));
        Assert.assertEquals(3, entropyCalculator.positiveAnswers(2));
        Assert.assertEquals(0, entropyCalculator.positiveAnswers(7));
    }

    @Test
    public void testCalculateEntropyForQuestion() {
        System.out.println("CalculateEntropyForQuestion");
        Assert.assertEquals(1, entropyCalculator.calculateEntropyForQuestion(1), 0.02);
        Assert.assertEquals(0.65, entropyCalculator.calculateEntropyForQuestion(5), 0.02);
        Assert.assertEquals(0.91, entropyCalculator.calculateEntropyForQuestion(4), 0.02);
        entropyCalculator.removeMovie(3);
        entropyCalculator.removeMovie(4);
        entropyCalculator.removeMovie(5);
        Assert.assertEquals(0, entropyCalculator.calculateEntropyForQuestion(9), 0.02);
        entropyCalculator.removeMovie(0);
        Assert.assertEquals(0, entropyCalculator.calculateEntropyForQuestion(4), 0.02);

        Assert.assertEquals(-1, entropyCalculator.getCalculatedValues()[1]);
        Assert.assertEquals(0, entropyCalculator.getCalculatedValues()[9]);
        Assert.assertEquals(1, entropyCalculator.getCalculatedValues()[4]);
    }

}
