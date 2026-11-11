package co.edu.autonoma.tracking_envios_api.service;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.exception.EstadoInvalidoException;

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
    private final AuditoriaNovedadRepository auditoriaRepository;


    public NovedadService(NovedadRepository novedadRepository, HistorialEstadoRepository historialRepository, AuditoriaNovedadRepository auditoriaRepository) {
        this.novedadRepository = novedadRepository;
        this.historialRepository = historialRepository;
        this.auditoriaRepository = auditoriaRepository;
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
        EstadoEnvio estadoActualEnum = novedad.getEnvio().getEstadoActual();
        String estadoActualStr = estadoActualEnum.name(); 
        
        validarTransicion(estadoActualStr, nuevoEstado);
        
        EstadoEnvio estadoNuevoEnum = EstadoEnvio.valueOf(nuevoEstado.toUpperCase());
        novedad.getEnvio().setEstadoActual(estadoNuevoEnum);
        
        NovedadEntity novedadGuardada = novedadRepository.save(novedad);
        
        registrarHistorial(novedad.getEnvio(), novedad.getUsuario(), estadoActualEnum, estadoNuevoEnum);
        
        return novedadGuardada;
    }

    private void validarTransicion(String estadoActual, String nuevoEstado) {
        if ("ENTREGADO".equalsIgnoreCase(estadoActual) && "EN_RUTA".equalsIgnoreCase(nuevoEstado)) {
            throw new EstadoInvalidoException("No se puede cambiar un envío de ENTREGADO a EN_RUTA.");
        }
    }

    private void registrarHistorial(EnvioEntity envio, UsuarioEntity usuario, EstadoEnvio estadoAnterior, EstadoEnvio estadoNuevo) {
        HistorialEstadoEntity historial = new HistorialEstadoEntity();
        historial.setEnvio(envio);
        historial.setUsuario(usuario);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setObservacion("Cambio de estado automático por actualización de novedad");
        
        historialRepository.save(historial);
    }
}