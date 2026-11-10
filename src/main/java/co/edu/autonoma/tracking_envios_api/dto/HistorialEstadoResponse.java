package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

/**
 * Representación de un cambio de estado en el historial de un envío.
 *
 * @param id identificador del registro histórico
 * @param idEnvio identificador del envío asociado
 * @param idUsuario identificador del usuario que realizó el cambio
 * @param estadoAnterior estado previo al cambio, si existe
 * @param estadoNuevo estado resultante del cambio
 * @param fechaCambio fecha y hora del cambio
 * @param observacion observación asociada al cambio
 */
public record HistorialEstadoResponse(
        Integer id,
        Integer idEnvio,
        Integer idUsuario,
        EstadoEnvio estadoAnterior,
        EstadoEnvio estadoNuevo,
        LocalDateTime fechaCambio,
        String observacion) {

    /**
     * Convierte una entidad del historial a su representación de API.
     *
     * @param h entidad persistente del historial
     * @return respuesta con los datos del cambio de estado
     * @throws NullPointerException si la entidad o sus relaciones requeridas son nulas
     */
    public static HistorialEstadoResponse desde(HistorialEstadoEntity h) {
        return new HistorialEstadoResponse(h.getId(), h.getEnvio().getId(), h.getUsuario().getId(),
                h.getEstadoAnterior(), h.getEstadoNuevo(), h.getFechaCambio(), h.getObservacion());
    }
}