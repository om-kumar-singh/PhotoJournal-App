package com.example.photojournal.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.photojournal.data.database.entities.Entry;
import com.example.photojournal.data.repositories.EntryRepository;

public class AddEditEntryViewModel extends AndroidViewModel {
    private EntryRepository entryRepository;
    private MutableLiveData<Entry> savedEntry = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public AddEditEntryViewModel(Application application) {
        super(application);
        entryRepository = new EntryRepository(application);
    }
    
    public void saveEntry(String title, String description, String photoPath, long dateTaken) {
        isLoading.setValue(true);
        Entry entry = new Entry(title, description, photoPath);
        entry.setDateTaken(dateTaken);
        
        entryRepository.insertEntry(entry, new EntryRepository.EntryCallback() {
            @Override
            public void onSuccess(Entry saved) {
                isLoading.setValue(false);
                savedEntry.setValue(saved);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public void updateEntry(Entry entry) {
        isLoading.setValue(true);
        entryRepository.updateEntry(entry, new EntryRepository.EntryCallback() {
            @Override
            public void onSuccess(Entry updated) {
                isLoading.setValue(false);
                savedEntry.setValue(updated);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public LiveData<Entry> getSavedEntry() {
        return savedEntry;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}

