/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.statistics.StatisticsHandler;
import java.util.Arrays;
import matrices.operations.CalculatedMatrixFactory;
import static matrices.operations.MatrixOperation.CUMSUM;
import static matrices.operations.MatrixOperation.NORMALIZE;
import static matrices.operations.MatrixOperation.ZEROS;
import org.jblas.FloatMatrix;
import rbm.MainFrame;

/**
 *
 * @author Skrzypek
 */
public class RBM extends Thread {

    // number of questions
    private int questions;
    // number of concepts (movies)
    private int concepts;
    // numbet of features
    private int features;
    //sprasity of correct answers
    private double q;

    private FloatMatrix dataSet;

    private FloatMatrix w;
    private FloatMatrix a;
    private FloatMatrix b;
    private FloatMatrix xMatrix;
    private FloatMatrix vMatrix;
    private double acc;

    private CalculatedMatrixFactory cmf;
    private FloatMatrix h1;
    private FloatMatrix v2;

    private MainFrame mainFrame;
    private String newLine;
    private Node[] order;
    EntropyCalculator entropyCalculator;
    private boolean filterMovies;
    SelectionHelperType selectionHelperType;
    RankingHelper rankingHelper;
    StatisticsHandler statisticsHandler;
    RBMHelper rbmHelper;

    public RBM(FloatMatrix a, FloatMatrix b, FloatMatrix w, int questions, FloatMatrix dataSet, MainFrame mainFrame) {
        this.a = a;
        this.b = b;
        this.w = w;
        this.questions = questions;
        this.cmf = new CalculatedMatrixFactory();
        this.dataSet = dataSet;
        this.mainFrame = mainFrame;
        this.entropyCalculator = new EntropyCalculator(dataSet.copy(dataSet));
        rankingHelper = new RankingHelper();
        newLine = System.getProperty("line.separator");
        setParameters();
        this.order = new Node[features];
        for (int i = 0; i < features; i++) {
            order[i] = new Node();
        }
        statisticsHandler = new StatisticsHandler();
        rbmHelper = new RBMHelper();
    }

    @Override
    public void run() {
        executeForAll(filterMovies, selectionHelperType);
    }

    public void executeForAll(boolean entropy, SelectionHelperType selectionHelperType) {
        // starts from 1 because of movie numbering
        this.selectionHelperType = selectionHelperType;
        for (int i = 1; i < concepts + 1; i++) {
            System.out.println("Film nr: " + i);
            this.entropyCalculator = new EntropyCalculator(dataSet.dup());
            statisticsHandler.setMainInfoMovieId(i - 1);
            mainFrame.setProgress("Film nr " + i + " z " + concepts);
            long start = System.currentTimeMillis();
            int similar = recognizeMovie(i, entropy, selectionHelperType);
            long end = System.currentTimeMillis();
            System.out.println("------------");
            float time = (end - start) / 1000;
            mainFrame.setOther("Ostatni film byl jednym z: " + similar);
        }
        //statistics
        statisticsHandler.handleStatistics(features, order, selectionHelperType, questions);
    }

    /**
     * recognize movie using questions selection based on RBM
     *
     * @param movieId
     * @param filterMovies
     * @param selectionHelperType
     * @return
     */
    public int recognizeMovie(int movieId, boolean filterMovies, SelectionHelperType selectionHelperType) {
        if (this.selectionHelperType == null) {
            this.selectionHelperType = selectionHelperType;
        }
        xMatrix = rbmHelper.generateXMatrix(movieId - 1, features, dataSet);
        vMatrix = cmf.singleMatrixOperation(new FloatMatrix(features, 1), ZEROS);
        int[] ids = new int[questions];
        int[] answered = new int[features];
        Arrays.fill(ids, -1);
        Arrays.fill(answered, -1);
        if (filterMovies) {
            entropyCalculator.calculateForAllQuestions();
        }
        for (int j = 0; j < questions; j++) {
            h1 = rbmHelper.calculateH1(b, vMatrix, w);
            v2 = rbmHelper.calculateV2(a, w, h1);
            removeAnswered(answered);
            includeEntropy();
//            funkcjaKuby();
//            calculateFreeEnergy(vMatrix);
            FloatMatrix px = cmf.singleMatrixOperation(v2, NORMALIZE);
            float r = (float)Math.random();
            FloatMatrix cumsum = cmf.singleMatrixOperation(px, CUMSUM);
            FloatMatrix l = rbmHelper.lessThan(cumsum, r);
            int id = (int) rbmHelper.sum(l);
            ids[j] = id;
            float answer = xMatrix.get(0, id);

            if (filterMovies) {
                entropyCalculator.getCalculatedValues()[id] = (int) answer;
            }
            answered[id] = (int) answer;
            if (filterMovies) {
                entropyCalculator.filterMovies(id, (int) answer);
            }
            vMatrix.put(id, 0, answer);

            order[id].add(j);
            if (filterMovies) {
                answered = entropyCalculator.answeredQuestions();
            }
        }
        int similiar = calculateAnswers(answered);

        return similiar;

    }

    private void setParameters() {
        this.features = a.rows;
        this.concepts = dataSet.rows;
        acc = 0;
    }

