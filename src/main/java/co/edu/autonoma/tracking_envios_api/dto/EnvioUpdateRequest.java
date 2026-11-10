package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

/**
 * Datos para solicitar una actualización de estado de un envío.
 *
 * @param nuevoEstado estado que se solicita asignar
 * @param idUsuario identificador del usuario que registra el cambio
 * @param observacion observación asociada al cambio
 * @param idRepartidor identificador del repartidor relacionado, si aplica
 */
public record EnvioUpdateRequest(
        EstadoEnvio nuevoEstado,
        Integer idUsuario,
        String observacion,
        Integer idRepartidor) {
}