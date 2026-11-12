package co.edu.autonoma.tracking_envios_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.service.NovedadService;

@RestController
@RequestMapping("/api/novedades")
public class NovedadController {

    private final NovedadService service;

    public NovedadController(NovedadService service) {
        this.service = service;
    }

    /**
     * Elimina físicamente una novedad y crea su registro de auditoría.
     *
     * @param id identificador positivo de la novedad
     * @throws org.springframework.web.server.ResponseStatusException si el ID es
     *         inválido, la novedad no existe o una restricción de la base de datos
     *         impide eliminarla
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @PatchMapping("/{id}/estado")
    public EnvioResponse actualizarEstado(@PathVariable Integer id, @RequestBody EstadoRequest request) {
        return service.actualizarEstado(id, request == null ? null : request.estado());
    }

    public record EstadoRequest(String estado) {
    }
}