package com.example.photojournal.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import com.example.photojournal.R;
import com.example.photojournal.data.database.entities.Entry;
import com.example.photojournal.ui.addedit.AddEditEntryActivity;
import com.example.photojournal.viewmodel.MainViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerViewHero;
    private RecyclerView recyclerViewRecentlyAdded;
    private RecyclerView recyclerViewAllEntries;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    
    private HeroAdapter heroAdapter;
    private EntryRowAdapter recentlyAddedAdapter;
    private EntryAdapter allEntriesAdapter;
    private MainViewModel mainViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        initViews();
        setupRecyclerViews();
        setupBottomNavigation();
        setupObservers();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        mainViewModel.loadEntries();
    }
    
    private void initViews() {
        recyclerViewHero = findViewById(R.id.recyclerViewHero);
        recyclerViewRecentlyAdded = findViewById(R.id.recyclerViewRecentlyAdded);
        recyclerViewAllEntries = findViewById(R.id.recyclerViewAllEntries);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void setupRecyclerViews() {
        // Hero section - horizontal scrolling
        heroAdapter = new HeroAdapter(new ArrayList<>(), entry -> {
            navigateToDetail(entry);
        });
        LinearLayoutManager heroLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHero.setLayoutManager(heroLayoutManager);
        recyclerViewHero.setAdapter(heroAdapter);
        SnapHelper heroSnapHelper = new LinearSnapHelper();
        heroSnapHelper.attachToRecyclerView(recyclerViewHero);
        
        // Recently Added - horizontal scrolling
        recentlyAddedAdapter = new EntryRowAdapter(new ArrayList<>(), entry -> {
            navigateToDetail(entry);
        });
        LinearLayoutManager recentlyAddedLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewRecentlyAdded.setLayoutManager(recentlyAddedLayoutManager);
        recyclerViewRecentlyAdded.setAdapter(recentlyAddedAdapter);
        
        // All Entries - vertical scrolling
        allEntriesAdapter = new EntryAdapter(new ArrayList<>(), entry -> {
            navigateToDetail(entry);
        }, entry -> {
            showDeleteConfirmation(entry);
        });
        LinearLayoutManager allEntriesLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewAllEntries.setLayoutManager(allEntriesLayoutManager);
        recyclerViewAllEntries.setAdapter(allEntriesAdapter);
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.nav_add) {
                Intent intent = new Intent(MainActivity.this, AddEditEntryActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
    
    private void setupObservers() {
        mainViewModel.getHeroEntries().observe(this, entries -> {
            if (entries != null) {
                heroAdapter.updateEntries(entries);
            }
        });
        
        mainViewModel.getRecentlyAddedEntries().observe(this, entries -> {
            if (entries != null) {
                recentlyAddedAdapter.updateEntries(entries);
            }
        });
        
        mainViewModel.getEntries().observe(this, entries -> {
            if (entries != null) {
                allEntriesAdapter.updateEntries(entries);
            }
        });
        
        mainViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
        
        mainViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }
    
    private void navigateToDetail(Entry entry) {
        Intent intent = new Intent(MainActivity.this, com.example.photojournal.ui.detail.EntryDetailActivity.class);
        intent.putExtra("entry", entry);
        startActivity(intent);
    }
    
    private void showDeleteConfirmation(Entry entry) {
        new AlertDialog.Builder(this)
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("Delete", (dialog, which) -> {
                mainViewModel.deleteEntry(entry);
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // No menu
        return true;
    }
}

