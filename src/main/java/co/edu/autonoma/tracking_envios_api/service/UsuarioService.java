package co.edu.autonoma.tracking_envios_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.UsuarioResponse;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public List<UsuarioResponse> listar() {
        return repository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    public List<UsuarioResponse> listarPorRol(RolUsuario rol) {
        return repository.findByRol(rol).stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse buscarPorId(Integer id) {
        return UsuarioResponse.desde(obtener(id));
    }

    private UsuarioEntity obtener(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el usuario " + id));
    }
}