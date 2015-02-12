/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.statistics.StatisticsHandler;
import java.util.Arrays;
import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.ADVERSE;
import static matrices.operations.MatrixOperation.CUMSUM;
import static matrices.operations.MatrixOperation.EXP;
import static matrices.operations.MatrixOperation.INCREMENT;
import static matrices.operations.MatrixOperation.INVERSE;
import static matrices.operations.MatrixOperation.NORMALIZE;
import static matrices.operations.MatrixOperation.ZEROS;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RBMHelper {

    private CalculatedMatrixFactory cmf;
    private RBMRepository repository;

    public RBMHelper(RBMRepository repository) {
        this.cmf = new CalculatedMatrixFactory();
        this.repository = repository;
    }

    public double sum(FloatMatrix m) {
        return m.sum();
    }

    public void generateXMatrix(int movieId) {
        float[][] result = new float[1][repository.getFeatures()];
        for (int i = 0; i < repository.getFeatures(); i++) {
            result[0][i] = repository.getDataSet().get(movieId, i);
        }
        repository.setXMatrix(new FloatMatrix(result));
    }

    public void generateVMatrix() {
        repository.setVMatrix(cmf.singleMatrixOperation(new FloatMatrix(repository.getFeatures(), 1), ZEROS));
    }

    public void calculateH1() {
        FloatMatrix result = cmf.multipleMatrixOperations(
                repository.getB().transpose().add(
                        repository.getVMatrix().transpose().mmul(
                                repository.getW())),
                ADVERSE, EXP, INCREMENT, INVERSE)
                .transpose();
        repository.setH1(result);
    }

    public FloatMatrix lessThan(FloatMatrix matrix, float parameter) {
        return matrix.lt(parameter);
    }

    public void calculateV2() {
        FloatMatrix result = cmf.multipleMatrixOperations(
                repository.getA().add(repository.getW().mmul(repository.getH1())),
                ADVERSE, EXP, INCREMENT, INVERSE);
        repository.setV2(result);
    }

    public void setUpEntropyCalculator() {
        repository.setEntropyCalculator(new EntropyCalculator(repository.getDataSet().dup()));
    }

    void generateAnswerArrays() {
        int[] ids = new int[repository.getQuestions()];
        int[] answered = new int[repository.getFeatures()];
        Arrays.fill(ids, -1);
        Arrays.fill(answered, -1);
        repository.setIds(ids);
        repository.setAnswered(answered);
    }

    void calculateBasicEntropy() {
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().calculateForAllQuestions();
        }
    }

    void setFilterMovies(boolean entropy) {
        repository.setFilterMovies(entropy);
    }

    void removeAnswered() {
        for (int i = 0; i < repository.getFeatures(); i++) {
            if (repository.getAnswered()[i] != -1) {
                repository.getV2().put(i, 0, 0);
            }
        }
    }

    void includeEntropy() {
        switch (repository.getSelectionHelperType()) {
            case NONE:
                break;
            case MULTIPLE:
                for (int i = 0; i < repository.getFeatures(); i++) {
                    repository.getV2().put(i, 0, repository.getV2().get(i, 0) *
                            repository.getEntropyCalculator().getEntropyForFeature(i));
                }
                break;
            case ADD:
                for (int i = 0; i < repository.getFeatures(); i++) {
                    repository.getV2().put(i, 0, repository.getV2().get(i, 0) + repository.getEntropyCalculator().getEntropyForFeature(i));
                }
                break;
            case ONLY_ENTROPY:
                for (int i = 0; i < repository.getFeatures(); i++) {
                    repository.getV2().put(i, 0, repository.getEntropyCalculator().getEntropyForFeature(i));
                }
                break;
            case RANKING:
                createAndSetRanking();
                break;
            case PERCENTAGE_RANKING:
                createAndSetPercentageRanking();
                break;
            default:
                throw new RuntimeException("BAD TYPE");
        }
    }

    void setSelectionHelperType(SelectionHelperType selectionHelperType) {
        repository.setSelectionHelperType(selectionHelperType);
    }

    private void createAndSetRanking() {
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
            repository.getV2().put(i, 0, powered);
        }
    }

    private void createAndSetPercentageRanking() {
        repository.getRankingHelper().setupHelper(repository.getV2(),
                new FloatMatrix(repository.getEntropyCalculator().getActuallEntropy()),
                repository.getEntropyCalculator().getCalculatedValues(),
                repository.getFeatures() - repository.getEntropyCalculator().answeredQuestionsAmount());

        for (int i = 0; i < repository.getFeatures(); i++) {
            float value = repository.getV2().get(i, 0);
            float v2Rank = repository.getRankingHelper().getV2PercentageRanking(value);
            value = repository.getEntropyCalculator().getEntropyForFeature(i);
            float entropyRank = repository.getRankingHelper().getEntropyPercentageRanking(value);
            float d = 2f * (v2Rank + entropyRank);
            float powered = (float) Math.pow(d, 5);
            repository.getV2().put(i, 0, powered);
        }
    }

    void askQuestion(int questionId) {
        FloatMatrix px = cmf.singleMatrixOperation(repository.getV2(), NORMALIZE);
        float r = (float) Math.random();
        FloatMatrix cumsum = cmf.singleMatrixOperation(px, CUMSUM);
        FloatMatrix l = lessThan(cumsum, r);
        int id = (int) sum(l);
        System.out.println(id);
        repository.getIds()[questionId] = id;
        float answer = repository.getXMatrix().get(0, id);
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().getCalculatedValues()[id] = (int) answer;
        }
        repository.getAnswered()[id] = (int) answer;
        if (repository.getFilterMovies()) {
            repository.getEntropyCalculator().filterMovies(id, (int) answer);
        }
        repository.getVMatrix().put(id, 0, answer);

        repository.getOrder()[id].add(questionId);
        if (repository.getFilterMovies()) {
            repository.setAnswered(repository.getEntropyCalculator().answeredQuestions());
        }
    }

    public int calculateAnswers(StatisticsHandler statisticsHandler) {
        int[] matches = new int[repository.getConcepts()];
        Arrays.fill(matches, -1);
        int actuall = 0;
        for (int i = 0; i < repository.getConcepts(); i++) {
            int j = 0;
            boolean match = true;
            while (j < repository.getFeatures()) {
                if (repository.getAnswered()[j] != -1 && (repository.getDataSet().get(i, j) != repository.getAnswered()[j] && repository.getDataSet().get(i, j) != 0.5f)) {
                    match = false;
                }
                j++;
            }
            if (match) {
                matches[actuall] = i;
                actuall++;
            }
        }
        System.out.println("Matches: " + actuall);
        String matchedMovies = "";
        for (int i = 0; i < actuall; i++) {
            matchedMovies += matches[i] + " ";
        }
        statisticsHandler.addMatchedMoviesInfo(actuall, matchedMovies);
        System.out.println(matchedMovies);
        return actuall;
    }

    int getActiveMovies() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
