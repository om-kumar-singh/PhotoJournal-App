package com.example.photojournalapp_v3.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "photo_entries")
public class PhotoEntry {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String title;

    public String description;

    @NonNull
    public String imagePath; // absolute path in app-specific dir

    public long createdAt;

    public long updatedAt;
}


