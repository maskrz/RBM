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
public class BoltzmannEntropyStrategy extends CommonStrategyOperations implements SelectionStrategy {

    @Override
    public FloatMatrix calculateVisible(RBMRepository repository) {
        prepareHiddenAndVisible(repository);
        FloatMatrix result = new FloatMatrix(repository.getFeatures(), 1);
        float ne = -calculateFreeEnergy(repository.getVMatrix(), repository);
        for (Integer i : repository.getEntropyCalculator().getUnknownAnswers()) {
            FloatMatrix withOne = repository.getVMatrix().dup();
            withOne.put(i, 0, 1f);

            float pe = -calculateFreeEnergy(withOne, repository);
            float mi = calculateMI(pe, ne);
            float o1 = (float) (-mi * calcLogarithm(mi));
            float o2 = 1 - mi;
            float o3 = (float) calcLogarithm(1 - mi);
            float o4 = o2 * o3;
            float res = o1 - o4;
            result.put(i, 0, res);

        }
        result = removeAnswered(repository, result);
        return result;
    }

    public static float calculateMI(float pp, float pm) {
        float denominator = (float) (1 + Math.exp(pm - pp));
        return 1 / denominator;
    }

    private float calculateFreeEnergy(FloatMatrix currentMatrix, RBMRepository repository) {
        FloatMatrix vCopy = currentMatrix.dup();
        // -b^t*x
        FloatMatrix t1 = repository.getA().transpose().mmul(vCopy);

        float sum = 0;
        int c = repository.getW().columns;
        for (int i = 0; i < c; i++) {
            // W.j ^t
            FloatMatrix column = repository.getW().getColumn(i);
            FloatMatrix m2 = column.transpose();
            //m2 * x
            FloatMatrix m3 = m2.mmul(vCopy);
            float x1 = m3.get(0, 0);
            float x2 = repository.getB().get(i, 0);
            float x3 = x1 + x2;
            float res = calcLogarithm((float) (1 + Math.exp(x3)));
            sum += res;
        }
        float btx = -t1.get(0, 0);
        return btx - sum;

    }

    protected float calcLogarithm(float x) {
        return (float) (Math.log(x) / Math.log(2));
    }

}
