package com.example.photojournal.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.photojournal.data.database.entities.Entry;
import com.example.photojournal.data.repositories.EntryRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private EntryRepository entryRepository;
    private MutableLiveData<List<Entry>> entries = new MutableLiveData<>();
    private MutableLiveData<List<Entry>> heroEntries = new MutableLiveData<>();
    private MutableLiveData<List<Entry>> recentlyAddedEntries = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public MainViewModel(Application application) {
        super(application);
        entryRepository = new EntryRepository(application);
        loadEntries();
    }
    
    public void loadEntries() {
        isLoading.setValue(true);
        entryRepository.getAllEntries(new EntryRepository.EntryListCallback() {
            @Override
            public void onSuccess(List<Entry> entryList) {
                isLoading.setValue(false);
                entries.setValue(entryList);
                
                // Update hero entries (latest 5 entries)
                List<Entry> heroList = new ArrayList<>(entryList);
                Collections.sort(heroList, (e1, e2) -> Long.compare(e2.getCreatedAt(), e1.getCreatedAt()));
                heroEntries.setValue(heroList.size() > 5 ? heroList.subList(0, 5) : heroList);
                
                // Update recently added (latest 10 entries)
                recentlyAddedEntries.setValue(heroList.size() > 10 ? heroList.subList(0, 10) : heroList);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public void searchEntries(String query) {
        isLoading.setValue(true);
        entryRepository.searchEntries(query, new EntryRepository.EntryListCallback() {
            @Override
            public void onSuccess(List<Entry> entryList) {
                isLoading.setValue(false);
                entries.setValue(entryList);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public void deleteEntry(Entry entry) {
        entryRepository.deleteEntry(entry, new EntryRepository.EntryCallback() {
            @Override
            public void onSuccess(Entry deletedEntry) {
                loadEntries(); // Reload list
            }
            
            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }
    
    public LiveData<List<Entry>> getEntries() {
        return entries;
    }
    
    public LiveData<List<Entry>> getHeroEntries() {
        return heroEntries;
    }
    
    public LiveData<List<Entry>> getRecentlyAddedEntries() {
        return recentlyAddedEntries;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}

