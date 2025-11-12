package com.example.photojournal.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.photojournal.util.ImageEditUtils;
import java.io.File;
import java.io.IOException;

public class ImageEditViewModel extends AndroidViewModel {
    private MutableLiveData<Bitmap> currentBitmap = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<String> savedImagePath = new MutableLiveData<>();
    
    private Bitmap originalBitmap;
    private Bitmap editedBitmap;
    private String originalImagePath;
    private int currentRotation = 0;
    private float currentBrightness = 0;
    private float currentContrast = 1.0f;
    private float currentSaturation = 1.0f;
    
    public ImageEditViewModel(Application application) {
        super(application);
    }
    
    public void loadImage(String imagePath) {
        originalImagePath = imagePath;
        Bitmap bitmap = ImageEditUtils.loadBitmapFromPath(imagePath);
        if (bitmap != null) {
            originalBitmap = bitmap;
            editedBitmap = bitmap.copy(bitmap.getConfig(), true);
            currentBitmap.setValue(editedBitmap);
            resetFilters();
        } else {
            errorMessage.setValue("Failed to load image");
        }
    }
    
    public void rotateImage() {
        if (editedBitmap != null) {
            currentRotation = (currentRotation + 90) % 360;
            editedBitmap = ImageEditUtils.rotateBitmap(editedBitmap, 90);
            currentBitmap.setValue(editedBitmap);
        }
    }
    
    public void adjustBrightness(float brightness) {
        if (originalBitmap != null) {
            currentBrightness = brightness;
            applyAllFilters();
        }
    }
    
    public void adjustContrast(float contrast) {
        if (originalBitmap != null) {
            currentContrast = contrast;
            applyAllFilters();
        }
    }
    
    public void adjustSaturation(float saturation) {
        if (originalBitmap != null) {
            currentSaturation = saturation;
            applyAllFilters();
        }
    }
    
    private void applyAllFilters() {
        if (originalBitmap == null) return;
        
        // Start with original
        editedBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        
        // Apply rotation
        if (currentRotation != 0) {
            editedBitmap = ImageEditUtils.rotateBitmap(editedBitmap, currentRotation);
        }
        
        // Apply brightness
        if (currentBrightness != 0) {
            editedBitmap = ImageEditUtils.adjustBrightness(editedBitmap, currentBrightness);
        }
        
        // Apply contrast
        if (currentContrast != 1.0f) {
            editedBitmap = ImageEditUtils.adjustContrast(editedBitmap, currentContrast);
        }
        
        // Apply saturation
        if (currentSaturation != 1.0f) {
            editedBitmap = ImageEditUtils.adjustSaturation(editedBitmap, currentSaturation);
        }
        
        currentBitmap.setValue(editedBitmap);
    }
    
    public void resetFilters() {
        currentRotation = 0;
        currentBrightness = 0;
        currentContrast = 1.0f;
        currentSaturation = 1.0f;
        if (originalBitmap != null) {
            editedBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
            currentBitmap.setValue(editedBitmap);
        }
    }
    
    public void saveImage(String outputPath) {
        if (editedBitmap == null) {
            errorMessage.setValue("No image to save");
            return;
        }
        
        File outputFile = new File(outputPath);
        try {
            outputFile.getParentFile().mkdirs();
            if (ImageEditUtils.saveBitmapToFile(editedBitmap, outputFile)) {
                savedImagePath.setValue(outputPath);
            } else {
                errorMessage.setValue("Failed to save image");
            }
        } catch (Exception e) {
            errorMessage.setValue("Error saving image: " + e.getMessage());
        }
    }
    
    public LiveData<Bitmap> getCurrentBitmap() {
        return currentBitmap;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<String> getSavedImagePath() {
        return savedImagePath;
    }
}

