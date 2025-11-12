package com.example.photojournal.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.photojournal.data.database.entities.User;
import com.example.photojournal.data.repositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository authRepository;
    private MutableLiveData<User> currentUser = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    
    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);
        loadCurrentUser();
    }
    
    private void loadCurrentUser() {
        if (authRepository.isLoggedIn()) {
            User user = authRepository.getCurrentUser();
            currentUser.setValue(user);
        }
    }
    
    public void register(String username, String email, String password) {
        isLoading.setValue(true);
        authRepository.register(username, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                currentUser.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public void login(String usernameOrEmail, String password) {
        isLoading.setValue(true);
        authRepository.login(usernameOrEmail, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                isLoading.setValue(false);
                currentUser.setValue(user);
            }
            
            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }
    
    public void logout() {
        authRepository.logout();
        currentUser.setValue(null);
    }
    
    public LiveData<User> getCurrentUser() {
        return currentUser;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
}

