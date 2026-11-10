package co.edu.autonoma.tracking_envios_api.service;

import org.springframework.stereotype.Service;

import co.edu.autonoma.tracking_envios_api.dto.EstadoResponse;

/**
 * Construye la respuesta de disponibilidad de la aplicación.
 */
@Service
public class EstadoService {
    /**
     * Obtiene el estado actual del servicio.
     *
     * @return respuesta con el nombre y la disponibilidad del servicio
     */
    public EstadoResponse consultar() {
        return new EstadoResponse("tu-api", "disponible");
    }
}
