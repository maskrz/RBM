/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matrices.operations;

import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
class DifferenceAbs implements TwoMatricesOperation {

    @Override
    public FloatMatrix performOperation(FloatMatrix matrix1, FloatMatrix matrix2) {
        FloatMatrix result = new FloatMatrix(matrix1.rows, matrix1.columns);
        for (int i = 0; i < matrix1.rows; i++) {
            for (int j = 0; j < matrix1.columns; j++) {
                result.put(i, j, Math.abs(matrix1.get(i, j) - matrix2.get(i, j)));
            }
        }
        return result;
    }

}
