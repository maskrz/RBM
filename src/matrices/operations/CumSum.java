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
public class CumSum implements SingleMatrixOperation {

    @Override
    public FloatMatrix performOperation(FloatMatrix matrix) {
        FloatMatrix result = new FloatMatrix(matrix.rows, matrix.columns);
        float sum = 0;
        for(int i = 0; i < matrix.rows; i ++) {
            sum += matrix.get(i, 0);
            result.put(i, 0, sum);
        }

        return result;
    }

}
