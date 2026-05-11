package com.backend.CineFlow.CineFlow.controller;

import com.backend.CineFlow.CineFlow.dto.SolicitudReserva;
import com.backend.CineFlow.CineFlow.service.ServicioEntradas;
import com.backend.CineFlow.CineFlow.dto.SolicitudCompra;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/entradas")
@CrossOrigin(origins = "*")
public class ControladorEntradas {
    
    @Autowired
    private ServicioEntradas servicioEntradas;
    
    /**
     * PATCH /api/v1/entradas/reservar
     * Reserva (bloquea) asientos temporalmente
     */
    @PatchMapping("/reservar")
    public ResponseEntity<Map<String, Object>> reservarAsientos(@RequestBody SolicitudReserva solicitud) {
        Map<String, Object> respuesta = servicioEntradas.reservarAsientos(solicitud);
        return ResponseEntity.ok(respuesta);
    }
    
    /**
     * POST /api/v1/entradas/pagar
     * Procesa el pago y compra de entradas
     */
    @PostMapping("/pagar")
    public ResponseEntity<Map<String, Object>> procesarPago(@RequestBody SolicitudCompra solicitud) {
        Map<String, Object> respuesta = servicioEntradas.procesarPago(solicitud);
        if ((Boolean) respuesta.get("exito")) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
    
    /**
     * GET /api/v1/entradas/{id}/codigoqr
     * Obtiene el código QR de una entrada
     */
    @GetMapping("/{id}/codigoqr")
    public ResponseEntity<Map<String, Object>> obtenerCodigoQR(@PathVariable Long id) {
        Map<String, Object> respuesta = servicioEntradas.obtenerCodigoQR(id);
        if ((Boolean) respuesta.get("exito")) {
            return ResponseEntity.ok(respuesta);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }
}