    private void removeAnswered(int[] answered) {
        for (int i = 0; i < features; i++) {
            if (answered[i] != -1) {
                v2.put(i, 0, 0);
            }
        }
    }

    private int calculateAnswers(int[] answered) {
        int[] matches = new int[concepts];
        Arrays.fill(matches, -1);
        int actuall = 0;
        for (int i = 0; i < concepts; i++) {
            int j = 0;
            boolean match = true;
            while (j < features) {
                if (answered[j] != -1 && (dataSet.get(i, j) != answered[j] && dataSet.get(i, j) != 0.5f)) {
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

    private int countAnswered(int[] answered) {
        int counter = 0;
        for (int i : answered) {
            if (i != -1) {
                counter++;
            }
        }
        return counter;
    }

    private void includeEntropy() {
        switch (selectionHelperType) {
            case NONE:
                break;
            case MULTIPLE:
                for (int i = 0; i < features; i++) {
                    v2.put(i, 0, v2.get(i, 0) * entropyCalculator.getEntropyForFeature(i));
                }
                break;
            case ADD:
                for (int i = 0; i < features; i++) {
                    v2.put(i, 0, v2.get(i, 0) + entropyCalculator.getEntropyForFeature(i));
                }
                break;
            case ONLY_ENTROPY:
                for (int i = 0; i < features; i++) {
                    v2.put(i, 0, entropyCalculator.getEntropyForFeature(i));
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

    private void createAndSetRanking() {
        rankingHelper.setupHelper(v2,
                new FloatMatrix(entropyCalculator.getActuallEntropy()),
                entropyCalculator.getCalculatedValues(),
                features - entropyCalculator.answeredQuestionsAmount());

        for (int i = 0; i < features; i++) {
            float value = v2.get(i, 0);
            float v2Rank = rankingHelper.getV2Ranking(value);
            value = entropyCalculator.getEntropyForFeature(i);
            float entropyRank = rankingHelper.getEntropyRanking(value);
            float u = rankingHelper.getActiveNumber();
            float d = 10f * (v2Rank + entropyRank);
            float rankingValue = u / d;
            float powered = (float) Math.pow(rankingValue, 2);
            v2.put(i, 0, powered);
        }
    }

    private void createAndSetPercentageRanking() {
        rankingHelper.setupHelper(v2,
                new FloatMatrix(entropyCalculator.getActuallEntropy()),
                entropyCalculator.getCalculatedValues(),
                features - entropyCalculator.answeredQuestionsAmount());

        for (int i = 0; i < features; i++) {
            float value = v2.get(i, 0);
            float v2Rank = rankingHelper.getV2PercentageRanking(value);
            value = entropyCalculator.getEntropyForFeature(i);
            float entropyRank = rankingHelper.getEntropyPercentageRanking(value);
            float d = 2f * (v2Rank + entropyRank);
            float powered = (float) Math.pow(d, 5);
            v2.put(i, 0, powered);
        }
    }

    public void setFilterMovies(boolean filterMovies) {
        this.filterMovies = filterMovies;
    }

    public void setSelectionHelperType(SelectionHelperType selectionHelperType) {
        this.selectionHelperType = selectionHelperType;
    }

    private void funkcjaKuby() {
        for (int i = 0; i < features; i++) {
            FloatMatrix withOne = vMatrix.dup();
            withOne.put(i, 0, 1f);
            FloatMatrix withZero = vMatrix.dup();
            withZero.put(i, 0, 0f);
            float myMi = calculateMI((float) -calculateFreeEnergy(withOne), (float) -calculateFreeEnergy(withZero));
            double pp = getExp(withOne);
            double pm = getExp(withZero);
            double mi = pp / (pp + pm);
            System.out.println("Regular: " + mi);
            System.out.println("Own: " + myMi);
            double o1 = (-mi * Math.log10(mi));
            double o2 = 1 - mi;
            double o3 = Math.log10(1 - mi);
            double o4 = o2 * o3;
            double res = o1 - o4;
        }
    }

    private double getExp(FloatMatrix currentMatrix) {
        double result = Math.exp(-calculateFreeEnergy(currentMatrix));
        return result;
    }

    private float calculateFreeEnergy(FloatMatrix currentMatrix) {
        FloatMatrix vCopy = currentMatrix.dup();
        // -b^t*x
        FloatMatrix t1 = a.transpose().mmul(vCopy);

        float sum = 0;
        for (int i = 0; i < w.columns; i++) {
            // W.j ^t
            FloatMatrix column = w.getColumn(i);
            FloatMatrix m2 = column.transpose();
            //m2 * x
            FloatMatrix m3 = m2.mmul(vCopy);

            // cj - row
            FloatMatrix m4 = b.getRow(i);

            //before exp
            FloatMatrix m5 = m4.add(m3);

            float exp = (float) Math.exp(m5.get(0, 0));
            float beforeLog = 1 + exp;

            float log = (float) Math.log10(beforeLog);

            sum += log;
        }
        float btx = -t1.get(0, 0);
        return btx - sum;

    }

    public static float calculateMI(float pp, float pm) {
        float denominator = (float) (1 + Math.exp(pm - pp));
        return 1 / denominator;
    }
}
