package com.backend.CineFlow.CineFlow.dto;

import java.util.List;

public class SolicitudReserva {
    private Long idFuncion;
    private String numeroPelicula;
    private List<String> asientosSeleccionados;
    
    public SolicitudReserva() {
    }
    
    public SolicitudReserva(Long idFuncion, String numeroPelicula, List<String> asientosSeleccionados) {
        this.idFuncion = idFuncion;
        this.numeroPelicula = numeroPelicula;
        this.asientosSeleccionados = asientosSeleccionados;
    }

    public Long getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(Long idFuncion) {
        this.idFuncion = idFuncion;
    }
    
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
