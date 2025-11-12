package com.example.photojournalapp_v3;

import static org.junit.Assert.assertNotNull;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.photojournalapp_v3.data.AppDatabase;
import com.example.photojournalapp_v3.data.PhotoEntryDao;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DatabaseSmokeTest {
    @Test
    public void dbOpens_andDaoAccessible() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase db = AppDatabase.getInstance(context);
        PhotoEntryDao dao = db.photoEntryDao();
        assertNotNull(dao);
    }
}


