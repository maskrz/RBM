/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import processors.helpers.VectorFiller;

/**
 *
 * @author Skrzypek
 */
public class FeaturesMatrix implements Serializable {

    private int[][] features;

    public FeaturesMatrix() {
    }

    public FeaturesMatrix(int[][] result) {
        this.features = result;
    }

    public void setFeatures(int[][] features) {
        this.features = features;
    }

    public int[][] getFeatures() {
        return this.features;
    }

    public boolean serialize() {
        try {
            ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream("serializedMatrix.ser"));
            ous.writeObject(this);
            ous.close();
            return true;
        } catch (IOException ex) {
            Logger.getLogger(VectorFiller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
