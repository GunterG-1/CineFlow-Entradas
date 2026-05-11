package com.backend.CineFlow.CineFlow.dto;

import java.util.List;

public class SolicitudReserva {
    private String numeroPelicula;
    private List<String> asientosSeleccionados;
    
    // Constructor
    public SolicitudReserva() {
    }
    
    public SolicitudReserva(String numeroPelicula, List<String> asientosSeleccionados) {
        this.numeroPelicula = numeroPelicula;
        this.asientosSeleccionados = asientosSeleccionados;
    }
    
    // Getters y Setters
    public String getNumeroPelicula() {
        return numeroPelicula;
    }
    
    public void setNumeroPelicula(String numeroPelicula) {
        this.numeroPelicula = numeroPelicula;
    }
    
    public List<String> getAsientosSeleccionados() {
        return asientosSeleccionados;
    }
    
    public void setAsientosSeleccionados(List<String> asientosSeleccionados) {
        this.asientosSeleccionados = asientosSeleccionados;
    }
}
