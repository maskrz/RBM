/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.choice.QuestionChoiceStrategy;
import algorithm.selection.SelectionStrategy;
import matrices.operations.CalculatedMatrixFactory;
import org.jblas.FloatMatrix;

/**
 *
 * @author Skrzypek
 */
public class RBMRepository {

    private FloatMatrix a;
    private FloatMatrix b;
    private FloatMatrix w;
    private CalculatedMatrixFactory cmf;
    private FloatMatrix dataSet;
    private int features;
    private int concepts;
    private Node[] order;
    private FloatMatrix xMatrix;
    private FloatMatrix vMatrix;
    private EntropyCalculator entropyCalculator;
    private int questions;
    private int[] ids;
    private float[] answered;
    private boolean filterMovies;
    private float certainty;
    private FloatMatrix h1;
    private FloatMatrix v2;
    private SelectionStrategy selectionStrategy;
    private RankingHelper rankingHelper;
    private QuestionChoiceStrategy questionChoiceStrategy;

    public RBMRepository(FloatMatrix a, FloatMatrix b, FloatMatrix w, FloatMatrix dataSet, int questions, float certainty) {
        this.a = a;
        this.b = b;
        this.w = w;
        this.questions = questions;
        this.cmf = new CalculatedMatrixFactory();
        this.dataSet = dataSet;
        this.entropyCalculator = new EntropyCalculator(getDataSet());
        this.certainty = certainty;
//        rankingHelper = new RankingHelper();
        setParameters();
        this.order = new Node[features];
        for (int i = 0; i < features; i++) {
            order[i] = new Node();
        }
        rankingHelper = new RankingHelper();
    }

    private void setParameters() {
        this.setFeatures(getA().rows);
        this.setConcepts(getDataSet().rows);
    }

    public FloatMatrix getA() {
        return a;
    }

    public void setA(FloatMatrix a) {
        this.a = a;
    }

    public FloatMatrix getB() {
        return b;
    }

    public void setB(FloatMatrix b) {
        this.b = b;
    }

    public FloatMatrix getW() {
        return w;
    }

    public void setW(FloatMatrix w) {
        this.w = w;
    }

    public CalculatedMatrixFactory getCmf() {
        return cmf;
    }

    public void setCmf(CalculatedMatrixFactory cmf) {
        this.cmf = cmf;
    }

    public FloatMatrix getDataSet() {
        return dataSet;
    }

    public void setDataSet(FloatMatrix dataSet) {
        this.dataSet = dataSet;
    }

    public int getFeatures() {
        return features;
    }

    public void setFeatures(int features) {
        this.features = features;
    }

    public int getConcepts() {
        return concepts;
    }

    public void setConcepts(int concepts) {
        this.concepts = concepts;
    }

    public Node[] getOrder() {
        return order;
    }

    public void setOrder(Node[] order) {
        this.order = order;
    }

    public FloatMatrix getXMatrix() {
        return xMatrix;
    }

    public void setXMatrix(FloatMatrix xMatrix) {
        this.xMatrix = xMatrix;
    }

    public FloatMatrix getVMatrix() {
        return vMatrix;
    }

    public void setVMatrix(FloatMatrix vMatrix) {
        this.vMatrix = vMatrix;
    }

    public EntropyCalculator getEntropyCalculator() {
        return entropyCalculator;
    }

    public void setEntropyCalculator(EntropyCalculator entropyCalculator) {
        this.entropyCalculator = entropyCalculator;
    }

    int getQuestions() {
        return questions;
    }

    boolean getFilterMovies() {
        return this.isFilterMovies();
    }

    void setFilterMovies(boolean filterMovies) {
        this.filterMovies = filterMovies;
    }

    public void setQuestions(int questions) {
        this.questions = questions;
    }

    public int[] getIds() {
        return ids;
    }

    public void setIds(int[] ids) {
        this.ids = ids;
    }

    public float[] getAnswered() {
        return answered;
    }

    public void setAnswered(float[] answered) {
        this.answered = answered;
    }

    public boolean isFilterMovies() {
        return filterMovies;
    }

    public FloatMatrix getH1() {
        return h1;
    }

    public void setH1(FloatMatrix h1) {
        this.h1 = h1;
    }

    public FloatMatrix getV2() {
        return v2;
    }

    public void setV2(FloatMatrix v2) {
        this.v2 = v2;
    }

    void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    SelectionStrategy getSelectionStrategy() {
        return this.selectionStrategy;
    }

    public RankingHelper getRankingHelper() {
        return rankingHelper;
    }

    public void setRankingHelper(RankingHelper rankingHelper) {
        this.rankingHelper = rankingHelper;
    }

    void setQuestionChoiceStrategy(QuestionChoiceStrategy questionChoiceStrategy) {
        this.questionChoiceStrategy = questionChoiceStrategy;
    }

    public QuestionChoiceStrategy getQuestionChoiceStrategy() {
        return questionChoiceStrategy;
    }

    public float getCertainty() {
        return certainty;
    }

    public void setCertainty(float certainty) {
        this.certainty = certainty;
    }
}
