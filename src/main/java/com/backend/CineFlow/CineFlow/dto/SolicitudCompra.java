package com.backend.CineFlow.CineFlow.dto;

import java.util.List;

public class SolicitudCompra {
    private Long idFuncion;
    private String numeroPelicula;
    private Long idUsuario;
    private List<String> asientosSeleccionados;
    private String emailComprador;
    private String codigoDescuento;
    private String numeroTarjeta;
    
    public SolicitudCompra() {
    }
    
    public SolicitudCompra(Long idFuncion, String numeroPelicula, Long idUsuario, List<String> asientosSeleccionados, 
                          String emailComprador, String codigoDescuento, String numeroTarjeta) {
        this.idFuncion = idFuncion;
        this.numeroPelicula = numeroPelicula;
        this.idUsuario = idUsuario;
        this.asientosSeleccionados = asientosSeleccionados;
        this.emailComprador = emailComprador;
        this.codigoDescuento = codigoDescuento;
        this.numeroTarjeta = numeroTarjeta;
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

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
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
