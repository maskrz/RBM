/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.ADVERSE;
import static matrices.operations.MatrixOperation.EXP;
import static matrices.operations.MatrixOperation.INCREMENT;
import static matrices.operations.MatrixOperation.INVERSE;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RBMHelper {

    private CalculatedMatrixFactory cmf;

    public RBMHelper () {
        this.cmf = new CalculatedMatrixFactory();
    }

    public double sum(FloatMatrix m) {
        return m.sum();
    }
    
    public FloatMatrix generateXMatrix(int movieId, int features, FloatMatrix dataSet) {
        float[][] result = new float[1][features];
        for (int i = 0; i < features; i++) {
            result[0][i] = dataSet.get(movieId, i);
        }
        return new FloatMatrix(result);
    }

    public FloatMatrix lessThan(FloatMatrix matrix, float parameter) {
        return matrix.lt(parameter);
    }

    public FloatMatrix calculateH1(FloatMatrix b, FloatMatrix vMatrix, FloatMatrix w) {
        FloatMatrix result = cmf.multipleMatrixOperations(
                b.transpose().add(vMatrix.transpose().mmul(w)),
                ADVERSE, EXP, INCREMENT, INVERSE)
                .transpose();
        return result;
    }

    public FloatMatrix calculateV2(FloatMatrix a, FloatMatrix w, FloatMatrix h1) {
        FloatMatrix result = cmf.multipleMatrixOperations(
                a.add(w.mmul(h1)),
                ADVERSE, EXP, INCREMENT, INVERSE);
        return result;
    }
}
