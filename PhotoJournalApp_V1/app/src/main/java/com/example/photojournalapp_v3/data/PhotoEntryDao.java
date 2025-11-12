package com.example.photojournalapp_v3.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.photojournalapp_v3.model.PhotoEntry;

import java.util.List;

@Dao
public interface PhotoEntryDao {
    @Query("SELECT * FROM photo_entries ORDER BY createdAt DESC")
    LiveData<List<PhotoEntry>> observeAll();

    @Query("SELECT * FROM photo_entries WHERE id = :id LIMIT 1")
    LiveData<PhotoEntry> observeById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(PhotoEntry entry);

    @Update
    int update(PhotoEntry entry);

    @Delete
    int delete(PhotoEntry entry);
}


