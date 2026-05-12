package com.backend.CineFlow.CineFlow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Entity
@Data
@Table(name = "tickets")
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String numeroAsiento;
    
    @Column(nullable = false)
    private String numeroPelicula;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoTicket estado;
    
    @Column(nullable = false)
    private Double precio;
    
    @Column(nullable = true)
    private LocalDateTime fechaBloqueo;
    
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;
    
    private LocalDateTime fechaCompra;
    
    @Column(unique = true)
    private String codigoQR;
    
    @Column
    private String emailComprador;
    
    @Column
    private Double descuentoAplicado;
    
    public Ticket() {
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoTicket.DISPONIBLE;
    }
}
