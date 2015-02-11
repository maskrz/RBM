/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algorithm;

import java.util.ArrayList;

/**
 *
 * @author Skrzypek
 */
public class Node {

    ArrayList<Integer> list;

    public Node() {
        this.list = new ArrayList<Integer>();
    }

    public void add(int e) {
        list.add(e);
    }

    public int getListLength() {
        return list.size();
    }

    public String toString() {
        String result = "";
        for (Integer i : list) {
            result = result + i + ";";
        }
        return result;
    }
}
