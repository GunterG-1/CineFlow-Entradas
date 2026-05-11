package com.backend.CineFlow.CineFlow.service;

import com.backend.CineFlow.CineFlow.repository.RepositorioTicket;
import com.backend.CineFlow.CineFlow.dto.SolicitudReserva;
import com.backend.CineFlow.CineFlow.model.EstadoTicket;
import com.backend.CineFlow.CineFlow.model.Ticket;
import com.backend.CineFlow.CineFlow.dto.SolicitudCompra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ServicioEntradas {
    
    @Autowired
    private RepositorioTicket repositorioTicket;
    
    private static final long TIEMPO_BLOQUEO_MINUTOS = 15;
    
    /**
     * PATCH /entradas/reservar
     * Bloquea temporalmente los asientos seleccionados
     */
    @Transactional
    public Map<String, Object> reservarAsientos(SolicitudReserva solicitud) {
        Map<String, Object> respuesta = new HashMap<>();
        List<Ticket> asientosReservados = new ArrayList<>();
        List<String> asientosNoDisponibles = new ArrayList<>();
        
        try {
            for (String numeroAsiento : solicitud.getAsientosSeleccionados()) {
                Optional<Ticket> ticket = repositorioTicket.buscarPorPeliculaYAsiento(
                    solicitud.getNumeroPelicula(), 
                    numeroAsiento
                );
                
                if (ticket.isPresent() && ticket.get().getEstado() == EstadoTicket.DISPONIBLE) {
                    Ticket t = ticket.get();
                    t.setEstado(EstadoTicket.BLOQUEADO);
                    t.setFechaBloqueo(LocalDateTime.now());
                    repositorioTicket.save(t);
                    asientosReservados.add(t);
                } else {
                    asientosNoDisponibles.add(numeroAsiento);
                }
            }
            
            respuesta.put("exito", true);
            respuesta.put("asientosReservados", asientosReservados.size());
            respuesta.put("asientosNoDisponibles", asientosNoDisponibles);
            respuesta.put("tiempoExpiracion", TIEMPO_BLOQUEO_MINUTOS);
            
        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("error", e.getMessage());
        }
        
        return respuesta;
    }
    
    /**
     * POST /entradas/pagar
     * Procesa la compra de entradas (transaccional)
     */
    @Transactional
    public Map<String, Object> procesarPago(SolicitudCompra solicitud) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            List<Ticket> ticketsAComprar = new ArrayList<>();
            double precioTotal = 0;
            
            // Validar que los asientos sigan disponibles o bloqueados
            for (String numeroAsiento : solicitud.getAsientosSeleccionados()) {
                Optional<Ticket> ticket = repositorioTicket.buscarPorPeliculaYAsiento(
                    solicitud.getNumeroPelicula(),
                    numeroAsiento
                );
                
                if (ticket.isEmpty()) {
                    throw new RuntimeException("Asiento " + numeroAsiento + " no encontrado");
                }
                
                Ticket t = ticket.get();
                if (t.getEstado() != EstadoTicket.BLOQUEADO && t.getEstado() != EstadoTicket.DISPONIBLE) {
                    throw new RuntimeException("Asiento " + numeroAsiento + " no está disponible");
                }
                
                ticketsAComprar.add(t);
                precioTotal += t.getPrecio();
            }
            
            // Aplicar descuento si existe
            double descuento = calcularDescuento(solicitud.getCodigoDescuento(), precioTotal);
            precioTotal -= descuento;
            
            // Validar pago (integración con pasarela de pago)
            if (!validarPago(solicitud)) {
                throw new RuntimeException("Error en la validación del pago");
            }
            
            // Confirmar compra
            List<String> codigosQR = new ArrayList<>();
            for (Ticket ticket : ticketsAComprar) {
                ticket.setEstado(EstadoTicket.VENDIDO);
                ticket.setFechaCompra(LocalDateTime.now());
                ticket.setEmailComprador(solicitud.getEmailComprador());
                ticket.setDescuentoAplicado(descuento / ticketsAComprar.size());
                String qr = generarCodigoQR(ticket);
                ticket.setCodigoQR(qr);
                codigosQR.add(qr);
                repositorioTicket.save(ticket);
            }
            
            respuesta.put("exito", true);
            respuesta.put("totalEntradas", ticketsAComprar.size());
            respuesta.put("precioTotal", precioTotal);
            respuesta.put("codigosQR", codigosQR);
            
        } catch (Exception e) {
            respuesta.put("exito", false);
            respuesta.put("error", e.getMessage());
        }
        
        return respuesta;
    }
    
    /**
     * GET /entradas/{id}/codigoqr
     * Obtiene el código QR de validación
     */
    public Map<String, Object> obtenerCodigoQR(Long idTicket) {
        Map<String, Object> respuesta = new HashMap<>();
        
        if (idTicket == null) {
            respuesta.put("exito", false);
            respuesta.put("error", "ID de ticket inválido");
            return respuesta;
        }
        
        Optional<Ticket> ticket = repositorioTicket.findById(idTicket);
        if (ticket.isPresent()) {
            respuesta.put("exito", true);
            respuesta.put("codigoQR", ticket.get().getCodigoQR());
            respuesta.put("asiento", ticket.get().getNumeroAsiento());
            respuesta.put("pelicula", ticket.get().getNumeroPelicula());
        } else {
            respuesta.put("exito", false);
            respuesta.put("error", "Ticket no encontrado");
        }
        
        return respuesta;
    }
    
    /**
     * Limpia los bloqueos caducados (ejecutarse periódicamente)
     */
    @Transactional
    public void limpiarBloqueosCaducados() {
        LocalDateTime tiempoLimite = LocalDateTime.now().minusMinutes(TIEMPO_BLOQUEO_MINUTOS);
        List<Ticket> bloqueadosCaducados = repositorioTicket.obtenerBloqueosCaducados(tiempoLimite);
        
        for (Ticket ticket : bloqueadosCaducados) {
            ticket.setEstado(EstadoTicket.DISPONIBLE);
            ticket.setFechaBloqueo(null);
            repositorioTicket.save(ticket);
        }
    }
    
    // Métodos auxiliares
    private double calcularDescuento(String codigoDescuento, double precioOriginal) {
        // Implementar lógica de descuentos
        return 0;
    }
    
    private boolean validarPago(SolicitudCompra solicitud) {
        // Integrar con pasarela de pago (Stripe, PayPal, etc.)
        return true;
    }
    
    private String generarCodigoQR(Ticket ticket) {
        return UUID.randomUUID().toString();
    }
}
