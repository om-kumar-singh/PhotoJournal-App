package com.example.photojournal.data.database.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.photojournal.data.database.entities.User;
import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);
    
    @Update
    void updateUser(User user);
    
    @Delete
    void deleteUser(User user);
    
    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(int id);
    
    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);
    
    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    List<User> getAllUsers();
    
    @Query("SELECT COUNT(*) FROM users")
    int getUserCount();
}

