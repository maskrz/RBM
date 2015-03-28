/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.selection;

import algorithm.RBMRepository;
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
public class CommonStrategyOperations {
    CalculatedMatrixFactory cmf;
    
    void prepareHiddenAndVisible(RBMRepository repository) {
        cmf = new CalculatedMatrixFactory();
        calculateH1(cmf, repository);
        calculateV2(cmf, repository);

    }

    public void calculateH1(CalculatedMatrixFactory cmf, RBMRepository repository) {
        FloatMatrix result = cmf.multipleMatrixOperations(
                repository.getB().transpose().add(
                        repository.getVMatrix().transpose().mmul(
                                repository.getW())),
                ADVERSE, EXP, INCREMENT, INVERSE)
                .transpose();
        repository.setH1(result);
    }

    public void calculateV2(CalculatedMatrixFactory cmf, RBMRepository repository) {
        FloatMatrix result = cmf.multipleMatrixOperations(
                repository.getA().add(repository.getW().mmul(repository.getH1())),
                ADVERSE, EXP, INCREMENT, INVERSE);
        repository.setV2(result);
    }

    FloatMatrix removeAnswered(RBMRepository repository, FloatMatrix visible) {
        for (int i = 0; i < repository.getFeatures(); i++) {
            if (repository.getAnswered()[i] != -1) {
                visible.put(i, 0, 0);
            }
        }
        return visible;
    }
}
