package com.example.barberia;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private Button btnRegister;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(name)) {
            etName.setError(getString(R.string.empty_fields));
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getString(R.string.empty_fields));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.empty_fields));
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.empty_fields));
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError(getString(R.string.passwords_not_match));
            Toast.makeText(this, R.string.passwords_not_match, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        // Usar hilo secundario para operación de red
        executorService.execute(() -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                createUserInFirestore(user.getUid(), name, email, phone);
                            }
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnRegister.setEnabled(true);
                            runOnUiThread(() ->
                                    Toast.makeText(RegisterActivity.this,
                                            getString(R.string.register_failed),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    private void createUserInFirestore(String userId, String name, String email, String phone) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", name);
        userData.put("email", email);
        userData.put("tipo", "usuario"); // Por defecto es usuario
        userData.put("telefono", phone);

        executorService.execute(() -> {
            db.collection("usuarios").document(userId).set(userData)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.register_success),
                                    Toast.LENGTH_SHORT).show();
                            // Cerrar sesión y volver al login
                            mAuth.signOut();
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        runOnUiThread(() ->
                                Toast.makeText(RegisterActivity.this,
                                        getString(R.string.register_failed),
                                        Toast.LENGTH_SHORT).show()
                        );
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
