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
public class ZerosMatrix implements SingleMatrixOperation {

    @Override
    public FloatMatrix performOperation(FloatMatrix matrix) {
        FloatMatrix result = new FloatMatrix(matrix.rows, matrix.columns);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result.put(i, j, 0);
            }
        }
        return result;
    }

}
