package com.backend.CineFlow.CineFlow.service;

import com.backend.CineFlow.CineFlow.repository.RepositorioTicket;
import com.backend.CineFlow.CineFlow.dto.SolicitudReserva;
import com.backend.CineFlow.CineFlow.model.EstadoTicket;
import com.backend.CineFlow.CineFlow.model.Ticket;
import com.backend.CineFlow.CineFlow.dto.SolicitudCompra;
import com.backend.CineFlow.CineFlow.event.TicketPaidEvent;
import com.backend.CineFlow.CineFlow.event.TicketReservedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ServicioEntradas {
    
    @Autowired
    private RepositorioTicket repositorioTicket;

    @Autowired
    private EventBusService eventBusService;
    
    private static final long TIEMPO_BLOQUEO_MINUTOS = 15;
    
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

            if (!asientosReservados.isEmpty()) {
                TicketReservedEvent event = construirEventoTicketReserved(solicitud, asientosReservados);
                eventBusService.publicarTicketReserved(event);
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
    
    @Transactional
    public Map<String, Object> procesarPago(SolicitudCompra solicitud) {
        Map<String, Object> respuesta = new HashMap<>();
        
        try {
            List<Ticket> ticketsAComprar = new ArrayList<>();
            double precioTotal = 0;
            
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
            
            double descuento = calcularDescuento(solicitud.getCodigoDescuento(), precioTotal);
            precioTotal -= descuento;
            
            if (!validarPago(solicitud)) {
                throw new RuntimeException("Error en la validación del pago");
            }
            
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

            TicketPaidEvent event = construirEventoTicketPaid(solicitud, ticketsAComprar, codigosQR);
            eventBusService.publicarTicketPaid(event);
            
            respuesta.put("exito", true);
            respuesta.put("totalEntradas", ticketsAComprar.size());
            respuesta.put("precioTotal", precioTotal);
            respuesta.put("codigosQR", codigosQR);
            
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            respuesta.put("exito", false);
            respuesta.put("error", e.getMessage());
        }
        
        return respuesta;
    }
    
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
    
    private double calcularDescuento(String codigoDescuento, double precioOriginal) {
        return 0;
    }
    
    private boolean validarPago(SolicitudCompra solicitud) {
        return true;
    }
    
    private String generarCodigoQR(Ticket ticket) {
        return UUID.randomUUID().toString();
    }

    private TicketPaidEvent construirEventoTicketPaid(SolicitudCompra solicitud, List<Ticket> tickets, List<String> codigosQR) {
        TicketPaidEvent event = new TicketPaidEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("Ticket.Paid");
        event.setIdFuncion(resolverIdFuncion(solicitud.getIdFuncion(), solicitud.getNumeroPelicula()));
        event.setNumeroPelicula(solicitud.getNumeroPelicula());
        event.setIdUsuario(solicitud.getIdUsuario());
        event.setEmailComprador(solicitud.getEmailComprador());
        event.setAsientos(tickets.stream().map(Ticket::getNumeroAsiento).collect(Collectors.toList()));
        event.setTicketIds(tickets.stream().map(Ticket::getId).collect(Collectors.toList()));
        event.setCodigosQR(codigosQR);
        event.setOccurredAt(LocalDateTime.now());
        return event;
    }

    private TicketReservedEvent construirEventoTicketReserved(SolicitudReserva solicitud, List<Ticket> tickets) {
        TicketReservedEvent event = new TicketReservedEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setEventType("Ticket.Reserved");
        event.setIdFuncion(resolverIdFuncion(solicitud.getIdFuncion(), solicitud.getNumeroPelicula()));
        event.setNumeroPelicula(solicitud.getNumeroPelicula());
        event.setAsientos(tickets.stream().map(Ticket::getNumeroAsiento).collect(Collectors.toList()));
        event.setOccurredAt(LocalDateTime.now());
        return event;
    }

    private Long resolverIdFuncion(Long idFuncion, String numeroPelicula) {
        if (idFuncion != null) {
            return idFuncion;
        }

        if (numeroPelicula == null) {
            return null;
        }

        try {
            return Long.parseLong(numeroPelicula);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
