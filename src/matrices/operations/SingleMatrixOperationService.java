/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matrices.operations;

import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;

/**
 *
 * @author Skrzypek
 */
public class SingleMatrixOperationService {



    public FloatMatrix performOperation(FloatMatrix matrix, MatrixOperation operation) {
        switch (operation) {
            case EXP:
                return expElements(matrix);
            case ADVERSE:
                return negateElements(matrix);
            case SUM_COLUMNS:
                return sumColumns(matrix);
            case SUM_ROWS:
                return sumRows(matrix);
            case NORMALIZE:
                return normalizeMatrix(matrix);
            case CUMSUM:
                return cumsum(matrix);
            default:
                return null;
        }
    }



    private FloatMatrix expElements(FloatMatrix matrix) {
        return MatrixFunctions.expi(matrix);
    }

    private FloatMatrix negateElements(FloatMatrix matrix) {
        return matrix.negi();
    }

    private FloatMatrix sumColumns(FloatMatrix matrix) {
        return matrix.columnSums();
    }

    private FloatMatrix sumRows(FloatMatrix matrix) {
        return matrix.rowSums();
    }

    private FloatMatrix cumsum(FloatMatrix matrix) {
        return matrix.cumulativeSumi();
    }

    private FloatMatrix normalizeMatrix(FloatMatrix matrix) {
        float sum = matrix.sum();
        FloatMatrix result = new FloatMatrix(matrix.rows, matrix.columns);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j ++) {
                result.put(i, j, matrix.get(i, j)/sum);
            }
        }
        return result;
    }
}
