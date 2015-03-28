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
public interface SelectionStrategy {

    public FloatMatrix calculateVisible(RBMRepository repository);
}
