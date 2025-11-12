package com.example.photojournalapp_v3.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.photojournalapp_v3.data.AppDatabase;
import com.example.photojournalapp_v3.data.PhotoEntryDao;
import com.example.photojournalapp_v3.model.PhotoEntry;
import com.example.photojournalapp_v3.repo.PhotoRepository;

public class DetailViewModel extends AndroidViewModel {
    private final PhotoRepository repository;
    private LiveData<PhotoEntry> entry;

    public DetailViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotoRepository(application);
    }

    public void load(long id) {
        entry = repository.observeById(id);
    }

    public LiveData<PhotoEntry> getEntry() {
        return entry;
    }

    public void delete(PhotoEntry e) {
        repository.delete(e);
    }
}


