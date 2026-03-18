package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);

        // Verificar si ya hay usuario logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserTypeAndRedirect(currentUser.getUid());
        }

        btnLogin.setOnClickListener(v -> loginUser());
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.empty_fields));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.empty_fields));
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Usar hilo secundario para operación de red
        executorService.execute(() -> {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        btnLogin.setEnabled(true);

                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserTypeAndRedirect(user.getUid());
                            }
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this,
                                            getString(R.string.login_failed),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    private void checkUserTypeAndRedirect(String userId) {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            db.collection("usuarios").document(userId).get()
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String tipo = document.getString("tipo");
                                Intent intent;

                                if ("admin".equals(tipo)) {
                                    intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                } else {
                                    intent = new Intent(LoginActivity.this, UserHomeActivity.class);
                                }

                                startActivity(intent);
                                finish();
                            }
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(LoginActivity.this,
                                            getString(R.string.error_loading_data),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
