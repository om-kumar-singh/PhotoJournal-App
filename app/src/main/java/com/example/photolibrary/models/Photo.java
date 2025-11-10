package com.example.photolibrary.models;

public class Photo {
    private String id;
    private String userId;
    private String imageUrl;
    private String description;
    private String timestamp;
    private long timestampMs;

    public Photo() {
        // Default constructor required for Firebase
    }

    public Photo(String id, String userId, String imageUrl, String description, String timestamp) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public long getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(long timestampMs) {
        this.timestampMs = timestampMs;
    }
}

