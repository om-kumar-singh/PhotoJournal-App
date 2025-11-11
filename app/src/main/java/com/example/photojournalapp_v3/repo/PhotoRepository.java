package com.example.photojournalapp_v3.repo;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.example.photojournalapp_v3.data.AppDatabase;
import com.example.photojournalapp_v3.data.PhotoEntryDao;
import com.example.photojournalapp_v3.model.PhotoEntry;
import com.example.photojournalapp_v3.storage.ImageStorage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PhotoRepository {
    private final PhotoEntryDao dao;
    private final Context appContext;

    public PhotoRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.dao = AppDatabase.getInstance(appContext).photoEntryDao();
    }

    public LiveData<List<PhotoEntry>> observeAll() {
        return dao.observeAll();
    }

    public LiveData<PhotoEntry> observeById(long id) {
        return dao.observeById(id);
    }

    public long insert(String title, String description, File imageFile) {
        PhotoEntry entry = new PhotoEntry();
        entry.title = title == null ? "" : title;
        entry.description = description;
        entry.imagePath = imageFile.getAbsolutePath();
        long now = System.currentTimeMillis();
        entry.createdAt = now;
        entry.updatedAt = now;
        return dao.insert(entry);
    }

    public int update(PhotoEntry entry, String newTitle, String newDescription) {
        entry.title = newTitle == null ? "" : newTitle;
        entry.description = newDescription;
        entry.updatedAt = System.currentTimeMillis();
        return dao.update(entry);
    }

    public int replaceImage(PhotoEntry entry, Uri source) throws IOException {
        File copied = ImageStorage.copyFromUri(appContext, source);
        // delete old file
        ImageStorage.deleteFileQuietly(entry.imagePath);
        entry.imagePath = copied.getAbsolutePath();
        entry.updatedAt = System.currentTimeMillis();
        return dao.update(entry);
    }

    public int delete(PhotoEntry entry) {
        ImageStorage.deleteFileQuietly(entry.imagePath);
        return dao.delete(entry);
    }
}


