package com.example.barberia;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.barberia.models.Turno;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditTurnoDialog extends Dialog {

    private EditText etFecha, etHora;
    private Button btnSave, btnCancel;
    private ProgressBar progressBar;

    private Turno turno;
    private OnTurnoSavedListener listener;
    private FirebaseFirestore db;
    private ExecutorService executorService;
    private boolean isEditMode;

    public interface OnTurnoSavedListener {
        void onTurnoSaved();
    }

    public AddEditTurnoDialog(@NonNull Context context, Turno turno, OnTurnoSavedListener listener) {
        super(context);
        this.turno = turno;
        this.listener = listener;
        this.isEditMode = (turno != null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_edit_turno);

        db = FirebaseFirestore.getInstance();
        executorService = Executors.newSingleThreadExecutor();

        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        progressBar = findViewById(R.id.progressBar);

        // Si es modo edición, llenar campos
        if (isEditMode) {
            setTitle("Editar Turno");
            etFecha.setText(turno.getFecha());
            etHora.setText(turno.getHora());
        } else {
            setTitle("Agregar Turno");
        }

        // DatePicker para fecha
        etFecha.setOnClickListener(v -> showDatePicker());

        // TimePicker para hora
        etHora.setOnClickListener(v -> showTimePicker());

        btnSave.setOnClickListener(v -> saveTurno());
        btnCancel.setOnClickListener(v -> dismiss());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String fecha = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    etFecha.setText(fecha);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getContext(),
                (view, selectedHour, selectedMinute) -> {
                    String hora = String.format("%02d:%02d", selectedHour, selectedMinute);
                    etHora.setText(hora);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }

    private void saveTurno() {
        String fecha = etFecha.getText().toString().trim();
        String hora = etHora.getText().toString().trim();

        if (TextUtils.isEmpty(fecha) || TextUtils.isEmpty(hora)) {
            Toast.makeText(getContext(), R.string.empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        Map<String, Object> turnoData = new HashMap<>();
        turnoData.put("fecha", fecha);
        turnoData.put("hora", hora);

        if (isEditMode) {
            // Mantener el estado actual y cliente si existe
            turnoData.put("estado", turno.getEstado());
            turnoData.put("clienteId", turno.getClienteId());
            turnoData.put("clienteNombre", turno.getClienteNombre());
            updateTurno(turnoData);
        } else {
            // Nuevo turno: estado disponible y sin cliente
            turnoData.put("estado", "disponible");
            turnoData.put("clienteId", null);
            turnoData.put("clienteNombre", null);
            createTurno(turnoData);
        }
    }

    private void createTurno(Map<String, Object> turnoData) {
        executorService.execute(() -> {
            db.collection("turnos").add(turnoData)
                    .addOnSuccessListener(documentReference -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), R.string.operation_success, Toast.LENGTH_SHORT).show();
                        listener.onTurnoSaved();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), R.string.operation_failed, Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void updateTurno(Map<String, Object> turnoData) {
        executorService.execute(() -> {
            db.collection("turnos").document(turno.getId()).set(turnoData)
                    .addOnSuccessListener(aVoid -> {
                        progressBar.setVisibility(View.GONE);
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), R.string.operation_success, Toast.LENGTH_SHORT).show();
                        listener.onTurnoSaved();
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