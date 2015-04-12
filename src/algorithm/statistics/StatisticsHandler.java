/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.statistics;

import algorithm.Node;
import algorithm.choice.QuestionChoiceStrategy;
import algorithm.selection.SelectionStrategy;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

/**
 *
 * @author Skrzypek
 */
public class StatisticsHandler {

    private StringBuilder mainInfo;
    private String newLine;
    private PartialResults partials;

    public StatisticsHandler(int questions, int concepts) {
        newLine = System.getProperty("line.separator");
        mainInfo = new StringBuilder();
        partials = new PartialResults(questions, concepts);
    }

    public void setMainInfoMovieId(int i) {
        getMainInfo().append(i).append(getNewLine());
    }

    public void addMatchedMoviesInfo(int actuall, String matchedMovies, int questions) {
        getMainInfo().append(questions).append(getNewLine());
        getMainInfo().append(actuall).append(getNewLine());
        getMainInfo().append(matchedMovies).append(getNewLine());
    }

    public void handleStatistics(int features, Node[] order,
            SelectionStrategy selectionStrategy, QuestionChoiceStrategy choiceStrategy, int questions) {
        Date time = new Date();
        String inf = selectionStrategy.getClass().getSimpleName() + "-" + choiceStrategy.getClass().getSimpleName() + "-" + questions + "-" + features + "_";
        createMainInfo(inf, time);
        createStatistics(inf, time, features, order);
    }

    private void createMainInfo(String inf, Date time) {
        String fileName = "mainInfo_" + inf + time.getTime() + ".txt";
        saveFile(fileName, getMainInfo().toString());
    }

    private void saveFile(String fileName, String fileContent) {
        File f = new File(fileName);
        try {
            if (!f.exists()) {
                f.createNewFile();
                PrintWriter writer = new PrintWriter(f);
                writer.printf(fileContent);
                writer.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createStatistics(String inf, Date time, int features, Node[] order) {
        String statisticsString = buildStatisticsString(features, order);
        String fileName = "statistics_" + inf + time.getTime() + ".txt";
        saveFile(fileName, statisticsString);
    }

    private String buildStatisticsString(int features, Node[] order) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < features; i++) {
            sb.append(i).append(";");
            sb.append(order[i].getListLength()).append(";");
            sb.append(order[i]).append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public StringBuilder getMainInfo() {
        return mainInfo;
    }

    public void setMainInfo(StringBuilder mainInfo) {
        this.mainInfo = mainInfo;
    }

    public String getNewLine() {
        return newLine;
    }

    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }

    public void addSnap(int question, int movie, PartialResult partialResult) {
        partials.addSnap(question, movie, partialResult);
    }

    public void saveAll(int questions, int features, SelectionStrategy selectionStrategy, QuestionChoiceStrategy choiceStrategy, float certainty) {
        for (int i = 0; i < questions; i++) {
            int qNo = i + 1;
            String result = createResultStringForQuestion(i);
            Date time = new Date();
            String inf = selectionStrategy.getClass().getSimpleName() + "-" + choiceStrategy.getClass().getSimpleName() + "-" + qNo + "-" + features + "_" + certainty+ "_" ;
            String fileName = "mainInfo_" + inf + time.getTime() + ".txt";
            saveFile(fileName, result);
        }
    }

    private String createResultStringForQuestion(int questionId) {
        return partials.createResultStringForQuestion(questionId);
    }
}
