package com.example.photojournal.data.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;
import com.example.photojournal.data.database.entities.Entry;
import java.util.List;

@Dao
public interface EntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertEntry(Entry entry);
    
    @Update
    void updateEntry(Entry entry);
    
    @Delete
    void deleteEntry(Entry entry);
    
    @Query("SELECT * FROM entries WHERE id = :id")
    Entry getEntryById(int id);
    
    @Query("SELECT * FROM entries ORDER BY createdAt DESC")
    List<Entry> getAllEntries();
    
    @Query("SELECT * FROM entries WHERE (title LIKE '%' || :searchQuery || '%' OR description LIKE '%' || :searchQuery || '%') ORDER BY createdAt DESC")
    List<Entry> searchEntries(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM entries")
    int getEntryCount();
    
    @Query("DELETE FROM entries")
    void deleteAllEntries();
}

