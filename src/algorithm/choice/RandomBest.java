/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.choice;

import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RandomBest implements QuestionChoiceStrategy {

    @Override
    public int selectQuestion(FloatMatrix visible) {
        int[] candidates = new int[2048];
        int candidatesAmount = 0;
        float bestResult = visible.max();
        float threshold = 0.9f * bestResult;
        for (int i = 0; i < visible.rows; i++) {
            float value = visible.get(i, 0);
            if (value >= threshold) {
                candidates[candidatesAmount++] = i;
            }
        }

        int pick = (int) (Math.random() * candidatesAmount);
        return candidates[pick];
    }

}
