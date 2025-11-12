package com.example.photojournal.ui.imageedit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.photojournal.R;
import com.example.photojournal.viewmodel.ImageEditViewModel;
import java.io.File;

public class ImageEditActivity extends AppCompatActivity {
    private ImageView imageViewEdit;
    private SeekBar seekBarBrightness;
    private SeekBar seekBarContrast;
    private SeekBar seekBarSaturation;
    private TextView textViewBrightness;
    private TextView textViewContrast;
    private TextView textViewSaturation;
    private Button btnRotate;
    private Button btnReset;
    private Button btnSave;
    private Button btnCrop;
    private ProgressBar progressBar;
    
    private ImageEditViewModel viewModel;
    private String imagePath;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        
        imagePath = getIntent().getStringExtra("imagePath");
        if (imagePath == null) {
            Toast.makeText(this, "No image provided", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        viewModel = new ViewModelProvider(this).get(ImageEditViewModel.class);
        viewModel.loadImage(imagePath);
        
        initViews();
        setupObservers();
        setupSeekBars();
        
        btnRotate.setOnClickListener(v -> viewModel.rotateImage());
        btnReset.setOnClickListener(v -> viewModel.resetFilters());
        btnSave.setOnClickListener(v -> saveEditedImage());
        btnCrop.setOnClickListener(v -> startCropActivity());
    }
    
    private void initViews() {
        imageViewEdit = findViewById(R.id.imageViewEdit);
        seekBarBrightness = findViewById(R.id.seekBarBrightness);
        seekBarContrast = findViewById(R.id.seekBarContrast);
        seekBarSaturation = findViewById(R.id.seekBarSaturation);
        textViewBrightness = findViewById(R.id.textViewBrightness);
        textViewContrast = findViewById(R.id.textViewContrast);
        textViewSaturation = findViewById(R.id.textViewSaturation);
        btnRotate = findViewById(R.id.btnRotate);
        btnReset = findViewById(R.id.btnReset);
        btnSave = findViewById(R.id.btnSave);
        btnCrop = findViewById(R.id.btnCrop);
        progressBar = findViewById(R.id.progressBar);
    }
    
    private void setupObservers() {
        viewModel.getCurrentBitmap().observe(this, bitmap -> {
            if (bitmap != null) {
                imageViewEdit.setImageBitmap(bitmap);
            }
        });
        
        viewModel.getSavedImagePath().observe(this, savedPath -> {
            if (savedPath != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("editedImagePath", savedPath);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void setupSeekBars() {
        // Brightness: -100 to 100
        seekBarBrightness.setMax(200);
        seekBarBrightness.setProgress(100);
        seekBarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float brightness = (progress - 100) * 2.55f; // Convert to -255 to 255 range
                    textViewBrightness.setText("Brightness: " + (progress - 100));
                    viewModel.adjustBrightness(brightness);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Contrast: 0 to 200 (representing 0.0 to 2.0)
        seekBarContrast.setMax(200);
        seekBarContrast.setProgress(100);
        seekBarContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float contrast = progress / 100.0f;
                    textViewContrast.setText("Contrast: " + String.format("%.1f", contrast));
                    viewModel.adjustContrast(contrast);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        
        // Saturation: 0 to 200 (representing 0.0 to 2.0)
        seekBarSaturation.setMax(200);
        seekBarSaturation.setProgress(100);
        seekBarSaturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    float saturation = progress / 100.0f;
                    textViewSaturation.setText("Saturation: " + String.format("%.1f", saturation));
                    viewModel.adjustSaturation(saturation);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void saveEditedImage() {
        // Create output file path
        File originalFile = new File(imagePath);
        String fileName = originalFile.getName();
        String editedFileName = "edited_" + fileName;
        File outputDir = getExternalFilesDir("Pictures");
        File outputFile = new File(outputDir, editedFileName);
        
        viewModel.saveImage(outputFile.getAbsolutePath());
    }
    
    private void startCropActivity() {
        // UCrop integration would go here
        // For now, show a message
        Toast.makeText(this, "Crop feature - to be implemented with UCrop", Toast.LENGTH_SHORT).show();
    }
}

