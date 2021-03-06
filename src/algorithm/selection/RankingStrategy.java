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
public class RankingStrategy extends CommonStrategyOperations implements SelectionStrategy  {

    @Override
    public FloatMatrix calculateVisible(RBMRepository repository) {
        prepareHiddenAndVisible(repository);
        FloatMatrix result = new FloatMatrix(repository.getFeatures(), 1);

        repository.getRankingHelper().setupHelper(repository.getV2(),
                new FloatMatrix(repository.getEntropyCalculator().getActuallEntropy()),
                repository.getEntropyCalculator().getCalculatedValues(),
                repository.getFeatures() - repository.getEntropyCalculator().answeredQuestionsAmount());

        for (int i = 0; i < repository.getFeatures(); i++) {
            float value = repository.getV2().get(i, 0);
            float v2Rank = repository.getRankingHelper().getV2Ranking(value);
            value = repository.getEntropyCalculator().getEntropyForFeature(i);
            float entropyRank = repository.getRankingHelper().getEntropyRanking(value);
            float u = repository.getRankingHelper().getActiveNumber();
            float d = 10f * (v2Rank + entropyRank);
            float rankingValue = u / d;
            float powered = (float) Math.pow(rankingValue, 2);
            result.put(i, 0, powered);
        }
        result = removeAnswered(repository, result);
        return result;
    }


}
