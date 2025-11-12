package com.example.photojournal.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.photojournal.R;
import com.example.photojournal.ui.main.MainActivity;
import com.example.photojournal.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private Button btnLogin;
    private TextView textViewError;
    private TextView textViewRegister;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnLogin = findViewById(R.id.btnLogin);
        textViewError = findViewById(R.id.textViewError);
        textViewRegister = findViewById(R.id.textViewRegister);
        progressBar = findViewById(R.id.progressBar);
        
        // Check if already logged in
        if (authViewModel.isLoggedIn()) {
            navigateToMain();
            return;
        }
        
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        // Observe ViewModel
        authViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                navigateToMain();
            }
        });
        
        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                textViewError.setText(error);
                textViewError.setVisibility(View.VISIBLE);
            } else {
                textViewError.setVisibility(View.GONE);
            }
        });
        
        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnLogin.setEnabled(!isLoading);
        });
    }
    
    private void attemptLogin() {
        String usernameOrEmail = editTextUsername.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        
        if (TextUtils.isEmpty(usernameOrEmail)) {
            textViewError.setText("Username or email is required");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        if (TextUtils.isEmpty(password)) {
            textViewError.setText("Password is required");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        textViewError.setVisibility(View.GONE);
        authViewModel.login(usernameOrEmail, password);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

