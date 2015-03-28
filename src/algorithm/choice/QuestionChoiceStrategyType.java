/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.choice;

/**
 *
 * @author Skrzypek
 */
public enum QuestionChoiceStrategyType {

    SELECT_BEST("SelectBest"),
    RANDOM_BEST("RandomBest"),
    RANDOM_FROM_PROBABILITIES("RandomFromProbabilities");

    private final String name;
    private final String packageName = "algorithm.choice.";

    QuestionChoiceStrategyType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public QuestionChoiceStrategy newInstance() {
        try {
            return (QuestionChoiceStrategy) Class.forName(packageName + getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
