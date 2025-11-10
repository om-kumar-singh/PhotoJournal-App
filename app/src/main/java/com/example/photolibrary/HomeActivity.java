package com.example.photolibrary;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photolibrary.adapters.PhotoAdapter;
import com.example.photolibrary.models.Photo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.example.photolibrary.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView recyclerPhotos;
    private ProgressBar progressLoading;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private ImageButton btnProfile;

    private PhotoAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference photosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        recyclerPhotos = findViewById(R.id.recyclerPhotos);
        progressLoading = findViewById(R.id.progressLoading);
        tvEmpty = findViewById(R.id.tvEmpty);
        fabAdd = findViewById(R.id.fabAdd);
        btnProfile = findViewById(R.id.btnProfile);

        recyclerPhotos.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new PhotoAdapter(this);
        recyclerPhotos.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, UploadActivity.class)));
        btnProfile.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, ProfileActivity.class)));

        // Use explicit database URL via helper to avoid rules/URL mismatch
        photosRef = FirebaseHelper.getPhotosReference();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhotos();
    }

    private void loadPhotos() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        showLoading(true);
        Query query = photosRef.orderByChild("userId").equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Photo> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Photo p = child.getValue(Photo.class);
                    if (p != null) list.add(p);
                }
                // newest first
                Collections.sort(list, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        long t1 = o1.getTimestampMs();
                        long t2 = o2.getTimestampMs();
                        return Long.compare(t2, t1);
                    }
                });
                adapter.setPhotos(list);
                showLoading(false);
                tvEmpty.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showLoading(false);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText(error.getMessage());
            }
        });
    }

    private void showLoading(boolean loading) {
        progressLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        recyclerPhotos.setVisibility(loading ? View.INVISIBLE : View.VISIBLE);
    }
}

