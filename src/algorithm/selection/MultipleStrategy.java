/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.selection;

import algorithm.RBMRepository;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class MultipleStrategy extends CommonStrategyOperations implements SelectionStrategy  {

    @Override
    public FloatMatrix calculateVisible(RBMRepository repository) {
        prepareHiddenAndVisible(repository);
        FloatMatrix result = new FloatMatrix(repository.getFeatures(), 1);
        for (int i = 0; i < repository.getFeatures(); i++) {
            result.put(i, 0, repository.getV2().get(i, 0)
                            * repository.getEntropyCalculator().getEntropyForFeature(i));
        }
        result = removeAnswered(repository, result);
        return result;
    }
}
