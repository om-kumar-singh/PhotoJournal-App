package com.example.photojournalapp_v3.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.photojournalapp_v3.model.PhotoEntry;
import com.example.photojournalapp_v3.repo.PhotoRepository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final PhotoRepository repository;
    private final LiveData<List<PhotoEntry>> entries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotoRepository(application);
        entries = repository.observeAll();
    }

    public LiveData<List<PhotoEntry>> getEntries() {
        return entries;
    }
}


