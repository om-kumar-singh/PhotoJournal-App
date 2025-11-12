package com.example.photojournal.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageEditUtils {
    
    /**
     * Rotate bitmap by 90 degrees
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        if (degrees == 0) return bitmap;
        
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
    
    /**
     * Adjust brightness of bitmap
     * @param bitmap Source bitmap
     * @param brightness Value from -100 to 100
     */
    public static Bitmap adjustBrightness(Bitmap bitmap, float brightness) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
            1, 0, 0, 0, brightness,
            0, 1, 0, 0, brightness,
            0, 0, 1, 0, brightness,
            0, 0, 0, 1, 0
        });
        
        return applyColorMatrix(bitmap, colorMatrix);
    }
    
    /**
     * Adjust contrast of bitmap
     * @param bitmap Source bitmap
     * @param contrast Value from 0 to 2 (1.0 is normal)
     */
    public static Bitmap adjustContrast(Bitmap bitmap, float contrast) {
        float scale = contrast;
        float translate = (-.5f * scale + .5f) * 255.f;
        
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.set(new float[]{
            scale, 0, 0, 0, translate,
            0, scale, 0, 0, translate,
            0, 0, scale, 0, translate,
            0, 0, 0, 1, 0
        });
        
        return applyColorMatrix(bitmap, colorMatrix);
    }
    
    /**
     * Adjust saturation of bitmap
     * @param bitmap Source bitmap
     * @param saturation Value from 0 to 2 (1.0 is normal)
     */
    public static Bitmap adjustSaturation(Bitmap bitmap, float saturation) {
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(saturation);
        
        return applyColorMatrix(bitmap, colorMatrix);
    }
    
    /**
     * Apply color matrix to bitmap
     */
    private static Bitmap applyColorMatrix(Bitmap bitmap, ColorMatrix colorMatrix) {
        Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return result;
    }
    
    /**
     * Save bitmap to file
     */
    public static boolean saveBitmapToFile(Bitmap bitmap, File file) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Load bitmap from file path
     */
    public static Bitmap loadBitmapFromPath(String path) {
        return BitmapFactory.decodeFile(path);
    }
}

