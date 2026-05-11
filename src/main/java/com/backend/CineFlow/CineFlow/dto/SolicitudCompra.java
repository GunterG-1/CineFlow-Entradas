package com.backend.CineFlow.CineFlow.dto;

import java.util.List;

public class SolicitudCompra {
    private String numeroPelicula;
    private List<String> asientosSeleccionados;
    private String emailComprador;
    private String codigoDescuento;
    private String numeroTarjeta;
    
    // Constructor
    public SolicitudCompra() {
    }
    
    public SolicitudCompra(String numeroPelicula, List<String> asientosSeleccionados, 
                          String emailComprador, String codigoDescuento, String numeroTarjeta) {
        this.numeroPelicula = numeroPelicula;
        this.asientosSeleccionados = asientosSeleccionados;
        this.emailComprador = emailComprador;
        this.codigoDescuento = codigoDescuento;
        this.numeroTarjeta = numeroTarjeta;
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
    
    public String getEmailComprador() {
        return emailComprador;
    }
    
    public void setEmailComprador(String emailComprador) {
        this.emailComprador = emailComprador;
    }
    
    public String getCodigoDescuento() {
        return codigoDescuento;
    }
    
    public void setCodigoDescuento(String codigoDescuento) {
        this.codigoDescuento = codigoDescuento;
    }
    
    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }
    
    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
}
