package com.example.photojournalapp_v3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.photojournalapp_v3.storage.ImageStorage;
import com.example.photojournalapp_v3.vm.AddEditViewModel;

import java.io.File;
import java.io.IOException;

public class AddEditEntryActivity extends AppCompatActivity {
    private static final int REQ_CAMERA = 1001;
    private static final int REQ_PICK = 1002;

    private AddEditViewModel viewModel;
    private ImageView imagePreview;
    private EditText inputTitle;
    private EditText inputDescription;
    private Uri cameraPhotoUri;
    private File cameraPhotoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_edit_entry);

        imagePreview = findViewById(R.id.imagePreview);
        inputTitle = findViewById(R.id.inputTitle);
        inputDescription = findViewById(R.id.inputDescription);
        Button btnCamera = findViewById(R.id.btnCamera);
        Button btnPick = findViewById(R.id.btnPick);
        Button btnSave = findViewById(R.id.btnSave);

        viewModel = new ViewModelProvider(this).get(AddEditViewModel.class);
        viewModel.getSelectedImagePath().observe(this, path -> {
            if (path != null && !path.isEmpty()) {
                Glide.with(this).load(path).centerCrop().into(imagePreview);
            }
        });

        btnCamera.setOnClickListener(v -> openCamera());
        btnPick.setOnClickListener(v -> openPicker());
        btnSave.setOnClickListener(v -> saveEntry());
    }

    private void openCamera() {
        try {
            cameraPhotoFile = ImageStorage.createNewImageFile(this);
            cameraPhotoUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    cameraPhotoFile
            );
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQ_CAMERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == REQ_CAMERA) {
            if (cameraPhotoFile != null && cameraPhotoFile.exists()) {
                viewModel.setSelectedImagePath(cameraPhotoFile.getAbsolutePath());
            }
        } else if (requestCode == REQ_PICK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                File copied = ImageStorage.copyFromUri(this, uri);
                viewModel.setSelectedImagePath(copied.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveEntry() {
        String title = inputTitle.getText().toString();
        String description = inputDescription.getText().toString();
        viewModel.saveEntry(title, description);
        finish();
    }
}


