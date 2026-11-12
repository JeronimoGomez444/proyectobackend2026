package co.edu.autonoma.tracking_envios_api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tracking_envios_api.dto.EstadoResponse;
import co.edu.autonoma.tracking_envios_api.service.EstadoService;

/*  *//**
 * Publica el endpoint de estado de disponibilidad del servicio.
 */
@RestController
@RequestMapping("/api/estado")
public class EstadoController {

    private final EstadoService service;

    /**
     * Crea el controlador con el servicio de estado requerido.
     *
     * @param service servicio que construye la respuesta de disponibilidad
     */
    public EstadoController(EstadoService service) {
        this.service = service;
    }

    /**
     * Consulta la disponibilidad de la aplicación.
     *
     * @return información del estado del servicio
     */
    @GetMapping
    public EstadoResponse consultar() {
        return service.consultar();
    }

}
