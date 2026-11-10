package co.edu.autonoma.tracking_envios_api.dto;

/**
 * Respuesta sencilla que identifica el servicio y su estado de disponibilidad.
 *
 * @param servicio nombre del servicio consultado
 * @param estado estado de disponibilidad del servicio
 */
public record EstadoResponse(
    String servicio,
    String estado
) {}
