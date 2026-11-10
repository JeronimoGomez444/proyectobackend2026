package co.edu.autonoma.tracking_envios_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.autonoma.tracking_envios_api.dto.UsuarioResponse;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.service.UsuarioService;

/**
 * Expone endpoints HTTP para consultar usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    /**
     * Crea el controlador con el servicio de usuarios requerido.
     *
     * @param service servicio que ejecuta las consultas
     */
    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    /**
     * Lista los usuarios, opcionalmente filtrados por rol.
     *
     * @param rol rol opcional para filtrar los resultados
     * @return usuarios encontrados
     */
    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) RolUsuario rol) {
        if (rol == null) {
            return service.listar();
        }
        return service.listarPorRol(rol);
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return datos del usuario
     * @throws org.springframework.web.server.ResponseStatusException si el usuario no existe
     */
    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Integer id) {
        return service.buscarPorId(id);
    }
}