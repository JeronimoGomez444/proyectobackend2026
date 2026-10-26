package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

public record EnvioResponse(
        Integer id,
        String codigoSeguimiento,
        EstadoEnvio estadoActual,
        Integer idOperador,
        Integer idRepartidor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaDespacho,
        LocalDateTime fechaEntrega) {

    public static EnvioResponse desde(EnvioEntity e) {
        Integer idRepartidor = e.getRepartidor() == null ? null : e.getRepartidor().getId();
        return new EnvioResponse(e.getId(), e.getCodigoSeguimiento(), e.getEstadoActual(),
                e.getOperador().getId(), idRepartidor,
                e.getFechaCreacion(), e.getFechaDespacho(), e.getFechaEntrega());
    }
}