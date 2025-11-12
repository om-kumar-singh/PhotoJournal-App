package com.example.photojournal.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import com.example.photojournal.data.database.entities.Entry;
import java.io.File;

public class ShareUtils {
    
    /**
     * Share entry to external apps
     */
    public static void shareEntry(Context context, Entry entry) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        
        // Share image if available
        if (entry.getPhotoPath() != null && !entry.getPhotoPath().isEmpty()) {
            String photoPath = entry.getPhotoPath();
            Uri photoUri;
            
            if (photoPath.startsWith("file://") || photoPath.startsWith("/")) {
                photoUri = photoPath.startsWith("file://") ? Uri.parse(photoPath) : Uri.parse("file://" + photoPath);
            } else {
                photoUri = Uri.parse(photoPath);
            }
            
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        } else {
            shareIntent.setType("text/plain");
        }
        
        // Add text content
        StringBuilder shareText = new StringBuilder();
        if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
            shareText.append(entry.getTitle()).append("\n\n");
        }
        if (entry.getDescription() != null && !entry.getDescription().isEmpty()) {
            shareText.append(entry.getDescription()).append("\n\n");
        }
        shareText.append("Shared from Photo Journal");
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        try {
            context.startActivity(Intent.createChooser(shareIntent, "Share Entry"));
        } catch (Exception e) {
            Toast.makeText(context, "No app available to share", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Share to specific platform
     */
    public static void shareToPlatform(Context context, Entry entry, String platform) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        
        if (entry.getPhotoPath() != null && !entry.getPhotoPath().isEmpty()) {
            String photoPath = entry.getPhotoPath();
            Uri photoUri;
            
            if (photoPath.startsWith("file://") || photoPath.startsWith("/")) {
                photoUri = photoPath.startsWith("file://") ? Uri.parse(photoPath) : Uri.parse("file://" + photoPath);
            } else {
                photoUri = Uri.parse(photoPath);
            }
            
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoUri);
        } else {
            shareIntent.setType("text/plain");
        }
        
        StringBuilder shareText = new StringBuilder();
        if (entry.getTitle() != null && !entry.getTitle().isEmpty()) {
            shareText.append(entry.getTitle()).append("\n\n");
        }
        if (entry.getDescription() != null && !entry.getDescription().isEmpty()) {
            shareText.append(entry.getDescription());
        }
        
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText.toString());
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        
        // Set package for specific platform
        switch (platform.toLowerCase()) {
            case "instagram":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "facebook":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "twitter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }
        
        try {
            context.startActivity(shareIntent);
        } catch (Exception e) {
            // Fallback to general share
            shareEntry(context, entry);
        }
    }
}

