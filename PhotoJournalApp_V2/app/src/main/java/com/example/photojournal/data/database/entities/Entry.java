package com.example.photojournal.data.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Index;
import java.io.Serializable;

@Entity(
    tableName = "entries",
    indices = {@Index("createdAt"), @Index("dateTaken")}
)
public class Entry implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    
    private String title;
    private String description;
    private String photoPath;
    private long dateTaken; // Timestamp when photo was taken
    private long createdAt; // Timestamp when entry was created
    private long updatedAt; // Timestamp when entry was last updated
    
    public Entry() {
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.dateTaken = now;
    }
    
    public Entry(String title, String description, String photoPath) {
        this.title = title;
        this.description = description;
        this.photoPath = photoPath;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.dateTaken = now;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public String getPhotoPath() {
        return photoPath;
    }
    
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public long getDateTaken() {
        return dateTaken;
    }
    
    public void setDateTaken(long dateTaken) {
        this.dateTaken = dateTaken;
        this.updatedAt = System.currentTimeMillis();
    }
    
    public long getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
    
    public long getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}

