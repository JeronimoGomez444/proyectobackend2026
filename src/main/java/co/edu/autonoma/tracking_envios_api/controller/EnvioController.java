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



@RestController
@RequestMapping("/api/envios")
public class EnvioController {

    private final EnvioService service;

    public EnvioController(EnvioService service) {
        this.service = service;
    }

    @GetMapping
    public List<EnvioResponse> listar(@RequestParam(required = false) EstadoEnvio estado) {
        return estado == null ? service.listar() : service.listarPorEstado(estado);
    }

    @GetMapping("/{id}")
    public EnvioResponse buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/seguimiento/{codigo}")
    public EnvioResponse buscarPorCodigo(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo);
    }

    @GetMapping("/{id}/paquete")
    public EnvioResponse.Paquete paquete(@PathVariable Integer id) {
        return service.obtenerPaquete(id);
    }

    @GetMapping("/{id}/destinatario")
    public EnvioResponse.Destinatario destinatario(@PathVariable Integer id) {
        return service.obtenerDestinatario(id);
    }

    @GetMapping("/{id}/historial")
    public List<HistorialEstadoResponse> historial(@PathVariable Integer id) {
        return service.historial(id);
    }

    @GetMapping("/{id}/novedades")
    public List<NovedadResponse> novedades(@PathVariable Integer id) {
        return service.novedades(id);
    }

    @GetMapping("/{id}/auditoria-novedades")
    public List<NovedadResponse.Auditoria> auditoriaNovedades(@PathVariable Integer id) {
        return service.auditoriaNovedades(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnvioResponse crear(@RequestBody EnvioRequest request) {
        return service.crear(request);
    }
}