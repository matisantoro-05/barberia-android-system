package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AdminHomeActivity extends AppCompatActivity {

    private Button btnManageUsers, btnManageAppointments, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        mAuth = FirebaseAuth.getInstance();

        btnManageUsers = findViewById(R.id.btnManageUsers);
        btnManageAppointments = findViewById(R.id.btnManageAppointments);
        btnLogout = findViewById(R.id.btnLogout);

        btnManageUsers.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, CrudUsuariosActivity.class);
            startActivity(intent);
        });

        btnManageAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(AdminHomeActivity.this, CrudTurnosActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}