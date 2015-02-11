/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm.statistics;

import algorithm.Node;
import algorithm.SelectionHelperType;
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

    public StatisticsHandler() {
        newLine = System.getProperty("line.separator");
        mainInfo = new StringBuilder();
    }

    public void setMainInfoMovieId(int i) {
        mainInfo.append(i).append(newLine);
    }

    public void addMatchedMoviesInfo(int actuall, String matchedMovies) {
        mainInfo.append(actuall).append(newLine);
        mainInfo.append(matchedMovies).append(newLine);
    }

    public void handleStatistics(int features, Node[] order, 
            SelectionHelperType selectionHelperType, int questions) {
        Date time = new Date();
        String inf = selectionHelperType + "-" + questions + "-" + features + "_";
        createMainInfo(inf, time);
        createStatistics(inf, time, features, order);
    }

    private void createMainInfo(String inf, Date time) {
        String fileName = "mainInfo_" + inf + time.getTime() + ".txt";
        saveFile(fileName, mainInfo.toString());
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
}
