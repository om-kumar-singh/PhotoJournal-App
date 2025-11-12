package com.example.photojournalapp_v3.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ImageStorage {
    private static final String SUBDIR = "PhotoJournal";

    public static File getImagesDir(Context context) {
        File base = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dir = new File(base, SUBDIR);
        if (!dir.exists()) {
            // noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
        return dir;
    }

    public static File createNewImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "PJ_" + timeStamp + "_" + UUID.randomUUID().toString().substring(0, 8);
        File storageDir = getImagesDir(context);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        return image;
    }

    public static File copyFromUri(Context context, Uri uri) throws IOException {
        ContentResolver resolver = context.getContentResolver();
        File dest = createNewImageFile(context);
        try (InputStream in = resolver.openInputStream(uri);
             FileOutputStream out = new FileOutputStream(dest)) {
            if (in == null) throw new IOException("Unable to open input stream: " + uri);
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
            out.flush();
        }
        return dest;
    }

    public static boolean deleteFileQuietly(String absolutePath) {
        if (absolutePath == null) return true;
        File f = new File(absolutePath);
        return !f.exists() || f.delete();
    }
}


