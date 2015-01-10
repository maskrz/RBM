/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package matrices.operations;

import java.io.File;
import java.io.FileOutputStream;
import org.jblas.FloatMatrix;
import org.jblas.MatrixFunctions;

/**
 *
 * @author Skrzypek
 */
public class CalculatedMatrixFactory {

    private final DefaultMatrices defaultMatrices;
    private final float ONE_VALUE = 1f;
    private final float ZERO_VALUE = 0f;
    private SingleMatrixOperationService singleMatrixOperationService;

    public CalculatedMatrixFactory() {
        defaultMatrices = new DefaultMatrices();
        singleMatrixOperationService = new SingleMatrixOperationService();
    }

    public FloatMatrix singleMatrixOperation(FloatMatrix matrix, MatrixOperation operation) {
        switch (operation) {
            case INCREMENT:
                return incrementElements(matrix);
            case EXP:
                return expElements(matrix);
            case INVERSE:
                return inverseElements(matrix);
            case RANDOM:
                return randMatrix(matrix);
            case ADVERSE:
                return negateElements(matrix);
            case SUM_COLUMNS:
                return sumColumns(matrix);
            case SUM_ROWS:
                return sumRows(matrix);
            case ONES:
                return onesMatrix(matrix);
            case ZEROS:
                return zerosMatrix(matrix);
            case NORMALIZE:
                return normalizeMatrix(matrix);
            case CUMSUM:
                return cumsum(matrix);
            default:
                return null;
        }
    }

    private FloatMatrix incrementElements(FloatMatrix matrix) {
        return addMatrices(matrix,
                defaultMatrices.getMatrix(matrix.rows,
                        matrix.columns,
                        ONE_VALUE));
    }

    private FloatMatrix addMatrices(FloatMatrix matrix1, FloatMatrix matrix2) {
        return matrix1.add(matrix2);
    }

    private FloatMatrix expElements(FloatMatrix matrix) {
        return MatrixFunctions.expi(matrix);
    }

    private FloatMatrix inverseElements(FloatMatrix matrix) {
        return divideMatrices(defaultMatrices.getMatrix(matrix.rows,
                matrix.columns,
                ONE_VALUE),
                matrix);
    }

    private FloatMatrix divideMatrices(FloatMatrix matrix1, FloatMatrix matrix2) {
        return matrix1.div(matrix2);
    }

    private FloatMatrix randMatrix(FloatMatrix matrix) {
        return FloatMatrix.rand(matrix.rows, matrix.columns);
    }

    private FloatMatrix negateElements(FloatMatrix matrix) {
        return matrix.negi();
    }

    private FloatMatrix sumColumns(FloatMatrix matrix) {
        return matrix.columnSums();
    }

    private FloatMatrix sumRows(FloatMatrix matrix) {
        return matrix.rowSums();
    }

    private FloatMatrix onesMatrix(FloatMatrix matrix) {
        return defaultMatrices.getMatrix(matrix.rows,
                matrix.columns,
                ONE_VALUE);
    }

    private FloatMatrix zerosMatrix(FloatMatrix matrix) {
        return defaultMatrices.getMatrix(matrix.rows,
                matrix.columns,
                ZERO_VALUE);
    }

    private FloatMatrix cumsum(FloatMatrix matrix) {
        return matrix.cumulativeSum();
    }

    private FloatMatrix normalizeMatrix(FloatMatrix matrix) {
        return performSingleMatrixOperation(matrix, new NormalizeMatrix());
    }

    private FloatMatrix performSingleMatrixOperation(FloatMatrix matrix,
            SingleMatrixOperation operation) {
        return operation.performOperation(matrix);
    }

    public FloatMatrix multipleMatrixOperations(FloatMatrix matrix, MatrixOperation... operations) {
        for (MatrixOperation operation : operations) {
            matrix = singleMatrixOperation(matrix, operation);
        }
        return matrix;
    }

    public void printMatrix(FloatMatrix matrix) {
        System.out.println(matrix.rows);
        System.out.println(matrix.columns);
        for (int i = 0; i < matrix.rows; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                System.out.print(matrix.get(i, j) + " ");
            }
            System.out.println("");
        }
    }

    public FloatMatrix twoMatricesOperation(FloatMatrix matrix1,
            FloatMatrix matrix2, MatrixOperation operation) {
        switch (operation) {
            case COMPARE:
                return matrix1.gt(matrix2);
            case DIF_ABS:
                return difAbs(matrix1, matrix2);
            default:
                return null;

        }
    }

    private FloatMatrix difAbs(FloatMatrix matrix1, FloatMatrix matrix2) {
        FloatMatrix result = matrix1.sub(matrix2);
        return MatrixFunctions.abs(result);
    }

    public FloatMatrix repeatMatrix(FloatMatrix matrix, int times) {
        float[][] result = new float[times][matrix.columns];
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < matrix.columns; j++) {
                result[i][j] = matrix.get(0, j);
            }
        }
        return new FloatMatrix(result);
    }

    public void saveMatrix(FloatMatrix matrix, String name) {
        try {
            FileOutputStream fos = null;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < matrix.rows; i++) {
                for (int j = 0; j < matrix.columns; j++) {
                    sb.append(matrix.get(i, j));
                    sb.append("; ");
//                    System.out.println(counter++);
                }
                sb.append(System.getProperty("line.separator"));
            }
            File f = new File(name);
            fos = new FileOutputStream(f);
            fos.write(sb.toString().getBytes());
            fos.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
