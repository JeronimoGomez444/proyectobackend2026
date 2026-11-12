package co.edu.autonoma.tracking_envios_api.service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;


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

    /**
     * Elimina físicamente una novedad, conservando primero sus datos para auditoría.
     *
     * <p>La auditoría y el borrado se ejecutan en una única transacción. Si una
     * restricción de integridad impide la operación, ambos cambios se revierten.</p>
     *
     * @param id identificador de la novedad
     * @throws ResponseStatusException con estado 400 si el identificador no es válido,
     *         404 si la novedad no existe o 409 si la base de datos bloquea el borrado
     */
    @Transactional
    public void eliminar(Integer id) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la novedad debe ser positivo.");
        }

        Optional<NovedadEntity> resultado = novedadRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No existe la novedad con ID " + id);
        }
        NovedadEntity novedad = resultado.get();
        if (novedad.getEnvio().getEstadoActual() == EstadoEnvio.ENTREGADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "No se puede eliminar una novedad después de cerrar el envío");
        }
        
        AuditoriaNovedadEntity auditoria = new AuditoriaNovedadEntity();
        auditoria.setEnvio(novedad.getEnvio());
        auditoria.setUsuario(novedad.getUsuario());
        auditoria.setIdNovedadEliminada(novedad.getId());
        auditoria.setDescripcionSnapshot(novedad.getDescripcion());
        auditoria.setFechaNovedadSnapshot(novedad.getFechaNovedad());
        auditoria.setFechaEliminacion(LocalDateTime.now());
        auditoria.setMotivo("Eliminación solicitada por API");

        try {
            auditoriaRepository.saveAndFlush(auditoria);
            novedadRepository.delete(novedad);
            novedadRepository.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar la novedad porque la base de datos tiene registros relacionados.",
                    ex);
        }
    }
    
    /**
     * Mantiene compatibilidad con el endpoint anterior de cambio de estado por novedad.
     * Solo permite la transición al siguiente estado y registra el usuario de la novedad.
     *
     * @param id identificador de la novedad
     * @param nuevoEstado estado de destino
     * @return envío actualizado
     */
    @Transactional
    public EnvioResponse actualizarEstado(Integer id, String nuevoEstado) {
        if (id == null || id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la novedad debe ser positivo");
        }
        Optional<NovedadEntity> resultado = novedadRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la novedad con ID " + id);
        }
        
        NovedadEntity novedad = resultado.get();
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado es obligatorio.");
        }

        EstadoEnvio estadoNuevoEnum;
        try {
            estadoNuevoEnum = EstadoEnvio.valueOf(nuevoEstado.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado inválido. Valores permitidos: CREADO, EN_RUTA, ENTREGADO.");
        }

        EstadoEnvio estadoActualEnum = novedad.getEnvio().getEstadoActual();
        validarTransicion(estadoActualEnum, estadoNuevoEnum);
        LocalDateTime fechaCambio = LocalDateTime.now();
        novedad.getEnvio().setEstadoActual(estadoNuevoEnum);
        if (estadoNuevoEnum == EstadoEnvio.EN_RUTA) {
            novedad.getEnvio().setFechaDespacho(fechaCambio);
        } else if (estadoNuevoEnum == EstadoEnvio.ENTREGADO) {
            novedad.getEnvio().setFechaEntrega(fechaCambio);
        }
        
        registrarHistorial(novedad.getEnvio(), novedad.getUsuario(), estadoActualEnum, estadoNuevoEnum);
        
        return EnvioResponse.desde(novedad.getEnvio());
    }

    private void validarTransicion(EstadoEnvio estadoActual, EstadoEnvio nuevoEstado) {
        boolean transicionPermitida = estadoActual == EstadoEnvio.CREADO
                && nuevoEstado == EstadoEnvio.EN_RUTA;
        transicionPermitida = transicionPermitida
                || (estadoActual == EstadoEnvio.EN_RUTA && nuevoEstado == EstadoEnvio.ENTREGADO);
        if (!transicionPermitida) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Transición no permitida: " + estadoActual + " -> " + nuevoEstado
                            + ". Las transiciones válidas son CREADO -> EN_RUTA -> ENTREGADO.");
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