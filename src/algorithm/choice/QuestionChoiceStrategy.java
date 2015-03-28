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
public interface QuestionChoiceStrategy {
    public int selectQuestion(FloatMatrix visible);
}
