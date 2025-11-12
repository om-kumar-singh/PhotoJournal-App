package com.example.photojournal;

import java.io.Serializable;

public class JournalEntry implements Serializable {
    private int id;
    private String note;
    private String photoPath;

    public JournalEntry(int id, String note, String photoPath) {
        this.id = id;
        this.note = note;
        this.photoPath = photoPath;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getPhotoPath() { return photoPath; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
}