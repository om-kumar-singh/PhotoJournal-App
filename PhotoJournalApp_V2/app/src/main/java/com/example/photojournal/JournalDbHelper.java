package com.example.photojournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class JournalDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "journal.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ENTRIES = "entries";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOTE = "note";
    private static final String COLUMN_PHOTO_PATH = "photo_path";

    public JournalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_ENTRIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTE + " TEXT, " +
                COLUMN_PHOTO_PATH + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRIES);
        onCreate(db);
    }

    public void addEntry(JournalEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTE, entry.getNote());
        values.put(COLUMN_PHOTO_PATH, entry.getPhotoPath());
        db.insert(TABLE_ENTRIES, null, values);
        db.close();
    }

    public List<JournalEntry> getAllEntries() {
        List<JournalEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ENTRIES, null);
        if (cursor.moveToFirst()) {
            do {
                JournalEntry entry = new JournalEntry(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2)
                );
                entries.add(entry);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return entries;
    }
}