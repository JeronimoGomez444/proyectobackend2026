package co.edu.autonoma.tracking_envios_api.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.exception.EstadoInvalidoException;


@Service
@Transactional(readOnly = true)
public class NovedadService {

    private final NovedadRepository novedadRepository;

    public NovedadService(NovedadRepository novedadRepository) {
        this.novedadRepository = novedadRepository;
    }

    @Transactional
    public void eliminar(Integer id) {
        Optional<NovedadEntity> resultado = novedadRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No existe la novedad con ID " + id);
        }
        NovedadEntity novedad = resultado.get();
        novedadRepository.delete(novedad);
    }
    private void validarTransicion(String estadoActual, String nuevoEstado) {
        if ("ENTREGADO".equalsIgnoreCase(estadoActual) && "EN_RUTA".equalsIgnoreCase(nuevoEstado)) {
            throw new EstadoInvalidoException("No se puede cambiar un envío de ENTREGADO a EN_RUTA.");
        }
    }
}