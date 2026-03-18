package com.example.barberia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class UserHomeActivity extends AppCompatActivity {

    private Button btnAvailableAppointments, btnMyAppointments, btnLogout;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        mAuth = FirebaseAuth.getInstance();

        btnAvailableAppointments = findViewById(R.id.btnAvailableAppointments);
        btnMyAppointments = findViewById(R.id.btnMyAppointments);
        btnLogout = findViewById(R.id.btnLogout);

        btnAvailableAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, TurnosDisponiblesActivity.class);
            startActivity(intent);
        });

        btnMyAppointments.setOnClickListener(v -> {
            Intent intent = new Intent(UserHomeActivity.this, MisTurnosActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(UserHomeActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}