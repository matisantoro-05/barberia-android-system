package com.example.barberia;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.barberia.models.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditUsuarioDialog extends Dialog {

    private EditText etNombre, etEmail, etTelefono, etPassword;
    private Spinner spinnerTipo;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private Usuario usuario;
    private OnUsuarioSavedListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ExecutorService executorService;
    private boolean isEditMode;

    public interface OnUsuarioSavedListener {
        void onUsuarioSaved();
    }

    public AddEditUsuarioDialog(@NonNull Context context, Usuario usuario, OnUsuarioSavedListener listener) {
        super(context);
        this.usuario = usuario;
        this.listener = listener;
        this.isEditMode = (usuario != null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_edit_usuario);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etPassword = findViewById(R.id.etPassword);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Configurar spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.user_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        // Si es modo edición, llenar campos
        if (isEditMode) {
            setTitle("Editar Usuario");
            etNombre.setText(usuario.getNombre());
            etEmail.setText(usuario.getEmail());
            etEmail.setEnabled(false); // No permitir cambiar email
            etTelefono.setText(usuario.getTelefono());
            etPassword.setVisibility(View.GONE); // No mostrar password en edición

            if ("admin".equals(usuario.getTipo())) {
                spinnerTipo.setSelection(0);
            } else {
                spinnerTipo.setSelection(1);
            }
        } else {
            setTitle("Agregar Usuario");
        }

        btnSave.setOnClickListener(v -> saveUsuario());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void saveUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String tipo = spinnerTipo.getSelectedItem().toString().toLowerCase();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email)) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isEditMode && TextUtils.isEmpty(password)) {
            Toast.makeText(getContext(), "Ingresá una contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if (isEditMode) {
            updateUsuario(nombre, email, telefono, tipo);
        } else {
            createUsuario(nombre, email, telefono, tipo, password);
        }
    }

    private void createUsuario(String nombre, String email, String telefono, String tipo, String password) {
        executorService.execute(() -> {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();
                            saveToFirestore(userId, nombre, email, telefono, tipo);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            btnSave.setEnabled(true);
                            Toast.makeText(getContext(), R.string.operation_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void updateUsuario(String nombre, String email, String telefono, String tipo) {
        saveToFirestore(usuario.getId(), nombre, email, telefono, tipo);
    }

    private void saveToFirestore(String userId, String nombre, String email, String telefono, String tipo) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("telefono", telefono);
        userData.put("tipo", tipo);

        executorService.execute(() -> {
            db.collection("usuarios").document(userId).set(userData)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), R.string.operation_success, Toast.LENGTH_SHORT).show();
                        listener.onUsuarioSaved();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), R.string.operation_failed, Toast.LENGTH_SHORT).show();
                    });
        });
    }
}