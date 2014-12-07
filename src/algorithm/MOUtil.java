/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 *
 * @author Skrzypek
 */
public class MOUtil {

    public static String trimTitle(String originalTitle) {
        String temp = originalTitle.replace(":", "");
        return MOUtil.removeSpaces(temp);
    }

    private MOUtil() {
    }

    public static String stringDateOfYear(String year) {
        return year + "-01-01";
    }

    public static Date dateOfYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.JANUARY, 1, 0, 0, 0);
        return cal.getTime();
    }

    public static int getYearOfDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String removeSpaces(String string) {
        return string.replace(" ", "_");
    }

    public static float randomGaussian() {
        Random random = new Random();
        float result = (float) random.nextGaussian();
        return result ;
    }
}
