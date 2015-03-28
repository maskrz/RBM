/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.choice;

import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.CUMSUM;
import static matrices.operations.MatrixOperation.NORMALIZE;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RandomFromProbabilities implements QuestionChoiceStrategy {

    @Override
    public int selectQuestion(FloatMatrix probabilities) {
        CalculatedMatrixFactory cmf = new CalculatedMatrixFactory();
        FloatMatrix px = cmf.singleMatrixOperation(probabilities, NORMALIZE);
        float r = (float) Math.random();
        FloatMatrix cumsum = cmf.singleMatrixOperation(px, CUMSUM);
        FloatMatrix l = lessThan(cumsum, r);
        int id = (int) sum(l);
        return id;
    }

    public FloatMatrix lessThan(FloatMatrix matrix, float parameter) {
        return matrix.lt(parameter);
    }

    public double sum(FloatMatrix m) {
        return m.sum();
    }

}
