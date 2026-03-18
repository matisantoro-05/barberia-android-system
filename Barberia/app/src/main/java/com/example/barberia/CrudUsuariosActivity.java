package com.example.barberia;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberia.adapters.UsuariosAdapter;
import com.example.barberia.models.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrudUsuariosActivity extends AppCompatActivity implements UsuariosAdapter.OnUsuarioClickListener {

    private RecyclerView recyclerView;
    private UsuariosAdapter adapter;
    private List<Usuario> usuariosList;
    private FloatingActionButton fabAdd;
    private ProgressBar progressBar;

    private FirebaseFirestore db;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crud_usuarios);

        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = findViewById(R.id.recyclerView);
        fabAdd = findViewById(R.id.fabAdd);
        progressBar = findViewById(R.id.progressBar);

        usuariosList = new ArrayList<>();
        adapter = new UsuariosAdapter(usuariosList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> showAddEditDialog(null));

        loadUsuarios();
    }

    private void loadUsuarios() {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            db.collection("usuarios")
                    .get()
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            usuariosList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Usuario usuario = document.toObject(Usuario.class);
                                usuario.setId(document.getId());
                                usuariosList.add(usuario);
                            }
                            runOnUiThread(() -> adapter.notifyDataSetChanged());
                        } else {
                            runOnUiThread(() ->
                                    Toast.makeText(CrudUsuariosActivity.this,
                                            getString(R.string.error_loading_data),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    });
        });
    }

    @Override
    public void onEditClick(Usuario usuario) {
        showAddEditDialog(usuario);
    }

    @Override
    public void onDeleteClick(Usuario usuario) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_delete))
                .setMessage("¿Eliminar a " + usuario.getNombre() + "?")
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> deleteUsuario(usuario))
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void deleteUsuario(Usuario usuario) {
        progressBar.setVisibility(View.VISIBLE);

        executorService.execute(() -> {
            db.collection("usuarios").document(usuario.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() -> {
                            Toast.makeText(CrudUsuariosActivity.this,
                                    getString(R.string.operation_success),
                                    Toast.LENGTH_SHORT).show();
                            loadUsuarios();
                        });
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        runOnUiThread(() ->
                                Toast.makeText(CrudUsuariosActivity.this,
                                        getString(R.string.operation_failed),
                                        Toast.LENGTH_SHORT).show()
                        );
                    });
        });
    }

    private void showAddEditDialog(Usuario usuario) {
        AddEditUsuarioDialog dialog = new AddEditUsuarioDialog(this, usuario, new AddEditUsuarioDialog.OnUsuarioSavedListener() {
            @Override
            public void onUsuarioSaved() {
                loadUsuarios();
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