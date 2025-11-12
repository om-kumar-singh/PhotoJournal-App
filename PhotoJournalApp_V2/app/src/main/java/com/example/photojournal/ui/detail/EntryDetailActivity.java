package com.example.photojournal.ui.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.bumptech.glide.Glide;
import com.example.photojournal.R;
import com.example.photojournal.data.database.entities.Entry;
import com.example.photojournal.data.repositories.EntryRepository;
import com.example.photojournal.ui.addedit.AddEditEntryActivity;
import com.example.photojournal.ui.imageedit.ImageEditActivity;
import com.example.photojournal.util.ShareUtils;
import com.example.photojournal.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryDetailActivity extends AppCompatActivity {
    private ImageView imageViewPhoto;
    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewDate;
    private ProgressBar progressBar;
    
    private Entry entry;
    private EntryRepository entryRepository;
    private MainViewModel mainViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_entry_detail);
        
        entry = (Entry) getIntent().getSerializableExtra("entry");
        if (entry == null) {
            Toast.makeText(this, "Entry not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        entryRepository = new EntryRepository(this);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        initViews();
        setupObservers();
    }
    
    private void initViews() {
        imageViewPhoto = findViewById(R.id.imageViewPhoto);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewDescription = findViewById(R.id.textViewDescription);
        textViewDate = findViewById(R.id.textViewDate);
        progressBar = findViewById(R.id.progressBar);
        
        
        // Load image
        if (entry.getPhotoPath() != null && !entry.getPhotoPath().isEmpty()) {
            String photoPath = entry.getPhotoPath();
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
        }
        
        textViewTitle.setText(entry.getTitle() != null && !entry.getTitle().isEmpty() 
            ? entry.getTitle() : "Untitled");
        textViewDescription.setText(entry.getDescription());
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault());
        if (entry.getDateTaken() > 0) {
            textViewDate.setText("Date: " + dateFormat.format(new Date(entry.getDateTaken())));
        } else {
            textViewDate.setText("Created: " + dateFormat.format(new Date(entry.getCreatedAt())));
        }
        
        imageViewPhoto.setOnClickListener(v -> {
            // Open image edit activity
            Intent intent = new Intent(EntryDetailActivity.this, ImageEditActivity.class);
            intent.putExtra("imagePath", entry.getPhotoPath());
            startActivityForResult(intent, 100);
        });
    }
    
    private void setupObservers() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_detail_menu, menu);
        // All entries can be edited/deleted (no authentication)
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_edit) {
            editEntry();
            return true;
        } else if (id == R.id.action_delete) {
            deleteEntry();
            return true;
        } else if (id == R.id.action_edit_image) {
            editImage();
            return true;
        } else if (id == R.id.action_share) {
            ShareUtils.shareEntry(this, entry);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    private void editEntry() {
        Intent intent = new Intent(EntryDetailActivity.this, AddEditEntryActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }
    
    private void editImage() {
        Intent intent = new Intent(EntryDetailActivity.this, ImageEditActivity.class);
        intent.putExtra("imagePath", entry.getPhotoPath());
        startActivityForResult(intent, 100);
    }
    
    private void deleteEntry() {
        new AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete", (dialog, which) -> {
                entryRepository.deleteEntry(entry, new EntryRepository.EntryCallback() {
                    @Override
                    public void onSuccess(Entry deletedEntry) {
                        Toast.makeText(EntryDetailActivity.this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    
                    @Override
                    public void onError(String error) {
                        Toast.makeText(EntryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String editedImagePath = data.getStringExtra("editedImagePath");
            if (editedImagePath != null) {
                entry.setPhotoPath(editedImagePath);
                entryRepository.updateEntry(entry, new EntryRepository.EntryCallback() {
                    @Override
                    public void onSuccess(Entry updatedEntry) {
                        // Reload image
                        Uri photoUri = Uri.parse("file://" + editedImagePath);
                        Glide.with(EntryDetailActivity.this)
                            .load(photoUri)
                            .centerCrop()
                            .into(imageViewPhoto);
                    }
                    
                    @Override
                    public void onError(String error) {
                        Toast.makeText(EntryDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}

