/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package algorithm.statistics;

/**
 *
 * @author Skrzypek
 */
public class PartialResults {

    PartialResult[][] results;

    public PartialResults(int questionAmount, int movies) {
        results = new PartialResult[questionAmount][movies];
    }
}
