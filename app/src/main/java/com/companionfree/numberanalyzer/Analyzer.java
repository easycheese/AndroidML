package com.companionfree.numberanalyzer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorChangingVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

class Analyzer {

    private RealMatrix theta;
    private final Sigmoid sig = new Sigmoid();
    private static final String TAG = "Analyzer";

    Analyzer(Context ctx) {
        InputStreamReader is;
        ArrayList<String> lines = new ArrayList<>();
        try {
            is = new InputStreamReader(ctx.getAssets()
                    .open("theta-output.csv")); // 80% probability
            BufferedReader reader = new BufferedReader(is);
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] matrixData = new double[lines.size()][10];
        int x=0;
        for (String line : lines) {
            String[] parsed = line.split(",");
            int i = 0;
            for (String item : parsed) {
                matrixData[x][i] = Double.parseDouble(item);
                i++;
            }
            x++;
        }
        //http://commons.apache.org/proper/commons-math/userguide/linear.html
        theta = MatrixUtils.createRealMatrix(matrixData);

    }

    void analyze(Bitmap[][] bitmaps, final AnalyzerListener listener) {
        boolean[][] values = new boolean[28][28];
        double[] testValues = new double[28*28 + 1];
        int pos = 0;
        testValues[pos] = 1;
        pos++;
        Log.d(TAG, "Checking pixels");
        for (int column = 0 ; column < 28 ; column++) { //upper left and down first column, then next column
            for (int row = 0 ; row < 28 ; row++) {
                boolean b = BitmapHelper.containsBlack(bitmaps[row][column]);
                values[row][column] = b;
                testValues[pos] = b ? 1.0 : 0.0;
                pos++;
            }
        }

        RealVector vector = MatrixUtils.createRealVector(testValues);

        Log.d(TAG, "Test " + vector.getDimension());
        Log.d(TAG, "Theta " + theta.getRowDimension() + " x " + theta.getColumnDimension());
        final RealVector result = theta.transpose().operate(vector);

        RealVectorChangingVisitor vis = new RealVectorChangingVisitor() {

            @Override
            public void start(int dimension, int start, int end) {

            }

            @Override
            public double visit(int index, double value) {
                return sig.value(value);
            }

            @Override
            public double end() {
                Log.d("TAG", result.toString());
                int value = result.getMaxIndex() + 1;
                Log.d(TAG, "Predicted index: " + value);
                Log.d(TAG, "Predicted prob: " + result.getMaxValue());
                Log.d(TAG, result.toString());
                listener.onAnalysisComplete(value);
                return 0;
            }
        };

        result.walkInDefaultOrder(vis);

        displayLogImage(testValues);
    }

    private void displayLogImage(double[] testValues) {

        String[] strings = new String[28];

        int temp = 0;
        for (int disp = 1 ; disp < testValues.length ; disp++) { //skip first one
            String printString = strings[temp];
            if (printString == null) {
                printString = "";
            }
            if (testValues[disp] == 1.0) {
                printString = printString + "X";
            } else {
                printString = printString + "0";
            }
            strings[temp] = printString;
            if (temp == 27) {
                temp = 0;
            } else {
                temp++;
            }
        }

        for(String string : strings) {
            Log.d(TAG, string);
        }
    }

    interface AnalyzerListener {
        void onAnalysisComplete(int prediction);
    }
}
