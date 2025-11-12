package com.example.photojournal.data.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.photojournal.data.database.AppDatabase;
import com.example.photojournal.data.database.daos.EntryDao;
import com.example.photojournal.data.database.entities.Entry;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EntryRepository {
    private EntryDao entryDao;
    private ExecutorService executor;
    private Handler mainHandler;
    
    public EntryRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.entryDao = database.entryDao();
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public interface EntryCallback {
        void onSuccess(Entry entry);
        void onError(String error);
    }
    
    public interface EntryListCallback {
        void onSuccess(List<Entry> entries);
        void onError(String error);
    }
    
    public void insertEntry(Entry entry, EntryCallback callback) {
        executor.execute(() -> {
            try {
                long id = entryDao.insertEntry(entry);
                entry.setId((int) id);
                mainHandler.post(() -> callback.onSuccess(entry));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Failed to save entry: " + e.getMessage()));
            }
        });
    }
    
    public void updateEntry(Entry entry, EntryCallback callback) {
        executor.execute(() -> {
            try {
                entry.setUpdatedAt(System.currentTimeMillis());
                entryDao.updateEntry(entry);
                mainHandler.post(() -> callback.onSuccess(entry));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Failed to update entry: " + e.getMessage()));
            }
        });
    }
    
    public void deleteEntry(Entry entry, EntryCallback callback) {
        executor.execute(() -> {
            try {
                entryDao.deleteEntry(entry);
                mainHandler.post(() -> callback.onSuccess(entry));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Failed to delete entry: " + e.getMessage()));
            }
        });
    }
    
    public void getEntryById(int id, EntryCallback callback) {
        executor.execute(() -> {
            try {
                Entry entry = entryDao.getEntryById(id);
                mainHandler.post(() -> callback.onSuccess(entry));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Failed to load entry: " + e.getMessage()));
            }
        });
    }
    
    public void searchEntries(String query, EntryListCallback callback) {
        executor.execute(() -> {
            try {
                List<Entry> entries = entryDao.searchEntries(query);
                mainHandler.post(() -> callback.onSuccess(entries));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Search failed: " + e.getMessage()));
            }
        });
    }
    
    public void getAllEntries(EntryListCallback callback) {
        executor.execute(() -> {
            try {
                List<Entry> entries = entryDao.getAllEntries();
                mainHandler.post(() -> callback.onSuccess(entries));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Failed to load entries: " + e.getMessage()));
            }
        });
    }
    
    public interface EntryDeleteCallback {
        void onSuccess();
        void onError(String error);
    }
}

