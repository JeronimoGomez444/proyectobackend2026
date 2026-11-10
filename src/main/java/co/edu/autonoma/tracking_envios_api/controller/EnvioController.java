package co.edu.autonoma.tracking_envios_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import co.edu.autonoma.tracking_envios_api.dto.EnvioRequest;

import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.dto.HistorialEstadoResponse;
import co.edu.autonoma.tracking_envios_api.dto.NovedadResponse;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.service.EnvioService;

/**
 * Expone los endpoints HTTP de consulta y creación de envíos.
 *
 * <p>Delega las reglas de negocio al servicio y conserva las rutas bajo
 * {@code /api/envios}.</p>
 */
@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService service;

    /**
     * Crea el controlador con el servicio de envíos requerido.
     *
     * @param service servicio que ejecuta las operaciones de negocio
     */
    public EnvioController(EnvioService service) {
        this.service = service;
    }

    /**
     * Lista envíos, opcionalmente filtrados por estado.
     *
     * @param estado estado opcional para filtrar los resultados
     * @return envíos encontrados
     */
    @GetMapping
    public List<EnvioResponse> listar(@RequestParam(required = false) EstadoEnvio estado) {
        if (estado == null) {
            return service.listar();
        }
        return service.listarPorEstado(estado);
    }

    /**
     * Consulta un envío por su identificador.
     *
     * @param id identificador del envío
     * @return datos del envío
     * @throws org.springframework.web.server.ResponseStatusException si el envío no existe
     */
    @GetMapping("/{id}")
    public EnvioResponse buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    /**
     * Consulta un envío por su código de seguimiento.
     *
     * @param codigo código de seguimiento
     * @return datos del envío
     * @throws org.springframework.web.server.ResponseStatusException si el código no existe
     */
    @GetMapping("/seguimiento/{codigo}")
    public EnvioResponse buscarPorCodigo(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo);
    }

    /**
     * Consulta el paquete asociado a un envío.
     *
     * @param id identificador del envío
     * @return datos del paquete
     * @throws org.springframework.web.server.ResponseStatusException si el envío o paquete no existe
     */
    @GetMapping("/{id}/paquete")
    public EnvioResponse.Paquete paquete(@PathVariable Integer id) {
        return service.obtenerPaquete(id);
    }

    /**
     * Consulta el destinatario asociado a un envío.
     *
     * @param id identificador del envío
     * @return datos del destinatario
     * @throws org.springframework.web.server.ResponseStatusException si el envío o destinatario no existe
     */
    @GetMapping("/{id}/destinatario")
    public EnvioResponse.Destinatario destinatario(@PathVariable Integer id) {
        return service.obtenerDestinatario(id);
    }

    /**
     * Consulta el historial de cambios de estado de un envío.
     *
     * @param id identificador del envío
     * @return historial ordenado cronológicamente
     * @throws org.springframework.web.server.ResponseStatusException si el envío no existe
     */
    @GetMapping("/{id}/historial")
    public List<HistorialEstadoResponse> historial(@PathVariable Integer id) {
        return service.historial(id);
    }

    /**
     * Consulta las novedades de un envío.
     *
     * @param id identificador del envío
     * @return novedades ordenadas desde la más reciente
     * @throws org.springframework.web.server.ResponseStatusException si el envío no existe
     */
    @GetMapping("/{id}/novedades")
    public List<NovedadResponse> novedades(@PathVariable Integer id) {
        return service.novedades(id);
    }

    /**
     * Consulta la auditoría de novedades de un envío.
     *
     * @param id identificador del envío
     * @return registros de auditoría
     * @throws org.springframework.web.server.ResponseStatusException si el envío no existe
     */
    @GetMapping("/{id}/auditoria-novedades")
    public List<NovedadResponse.Auditoria> auditoriaNovedades(@PathVariable Integer id) {
        return service.auditoriaNovedades(id);
    }

    /**
     * Crea un envío y responde con estado HTTP {@code 201}.
     *
     * @param request datos del nuevo envío
     * @return datos del envío creado
     * @throws org.springframework.web.server.ResponseStatusException si la solicitud no es válida
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponse crear(@RequestBody EnvioRequest request) {
        return service.crear(request);
    }
}