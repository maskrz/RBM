/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.selection;

import algorithm.RBMRepository;
import static matrices.operations.MatrixOperation.ADVERSE;
import static matrices.operations.MatrixOperation.EXP;
import static matrices.operations.MatrixOperation.INCREMENT;
import static matrices.operations.MatrixOperation.INVERSE;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class PureBoltzmannStrategy extends CommonStrategyOperations implements SelectionStrategy {

    @Override
    public FloatMatrix calculateVisible(RBMRepository repository) {
        prepareHiddenAndVisible(repository);
        FloatMatrix result = cmf.multipleMatrixOperations(
                repository.getA().add(repository.getW().mmul(repository.getH1())),
                ADVERSE, EXP, INCREMENT, INVERSE);
        result = removeAnswered(repository, result);
        return result;
    }

}
