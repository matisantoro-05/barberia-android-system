package com.example.barberia.models;

public class Turno {
    private String id;
    private String fecha;
    private String hora;
    private String estado; // "disponible" o "reservado"
    private String clienteId;
    private String clienteNombre;

    // Constructor vacío requerido para Firestore
    public Turno() {
    }

    public Turno(String id, String fecha, String hora, String estado, String clienteId, String clienteNombre) {
        this.id = id;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public boolean isDisponible() {
        return "disponible".equals(estado);
    }
}