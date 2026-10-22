package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

public record HistorialEstadoResponse(
        Integer id,
        Integer idEnvio,
        Integer idUsuario,
        EstadoEnvio estadoAnterior,
        EstadoEnvio estadoNuevo,
        LocalDateTime fechaCambio,
        String observacion) {

    public static HistorialEstadoResponse desde(HistorialEstadoEntity h) {
        return new HistorialEstadoResponse(h.getId(), h.getEnvio().getId(), h.getUsuario().getId(),
                h.getEstadoAnterior(), h.getEstadoNuevo(), h.getFechaCambio(), h.getObservacion());
    }
}