package com.example.photojournalapp_v3.vm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.photojournalapp_v3.repo.PhotoRepository;

import java.io.File;

public class AddEditViewModel extends AndroidViewModel {
    private final PhotoRepository repository;
    private final MutableLiveData<String> selectedImagePath = new MutableLiveData<>();

    public AddEditViewModel(@NonNull Application application) {
        super(application);
        repository = new PhotoRepository(application);
    }

    public LiveData<String> getSelectedImagePath() {
        return selectedImagePath;
    }

    public void setSelectedImagePath(String path) {
        selectedImagePath.setValue(path);
    }

    public void saveEntry(String title, String description) {
        String path = selectedImagePath.getValue();
        if (path == null || path.isEmpty()) return;
        repository.insert(title, description, new File(path));
    }
}


