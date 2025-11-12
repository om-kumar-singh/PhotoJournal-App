package com.example.photojournal.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.photojournal.R;
import com.example.photojournal.ui.main.MainActivity;
import com.example.photojournal.viewmodel.AuthViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button btnRegister;
    private TextView textViewError;
    private TextView textViewLogin;
    private ProgressBar progressBar;
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        textViewError = findViewById(R.id.textViewError);
        textViewLogin = findViewById(R.id.textViewLogin);
        progressBar = findViewById(R.id.progressBar);
        
        btnRegister.setOnClickListener(v -> attemptRegister());
        
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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
            btnRegister.setEnabled(!isLoading);
        });
    }
    
    private void attemptRegister() {
        String username = editTextUsername.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        
        if (TextUtils.isEmpty(username)) {
            textViewError.setText("Username is required");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        if (TextUtils.isEmpty(email)) {
            textViewError.setText("Email is required");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textViewError.setText("Invalid email address");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            textViewError.setText("Password must be at least 6 characters");
            textViewError.setVisibility(View.VISIBLE);
            return;
        }
        
        textViewError.setVisibility(View.GONE);
        authViewModel.register(username, email, password);
    }
    
    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

