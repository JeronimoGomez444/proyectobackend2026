package co.edu.autonoma.tracking_envios_api.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.UsuarioResponse;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;

/**
 * Proporciona consultas de usuarios y convierte sus entidades a respuestas de la API.
 */
@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository repository;

    /**
     * Crea el servicio con el repositorio de usuarios requerido.
     *
     * @param repository repositorio usado para consultar usuarios
     */
    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Recupera todos los usuarios.
     *
     * @return lista inmodificable de usuarios en el orden del repositorio
     */
    public List<UsuarioResponse> listar() {
        List<UsuarioResponse> respuestas = new ArrayList<>();
        for (UsuarioEntity usuario : repository.findAll()) {
            respuestas.add(UsuarioResponse.desde(usuario));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Recupera los usuarios que tienen el rol indicado.
     *
     * @param rol rol por el que se filtran los usuarios
     * @return lista inmodificable de usuarios con el rol solicitado
     */
    public List<UsuarioResponse> listarPorRol(RolUsuario rol) {
        List<UsuarioResponse> respuestas = new ArrayList<>();
        for (UsuarioEntity usuario : repository.findByRol(rol)) {
            respuestas.add(UsuarioResponse.desde(usuario));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return datos del usuario encontrado
     * @throws ResponseStatusException si el usuario no existe
     */
    public UsuarioResponse buscarPorId(Integer id) {
        return UsuarioResponse.desde(obtener(id));
    }

    /**
     * Obtiene una entidad de usuario a partir de su identificador.
     *
     * @param id identificador del usuario
     * @return entidad encontrada
     * @throws ResponseStatusException si no existe un usuario con ese identificador
     */
    private UsuarioEntity obtener(Integer id) {
        Optional<UsuarioEntity> resultado = repository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el usuario " + id);
        }
        return resultado.get();
    }
}