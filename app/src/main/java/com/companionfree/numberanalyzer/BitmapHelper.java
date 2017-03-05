package com.companionfree.numberanalyzer;

import android.graphics.Bitmap;

class BitmapHelper {

    static Bitmap[][] splitBitmap(Bitmap bitmap, int rowCount, int columnCount) {

        Bitmap[][] bitmaps = new Bitmap[rowCount][columnCount];
        int sliceWidth, sliceHeight;
        sliceWidth = bitmap.getWidth() / rowCount;
        sliceHeight = bitmap.getHeight() / columnCount;

        for(int row = 0; row < rowCount; row++) {
            for(int column = 0; column < columnCount; column++) {

                int firstXPixel = column * sliceWidth;
                int firstYPixel = row * sliceHeight;
                bitmaps[row][column] =
                        Bitmap.createBitmap(bitmap, firstXPixel, firstYPixel, sliceWidth, sliceHeight);
            }
        }

        return bitmaps;
    }

    static boolean containsBlack(Bitmap bitmap) { // Checks borders only, faster
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

    private static boolean isBlack(int pixel) {
        return pixel == -16777216;
    }
}
