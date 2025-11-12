package com.example.photojournal.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.photojournal.data.database.entities.User;
import com.example.photojournal.data.database.entities.Entry;

import com.example.photojournal.data.database.daos.UserDao;
import com.example.photojournal.data.database.daos.EntryDao;

@Database(
    entities = {
        User.class,
        Entry.class
    },
    version = 4,
    exportSchema = true
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract EntryDao entryDao();
    
    private static volatile AppDatabase INSTANCE;
    
    private static final RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Create default user with id 0 for single-user mode (no authentication)
            long currentTime = System.currentTimeMillis();
            db.execSQL("INSERT OR IGNORE INTO users (id, username, email, passwordHash, createdAt) " +
                "VALUES (0, 'Default User', 'default@photojournal.app', '', " + currentTime + ")");
        }
    };
    
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "journal_v2.db")
                            .addCallback(roomCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

