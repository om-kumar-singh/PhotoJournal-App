package com.example.photolibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.example.photolibrary.models.Photo;
import com.example.photolibrary.utils.FirebaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UploadActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQ_STORAGE_PERMISSION = 2001;
    
    private ImageView ivImagePreview, ivPlaceholder, ivBack;
    private Button btnChooseImage, btnSave;
    private TextInputEditText etDescription;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        
        // Check if user is logged in
        if (currentUser == null) {
            Intent intent = new Intent(UploadActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        storageReference = FirebaseStorage.getInstance().getReference("photos");
        databaseReference = FirebaseHelper.getPhotosReference();

        ivImagePreview = findViewById(R.id.ivImagePreview);
        ivPlaceholder = findViewById(R.id.ivPlaceholder);
        ivBack = findViewById(R.id.ivBack);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSave = findViewById(R.id.btnSave);
        etDescription = findViewById(R.id.etDescription);

        btnChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ensureStoragePermissionAndOpenChooser();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhoto();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void ensureStoragePermissionAndOpenChooser() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, REQ_STORAGE_PERMISSION);
                return;
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQ_STORAGE_PERMISSION);
                return;
            }
        }
        openImageChooser();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImageChooser();
            } else {
                Toast.makeText(this, "Permission required to choose images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                ivImagePreview.setImageURI(imageUri);
                ivPlaceholder.setVisibility(View.GONE);
                ivImagePreview.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Toast.makeText(this, "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePhoto() {
        if (imageUri == null) {
            Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
            return;
        }

        String description = etDescription.getText().toString().trim();
        if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading state
        btnSave.setEnabled(false);
        btnSave.setText("Saving...");

        // Create a unique filename for the image
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "photo_" + timestamp + ".jpg";
        StorageReference imageRef = storageReference.child(currentUser.getUid()).child(imageFileName);

        // Upload image to Firebase Storage
        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Some buckets can take a moment to make the object visible for URL fetch.
                // Retry a few times before giving up.
                fetchUrlAndSave(taskSnapshot.getStorage(), description, 3);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, R.string.photo_save_failed + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText(R.string.save);
            }
        });
    }

    private void fetchUrlAndSave(StorageReference storageRef, String description, int retriesLeft) {
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri downloadUri) {
                String photoId = databaseReference.push().getKey();
                long timestampMs = System.currentTimeMillis();
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(timestampMs));

                Photo photo = new Photo(photoId, currentUser.getUid(), downloadUri.toString(), description, timestamp);
                photo.setTimestampMs(timestampMs);

                if (photoId != null) {
                    databaseReference.child(photoId).setValue(photo)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(UploadActivity.this, R.string.photo_saved, Toast.LENGTH_SHORT).show();
                                    etDescription.setText("");
                                    ivImagePreview.setImageURI(null);
                                    ivPlaceholder.setVisibility(View.VISIBLE);
                                    ivImagePreview.setVisibility(View.GONE);
                                    imageUri = null;
                                    btnSave.setEnabled(true);
                                    btnSave.setText(R.string.save);
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(UploadActivity.this, R.string.photo_save_failed + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    btnSave.setEnabled(true);
                                    btnSave.setText(R.string.save);
                                }
                            });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (retriesLeft > 0) {
                    btnSave.postDelayed(() -> fetchUrlAndSave(storageRef, description, retriesLeft - 1), 600);
                } else {
                    Toast.makeText(UploadActivity.this, "Unable to get file URL after upload: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText(R.string.save);
                }
            }
        });
    }
}

