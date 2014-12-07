/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package matrices.operations;

import java.util.HashMap;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
class DefaultMatrices {

    private HashMap<String, FloatMatrix> matrices;

    public DefaultMatrices() {
        matrices = new HashMap<String, FloatMatrix>();
    }

    public FloatMatrix getMatrix(int rows, int columns, float value) {
        FloatMatrix result = matrices.get(rows + "-" + columns + "-" + value);
        return  result != null? result : addMatrix(rows, columns, value);
    }

    private FloatMatrix addMatrix(int rows, int columns, float value) {
        FloatMatrix result = new FloatMatrix(1, 1);
        result.put(0, 0, value);
        result = result.repmat(rows, columns);
        matrices.put(rows + "-" + columns + "-" + value, result);
        return result;
    }

    public int size() {
        return matrices.size();
    }
    
}
