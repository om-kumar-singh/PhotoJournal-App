package com.example.photojournal.ui.addedit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.app.DatePickerDialog;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.photojournal.R;
import com.example.photojournal.data.database.entities.Entry;
import com.example.photojournal.viewmodel.AddEditEntryViewModel;
import com.google.android.material.textfield.TextInputEditText;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditEntryActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_IMAGE_CAPTURE = 102;
    private static final int REQUEST_IMAGE_PICK = 103;
    
    private ImageView imageViewPhoto;
    private TextInputEditText editTextTitle;
    private TextInputEditText editTextDescription;
    private Button btnTakePhoto;
    private Button btnSelectFromGallery;
    private Button btnSaveEntry;
    private Button btnSelectDate;
    private ProgressBar progressBar;
    private TextView textViewError;
    
    private String currentPhotoPath;
    private Uri selectedImageUri;
    private long selectedDateTaken;
    private AddEditEntryViewModel viewModel;
    private Entry entryToEdit;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_entry);
        
        viewModel = new ViewModelProvider(this).get(AddEditEntryViewModel.class);
        
        initViews();
        setupObservers();
        
        // Check if editing existing entry
        entryToEdit = (Entry) getIntent().getSerializableExtra("entry");
        if (entryToEdit != null) {
            loadEntryForEdit();
        } else {
            selectedDateTaken = System.currentTimeMillis();
            updateDateButton();
        }
        
        btnTakePhoto.setOnClickListener(v -> requestCameraPermission());
        btnSelectFromGallery.setOnClickListener(v -> requestStoragePermission());
        btnSaveEntry.setOnClickListener(v -> saveEntry());
        btnSelectDate.setOnClickListener(v -> showDatePicker());
    }
    
    private void initViews() {
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDescription = findViewById(R.id.editTextDescription);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectFromGallery = findViewById(R.id.btnSelectFromGallery);
        btnSaveEntry = findViewById(R.id.btnSaveEntry);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);
    }
    
    private void setupObservers() {
        viewModel.getSavedEntry().observe(this, entry -> {
            if (entry != null) {
                Toast.makeText(this, "Entry saved successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                textViewError.setText(error);
                textViewError.setVisibility(View.VISIBLE);
            } else {
                textViewError.setVisibility(View.GONE);
            }
        });
        
        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSaveEntry.setEnabled(!isLoading);
        });
    }
    
    private void loadEntryForEdit() {
        editTextTitle.setText(entryToEdit.getTitle());
        editTextDescription.setText(entryToEdit.getDescription());
        selectedDateTaken = entryToEdit.getDateTaken();
        updateDateButton();
        
        if (entryToEdit.getPhotoPath() != null) {
            String photoPath = entryToEdit.getPhotoPath();
            Uri photoUri;
            if (photoPath.startsWith("file://") || photoPath.startsWith("/")) {
                photoUri = photoPath.startsWith("file://") ? Uri.parse(photoPath) : Uri.parse("file://" + photoPath);
            } else {
                photoUri = Uri.parse(photoPath);
            }
            Glide.with(this)
                .load(photoUri)
                .centerCrop()
                .into(imageViewPhoto);
            currentPhotoPath = entryToEdit.getPhotoPath();
        }
    }
    
    private void saveEntry() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String photoPath = currentPhotoPath != null ? currentPhotoPath : 
                          (selectedImageUri != null ? selectedImageUri.toString() : null);
        
        if (TextUtils.isEmpty(photoPath)) {
            textViewError.setText("Please select a photo");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        if (entryToEdit != null) {
            // Update existing entry
            entryToEdit.setTitle(title);
            entryToEdit.setDescription(description);
            entryToEdit.setPhotoPath(photoPath);
            entryToEdit.setDateTaken(selectedDateTaken);
            viewModel.updateEntry(entryToEdit);
        } else {
            // Create new entry
            viewModel.saveEntry(title, description, photoPath, selectedDateTaken);
        }
    }
    
    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                REQUEST_CAMERA_PERMISSION);
        } else {
            dispatchTakePictureIntent();
        }
    }
    
    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_STORAGE_PERMISSION);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                return;
            }
        }
        dispatchPickPictureIntent();
    }
    
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, 
                    "com.example.photojournal.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // Grant URI permissions to all camera activities
                for (android.content.pm.ResolveInfo res : getPackageManager().queryIntentActivities(takePictureIntent, 0)) {
                    grantUriPermission(res.activityInfo.packageName, photoURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            } else {
                Toast.makeText(this, "Unable to prepare camera. Opening picker instead.", Toast.LENGTH_SHORT).show();
                dispatchPickPictureIntent();
            }
        } else {
            Toast.makeText(this, "Camera not available. Opening picker instead.", Toast.LENGTH_SHORT).show();
            dispatchPickPictureIntent();
        }
    }
    
    private void dispatchPickPictureIntent() {
        Intent pickIntent;
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            pickIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pickIntent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        pickIntent.setType("image/*");
        startActivityForResult(Intent.createChooser(pickIntent, "Select Photo"), REQUEST_IMAGE_PICK);
    }
    
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir("Pictures");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDateTaken);
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                selectedDateTaken = selectedCalendar.getTimeInMillis();
                updateDateButton();
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
    
    private void updateDateButton() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        btnSelectDate.setText("Date: " + dateFormat.format(new Date(selectedDateTaken)));
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath != null) {
                Uri photoUri = Uri.parse("file://" + currentPhotoPath);
                Glide.with(this)
                    .load(photoUri)
                    .centerCrop()
                    .into(imageViewPhoto);
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null && android.os.Build.VERSION.SDK_INT >= 19) {
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(selectedImageUri, takeFlags);
            }
            Glide.with(this)
                .load(selectedImageUri)
                .centerCrop()
                .into(imageViewPhoto);
            currentPhotoPath = null;
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchPickPictureIntent();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
}

