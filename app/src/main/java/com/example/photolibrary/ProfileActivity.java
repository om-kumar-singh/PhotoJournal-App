package com.example.photolibrary;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_AVATAR = 1001;

    private ImageView ivAvatar;
    private TextView tvEmail;
    private EditText etFullName;
    private Button btnChangeAvatar, btnSave;

    private Uri avatarUri;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private StorageReference avatarsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            finish();
            return;
        }

        ivAvatar = findViewById(R.id.ivAvatar);
        tvEmail = findViewById(R.id.tvEmail);
        etFullName = findViewById(R.id.etFullName);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        btnSave = findViewById(R.id.btnSave);

        tvEmail.setText(user.getEmail());

        usersRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
        avatarsRef = FirebaseStorage.getInstance().getReference("avatars").child(user.getUid() + ".jpg");

        // Load current profile
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String fullName = snapshot.child("fullName").getValue(String.class);
                String avatarUrl = snapshot.child("avatarUrl").getValue(String.class);
                if (!TextUtils.isEmpty(fullName)) etFullName.setText(fullName);
                if (!TextUtils.isEmpty(avatarUrl)) {
                    Glide.with(ProfileActivity.this).load(avatarUrl).circleCrop().into(ivAvatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        btnChangeAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Choose Avatar"), PICK_AVATAR);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AVATAR && resultCode == RESULT_OK && data != null && data.getData() != null) {
            avatarUri = data.getData();
            Glide.with(this).load(avatarUri).circleCrop().into(ivAvatar);
        }
    }

    private void saveProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;
        String fullName = etFullName.getText().toString().trim();
        Map<String, Object> updates = new HashMap<>();
        updates.put("fullName", fullName);

        if (avatarUri != null) {
            avatarsRef.putFile(avatarUri)
                    .addOnSuccessListener(new OnSuccessListener<com.google.firebase.storage.UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(com.google.firebase.storage.UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            updates.put("avatarUrl", uri.toString());
                                            usersRef.updateChildren(updates);
                                            Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "Avatar upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            usersRef.updateChildren(updates);
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}


