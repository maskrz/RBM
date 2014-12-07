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
public interface SingleMatrixOperation {
    
    FloatMatrix performOperation(FloatMatrix matrix);
}
