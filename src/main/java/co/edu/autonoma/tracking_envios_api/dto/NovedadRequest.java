package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

/**
 * Datos para registrar una novedad asociada a un envío.
 *
 * @param idUsuario identificador del usuario que registra la novedad
 * @param descripcion texto descriptivo de la novedad
 * @param fechaNovedad fecha y hora en que ocurrió la novedad
 */
public record NovedadRequest(Integer idUsuario, String descripcion, LocalDateTime fechaNovedad) {
}