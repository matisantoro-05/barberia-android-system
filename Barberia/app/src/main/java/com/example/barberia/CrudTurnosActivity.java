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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrudTurnosActivity extends AppCompatActivity implements TurnosAdapter.OnTurnoClickListener {

    private RecyclerView recyclerView;
    private TurnosAdapter adapter;
    private List<Turno> turnosList;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_turnos);

        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        progressBar = findViewById(R.id.progressBar);

        turnosList = new ArrayList<>();
        adapter = new TurnosAdapter(turnosList, this, true); // true = modo admin

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        loadTurnos();
    }

    private void loadTurnos() {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            db.collection("turnos")
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
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(CrudTurnosActivity.this,
                                            getString(R.string.error_loading_data),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    @Override
    public void onEditClick(Turno turno) {
        showAddEditDialog(turno);
    }

    @Override
    public void onDeleteClick(Turno turno) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage("¿Eliminar turno del " + turno.getFecha() + " a las " + turno.getHora() + "?")
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteTurno(turno))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    @Override
    public void onBookClick(Turno turno) {
        // No se usa en modo admin
    }

    @Override
    public void onCancelClick(Turno turno) {
        // No se usa en modo admin
    }

    private void deleteTurno(Turno turno) {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            db.collection("turnos").document(turno.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() -> {
                            Toast.makeText(CrudTurnosActivity.this,
                                    getString(R.string.operation_success),
                                    Toast.LENGTH_SHORT).show();
                            loadTurnos();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() ->
                                Toast.makeText(CrudTurnosActivity.this,
                                        getString(R.string.operation_failed),
                                        Toast.LENGTH_SHORT).show()
                        );
                    });
        });
    }

    private void showAddEditDialog(Turno turno) {
        AddEditTurnoDialog dialog = new AddEditTurnoDialog(this, turno, new AddEditTurnoDialog.OnTurnoSavedListener() {
            @Override
            public void onTurnoSaved() {
                loadTurnos();
            }
        });
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}