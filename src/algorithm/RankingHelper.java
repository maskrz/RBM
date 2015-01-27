/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm;

import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RankingHelper {

    private FloatMatrix v2;
    private FloatMatrix entropy;
    private int[] calculatedValues;
    private FloatMatrix v2Sorted;
    private FloatMatrix entropySorted;
    private int activeNumber;
    private float maxEntropy;
    private float maxV2;

    public void sortV2() {
        FloatMatrix temp = new FloatMatrix(1, activeNumber);
        int actuall = 0;
        for (int i = 0; i < v2.rows; i++) {
            if(calculatedValues[i] < 0) {
                temp.put(0, actuall, v2.get(i, 0));
                actuall ++;
            }
        }
        v2Sorted = temp.sortRows();
        if(activeNumber > 0) {
            maxV2 = v2Sorted.get(0, activeNumber - 1);
        }
    }

    private void sortEntropy() {
        FloatMatrix temp = new FloatMatrix(1, activeNumber);
        int actuall = 0;
        for (int i = 0; i < entropy.rows; i++) {
            if(calculatedValues[i] < 0) {
                temp.put(0, actuall, entropy.get(i, 0));
                actuall ++;
            }
        }
        entropySorted = temp.sortRows();
        // FIXME - check also 36 - add stop after find movie
        if (activeNumber > 0) {
            maxEntropy = entropySorted.get(0, activeNumber - 1);
        }
    }

    public void setupHelper(FloatMatrix v2, FloatMatrix entropy, int[] calculatedValues, int activeNumber) {
        this.v2 = v2;
        this.entropy = entropy;
        this.calculatedValues = calculatedValues;
        this.activeNumber = activeNumber;
        sortV2();
        sortEntropy();
    }

    public float getV2Ranking(float value) {
        for(int i = activeNumber - 1; i >=0; i --) {
            if(value == v2Sorted.get(0, i)) {
                int rank = activeNumber - i;
                return rank;
            }
        }
        return Float.POSITIVE_INFINITY;
    }

    public float getEntropyRanking(float value) {
        for(int i = activeNumber - 1; i >=0; i --) {
            if(value == entropySorted.get(0, i)) {
                int rank = activeNumber - i;
                return rank;
            }
        }
        return Float.POSITIVE_INFINITY;
    }

    public int getActiveNumber() {
        return activeNumber;
    }

    float getV2PercentageRanking(float value) {
        return value/maxV2;
    }

    float getEntropyPercentageRanking(float value) {
        return value/maxEntropy;
    }

    public float getMaxV2() {
        return this.maxV2;
    }

    public float getMaxEntropy() {
        return this.maxEntropy;
    }
}
