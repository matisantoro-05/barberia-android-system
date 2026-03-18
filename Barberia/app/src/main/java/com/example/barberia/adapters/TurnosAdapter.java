package com.example.barberia.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.barberia.R;
import com.example.barberia.models.Turno;

import java.util.List;

public class TurnosAdapter extends RecyclerView.Adapter<TurnosAdapter.TurnoViewHolder> {

    private List<Turno> turnosList;
    private OnTurnoClickListener listener;
    private boolean isAdminMode;

    public interface OnTurnoClickListener {
        void onEditClick(Turno turno);
        void onDeleteClick(Turno turno);
        void onBookClick(Turno turno);
        void onCancelClick(Turno turno);
    }

    public TurnosAdapter(List<Turno> turnosList, OnTurnoClickListener listener, boolean isAdminMode) {
        this.turnosList = turnosList;
        this.listener = listener;
        this.isAdminMode = isAdminMode;
    }

    @NonNull
    @Override
    public TurnoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_turno, parent, false);
        return new TurnoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TurnoViewHolder holder, int position) {
        Turno turno = turnosList.get(position);

        holder.tvFecha.setText("Fecha: " + turno.getFecha());
        holder.tvHora.setText("Hora: " + turno.getHora());
        holder.tvEstado.setText("Estado: " + turno.getEstado());

        if (turno.getClienteNombre() != null && !turno.getClienteNombre().isEmpty()) {
            holder.tvCliente.setText("Cliente: " + turno.getClienteNombre());
            holder.tvCliente.setVisibility(View.VISIBLE);
        } else {
            holder.tvCliente.setVisibility(View.GONE);
        }

        // Configurar botones según el modo
        if (isAdminMode) {
            // Modo Admin: mostrar Editar y Eliminar
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnBook.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

            holder.btnEdit.setOnClickListener(v -> listener.onEditClick(turno));
            holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(turno));
        } else {
            // Modo Usuario
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);

            if (turno.isDisponible()) {
                // Turno disponible: mostrar botón Sacar Turno
                holder.btnBook.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnBook.setOnClickListener(v -> listener.onBookClick(turno));
            } else {
                // Turno reservado: mostrar botón Cancelar
                holder.btnBook.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(turno));
            }
        }

        // Color del estado
        if ("disponible".equals(turno.getEstado())) {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.tvEstado.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    @Override
    public int getItemCount() {
        return turnosList.size();
    }

    static class TurnoViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHora, tvEstado, tvCliente;
        Button btnEdit, btnDelete, btnBook, btnCancel;

        public TurnoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHora = itemView.findViewById(R.id.tvHora);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnBook = itemView.findViewById(R.id.btnBook);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}