package com.example.photojournal.data.repositories;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.photojournal.data.database.AppDatabase;
import com.example.photojournal.data.database.daos.UserDao;
import com.example.photojournal.data.database.entities.User;
import com.example.photojournal.util.PasswordUtils;
import com.example.photojournal.util.SessionManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthRepository {
    private UserDao userDao;
    private SessionManager sessionManager;
    private ExecutorService executor;
    private Handler mainHandler;
    
    public AuthRepository(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.userDao = database.userDao();
        this.sessionManager = new SessionManager(context);
        this.executor = Executors.newSingleThreadExecutor();
        this.mainHandler = new Handler(Looper.getMainLooper());
    }
    
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    
    public void register(String username, String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Check if username already exists
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser != null) {
                    mainHandler.post(() -> callback.onError("Username already exists"));
                    return;
                }
                
                // Check if email already exists
                existingUser = userDao.getUserByEmail(email);
                if (existingUser != null) {
                    mainHandler.post(() -> callback.onError("Email already exists"));
                    return;
                }
                
                // Validate input
                if (username == null || username.trim().isEmpty()) {
                    mainHandler.post(() -> callback.onError("Username cannot be empty"));
                    return;
                }
                
                if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                    mainHandler.post(() -> callback.onError("Invalid email address"));
                    return;
                }
                
                if (password == null || password.length() < 6) {
                    mainHandler.post(() -> callback.onError("Password must be at least 6 characters"));
                    return;
                }
                
                // Hash password
                String passwordHash = PasswordUtils.hashPassword(password);
                
                // Create new user
                User user = new User(username.trim(), email.trim().toLowerCase(), passwordHash);
                long userId = userDao.insertUser(user);
                user.setId((int) userId);
                
                // Save session
                sessionManager.saveSession(user.getId(), user.getUsername());
                
                mainHandler.post(() -> callback.onSuccess(user));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Registration failed: " + e.getMessage()));
            }
        });
    }
    
    public void login(String usernameOrEmail, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User user = null;
                
                // Try username first
                user = userDao.getUserByUsername(usernameOrEmail);
                
                // If not found, try email
                if (user == null) {
                    user = userDao.getUserByEmail(usernameOrEmail);
                }
                
                if (user == null) {
                    mainHandler.post(() -> callback.onError("User not found"));
                    return;
                }
                
                // Verify password
                if (!PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                    mainHandler.post(() -> callback.onError("Incorrect password"));
                    return;
                }
                
                // Save session
                sessionManager.saveSession(user.getId(), user.getUsername());
                
                // Create final reference for lambda
                final User finalUser = user;
                mainHandler.post(() -> callback.onSuccess(finalUser));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Login failed: " + e.getMessage()));
            }
        });
    }
    
    public void logout() {
        sessionManager.clearSession();
    }
    
    public User getCurrentUser() {
        if (!sessionManager.isLoggedIn()) {
            return null;
        }
        
        int userId = sessionManager.getCurrentUserId();
        return userDao.getUserById(userId);
    }
    
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }
    
    public int getCurrentUserId() {
        return sessionManager.getCurrentUserId();
    }
    
    public void updateUser(User user, AuthCallback callback) {
        executor.execute(() -> {
            try {
                userDao.updateUser(user);
                // Create final reference for lambda
                final User finalUser = user;
                mainHandler.post(() -> callback.onSuccess(finalUser));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Update failed: " + e.getMessage()));
            }
        });
    }
}

