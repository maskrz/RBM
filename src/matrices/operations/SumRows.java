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
class SumRows implements SingleMatrixOperation {

    @Override
    public FloatMatrix performOperation(FloatMatrix matrix) {
        FloatMatrix result = new FloatMatrix(matrix.rows, 1);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result.put(i, 0, result.get(i, 0) + matrix.get(i, j));
            }
        }
        return result;
    }

}
