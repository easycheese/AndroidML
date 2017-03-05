package com.companionfree.numberanalyzer;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.simplify.ink.InkView;

import org.apache.commons.math3.analysis.function.Sigmoid;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixChangingVisitor;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.RealVectorChangingVisitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ink)
    InkView inkView;

    @BindView(R.id.prediction)
    TextView prediction;

    private static final String TAG = "NumberAnalyzer";
    RealMatrix theta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        inkView.setColor(getResources().getColor(android.R.color.black));
        inkView.setMinStrokeWidth(13.0f);
        inkView.setMaxStrokeWidth(13.0f);

        InputStreamReader is;
        ArrayList<String> lines = new ArrayList<>();
        try {
            is = new InputStreamReader(getAssets()
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

//        theta.walkInOptimizedOrder(disp);

        double[] test = new double[]{.1,.008,-.053};
        RealVector testVector = MatrixUtils.createRealVector(test);
        double[][] m = new double[3][3];

        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                m[i][j] = i*j;
            }
        }

        RealMatrix matrix = MatrixUtils.createRealMatrix(m);
        RealVector output = matrix.operate(testVector);
//        output.walkInDefaultOrder(vis);



    }

    public Bitmap[][] splitBitmap(Bitmap bitmap, int rowCount, int columnCount) {
        // Allocate a two dimensional array to hold the individual images.
        Bitmap[][] bitmaps = new Bitmap[rowCount][columnCount];
        int sliceWidth, sliceHeight;
        // Divide the original bitmap sliceWidth by the desired vertical column count
        sliceWidth = bitmap.getWidth() / rowCount;
        // Divide the original bitmap sliceHeight by the desired horizontal row count
        sliceHeight = bitmap.getHeight() / columnCount;
        // Loop the array and create bitmaps for each coordinate

        for(int row = 0; row < rowCount; row++) {
            for(int column = 0; column < columnCount; column++) {
                // Create the sliced bitmap
//                int tempX = rowCount - row;
//                int row = rowCount - row-1; // Start at bottom
                int firstXPixel = column * sliceWidth;
                int firstYPixel = row * sliceHeight;
                bitmaps[row][column] =
                        Bitmap.createBitmap(bitmap, firstXPixel, firstYPixel, sliceWidth, sliceHeight);
            }
        }
        // Return the array
        return bitmaps;
    }

    @OnClick(R.id.btn_clear)
    public void onClearClick() {
        inkView.clear();
    }

    @OnClick(R.id.btn_calc)
    public void onCalcClick() {
        Log.d(TAG, "Checking");
        Bitmap[][] bitmaps = splitBitmap(inkView.getBitmap(), 28, 28);
        boolean[][] values = new boolean[28][28];
        double[] testValues = new double[28*28 + 1];
//        containsBlack(bitmaps[0][0]);
        int pos = 0;
        testValues[pos] = 1;
        pos++;
        Log.d(TAG, "Checking pixels");
        for (int column = 0 ; column < 28 ; column++) { //upper left and down first column, then next column
            for (int row = 0 ; row < 28 ; row++) {
                boolean b = containsBlack(bitmaps[row][column]);
                values[row][column] = b;
                testValues[pos] = b ? 1.0 : 0.0;
                pos++;
            }
        }

//        for (int row = 0 ; row < 28 ; row++) { //upper left and cross first row, then next row
//            for (int column = 0 ; column < 28 ; column++) {
//                boolean b = containsBlack(bitmaps[row][column]);
//                values[row][column] = b;
//                testValues[pos] = b ? 1.0 : 0.0;
//                pos++;
//            }
//        }

        RealVector vector = MatrixUtils.createRealVector(testValues);

//        vector.walkInDefaultOrder(dispV);
//        RealMatrix test = MatrixUtils.createRealMatrix(testValues);
        Log.d(TAG, "Test " + vector.getDimension());
        Log.d(TAG, "Theta " + theta.getRowDimension() + " x " + theta.getColumnDimension());
//        RealMatrix result = test.transpose().multiply(theta);
        RealVector result = theta.transpose().operate(vector);
        sigmoid(result);


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

//        for (int row = 0 ; row < 28 ; row++) { //
//            String print = "";
//            for (int column = 0 ; column < 28 ; column++) {
//                if (values[row][column]) {
//                    print = print + "X";
//                } else {
//                    print = print + "o";
//                }
//            }
//            Log.d(TAG, print);
//        }
    }

    final Sigmoid sig = new Sigmoid();
    public void sigmoid(final RealVector z) {

        RealVectorChangingVisitor vis = new RealVectorChangingVisitor() {

            @Override
            public void start(int dimension, int start, int end) {

            }

            @Override
            public double visit(int index, double value) {
//                Log.d(TAG, "" + value);
                double value1 = sig.value(value);
//                Log.d(TAG, "New Val:" + value);
                return value1;
//                return value;
            }

            @Override
            public double end() {
//                z.walkInDefaultOrder(dispV);
                Log.d("TAG", z.toString());
                int value = z.getMaxIndex() + 1;
                Log.d(TAG, "Predicted index: " + value);
                Log.d(TAG, "Predicted prob: " + z.getMaxValue());

                prediction.setText("" + value);
                return 0;
            }
        };

        z.walkInDefaultOrder(vis);
    }


    RealVectorChangingVisitor dispV = new RealVectorChangingVisitor() {


        @Override
        public void start(int dimension, int start, int end) {

        }

        @Override
        public double visit(int index, double value) {
            Log.d(TAG, "" + value);
            return value;
        }

        @Override
        public double end() {

            return 0;
        }
    };

    RealMatrixChangingVisitor disp = new RealMatrixChangingVisitor() {
        private int count = 0;
        @Override
        public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {

        }

        @Override
        public double visit(int row, int column, double value) {
            Log.d(TAG, "" + value);
            if (value == 1.0) {
                count++;
            }
            return value;
        }

        @Override
        public double end() {
            Log.d(TAG, "" + count);
            return 0;
        }
    };

    public boolean containsBlack(Bitmap bitmap) { // Checks borders only
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        for (int x = 0 ; x < width ; x++) {
            if (isBlack(bitmap.getPixel(x,0)) || isBlack(bitmap.getPixel(x, height-1))) {
                return true;
            }
        }
        for (int y = 0 ; y < height ; y++) {
            if (isBlack(bitmap.getPixel(0,y)) || isBlack(bitmap.getPixel(width-1, y))) {
                return true;
            }
        }

        return false;
    }
    private boolean isBlack(int pixel) {
        return pixel == -16777216;
    }
}
