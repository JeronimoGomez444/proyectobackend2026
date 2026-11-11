package co.edu.autonoma.tracking_envios_api.service;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.exception.EstadoInvalidoException;

import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import java.time.LocalDateTime;


@Service
@Transactional(readOnly = true)
public class NovedadService {

    private final NovedadRepository novedadRepository;
    private final HistorialEstadoRepository historialRepository;

    public NovedadService(NovedadRepository novedadRepository, HistorialEstadoRepository historialRepository) {
        this.novedadRepository = novedadRepository;
        this.historialRepository = historialRepository;
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
    
    @Transactional(readOnly = false)
    public NovedadEntity actualizarEstado(Integer id, String nuevoEstado) {
        Optional<NovedadEntity> resultado = novedadRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la novedad con ID " + id);
        }
        
        NovedadEntity novedad = resultado.get();
        
        String estadoActual = novedad.getEnvio().getEstadoActual().name(); 
        
        validarTransicion(estadoActual, nuevoEstado);
        
        novedad.getEnvio().setEstadoActual(EstadoEnvio.valueOf(nuevoEstado.toUpperCase()));
        
        NovedadEntity novedadGuardada = novedadRepository.save(novedad);
        
        registrarHistorial(null, estadoActual, nuevoEstado); 
        
        return novedadGuardada;
    }

    private void validarTransicion(String estadoActual, String nuevoEstado) {
        if ("ENTREGADO".equalsIgnoreCase(estadoActual) && "EN_RUTA".equalsIgnoreCase(nuevoEstado)) {
            throw new EstadoInvalidoException("No se puede cambiar un envío de ENTREGADO a EN_RUTA.");
        }
    }

    private void registrarHistorial(Object envio, String estadoAnterior, String estadoNuevo) {
    }
}