package com.example.barberia;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberia.adapters.TurnosAdapter;
import com.example.barberia.models.Turno;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MisTurnosActivity extends AppCompatActivity implements TurnosAdapter.OnTurnoClickListener {

    private RecyclerView recyclerView;
    private TurnosAdapter adapter;
    private List<Turno> turnosList;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_turnos);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        turnosList = new ArrayList<>();
        adapter = new TurnosAdapter(turnosList, this, false); // false = modo usuario

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadMisTurnos();
    }

    private void loadMisTurnos() {
        progressBar.setVisibility(View.VISIBLE);

        String userId = mAuth.getCurrentUser().getUid();

        executorService.execute(() -> {
            db.collection("turnos")
                    .whereEqualTo("clienteId", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            turnosList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Turno turno = document.toObject(Turno.class);
                                turno.setId(document.getId());
                                turnosList.add(turno);
                            }
                            runOnUiThread(() -> adapter.notifyDataSetChanged());

                            if (turnosList.isEmpty()) {
                                runOnUiThread(() ->
                                        Toast.makeText(MisTurnosActivity.this,
                                                "No tenés turnos reservados",
                                                Toast.LENGTH_SHORT).show()
                                );
                            }
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(MisTurnosActivity.this,
                                            getString(R.string.error_loading_data),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    @Override
    public void onCancelClick(Turno turno) {
        new AlertDialog.Builder(this)
                .setTitle("Cancelar Turno")
                .setMessage("¿Estás seguro que querés cancelar el turno del " + turno.getFecha() + " a las " + turno.getHora() + "?")
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> cancelTurno(turno))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void cancelTurno(Turno turno) {
        progressBar.setVisibility(View.VISIBLE);

        Map<String, Object> updates = new HashMap<>();
        updates.put("estado", "disponible");
        updates.put("clienteId", null);
        updates.put("clienteNombre", null);

        executorService.execute(() -> {
            db.collection("turnos").document(turno.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() -> {
                            Toast.makeText(MisTurnosActivity.this,
                                    "Turno cancelado exitosamente",
                                    Toast.LENGTH_SHORT).show();
                            loadMisTurnos();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() ->
                                Toast.makeText(MisTurnosActivity.this,
                                        getString(R.string.operation_failed),
                                        Toast.LENGTH_SHORT).show()
                        );
                    });
        });
    }

    @Override
    public void onBookClick(Turno turno) {
        // No se usa en esta activity
    }

    @Override
    public void onEditClick(Turno turno) {
        // No se usa en modo usuario
    }

    @Override
    public void onDeleteClick(Turno turno) {
        // No se usa en modo usuario
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}