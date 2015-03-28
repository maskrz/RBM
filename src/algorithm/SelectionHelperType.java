/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import algorithm.selection.SelectionStrategy;

/**
 *
 * @author Skrzypek
 */
public enum SelectionHelperType {

    NONE("PureBoltzmannStrategy"),
    ADD("AddStrategy"),
    MULTIPLE("MultipleStrategy"),
    ONLY_ENTROPY("EntropyStrategy"),
    RANKING("RankingStrategy"),
    PERCENTAGE_RANKING("PercentageRankingStrategy"),
    RANDOM("RandomStrategy"),
    BOLTZMANN_ENTROPY("BoltzmannEntropyStrategy");

    private final String name;
    private final String packageName = "algorithm.selection.";

    SelectionHelperType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public SelectionStrategy newInstance() {
        try {
            return (SelectionStrategy) Class.forName(packageName + getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
