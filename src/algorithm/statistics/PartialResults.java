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
    int movies;

    public PartialResults(int questionAmount, int movies) {
        this.movies = movies;
        results = new PartialResult[questionAmount][movies];
    }

    void addSnap(int question, int movie, PartialResult partialResult) {
        results[question][movie] = partialResult;
    }

    public String createResultStringForQuestion(int questionId) {
        StringBuilder sb = new StringBuilder();
        String newLine = System.getProperty("line.separator");
        for(int i = 0; i < movies; i ++) {
            sb.append(i).append(newLine);
            sb.append(questionId).append(newLine);
            sb.append(results[questionId][i].getSimilarAmount()).append(newLine);
            sb.append(results[questionId][i].getSimiliarString()).append(newLine);
        }
        return sb.toString();
    }
}
