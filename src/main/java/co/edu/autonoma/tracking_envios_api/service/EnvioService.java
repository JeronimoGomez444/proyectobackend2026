package co.edu.autonoma.tracking_envios_api.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.AuditoriaNovedadResponse;
import co.edu.autonoma.tracking_envios_api.dto.DestinatarioResponse;
import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.dto.HistorialEstadoResponse;
import co.edu.autonoma.tracking_envios_api.dto.NovedadResponse;
import co.edu.autonoma.tracking_envios_api.dto.PaqueteResponse;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.DestinatarioRepository;
import co.edu.autonoma.tracking_envios_api.repository.EnvioRepository;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.PaqueteRepository;

@Service
@Transactional(readOnly = true)
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final PaqueteRepository paqueteRepository;
    private final DestinatarioRepository destinatarioRepository;
    private final HistorialEstadoRepository historialRepository;
    private final NovedadRepository novedadRepository;
    private final AuditoriaNovedadRepository auditoriaRepository;

    public EnvioService(EnvioRepository envioRepository,
                        PaqueteRepository paqueteRepository,
                        DestinatarioRepository destinatarioRepository,
                        HistorialEstadoRepository historialRepository,
                        NovedadRepository novedadRepository,
                        AuditoriaNovedadRepository auditoriaRepository) {
        this.envioRepository = envioRepository;
        this.paqueteRepository = paqueteRepository;
        this.destinatarioRepository = destinatarioRepository;
        this.historialRepository = historialRepository;
        this.novedadRepository = novedadRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    public List<EnvioResponse> listar() {
        return envioRepository.findAll().stream().map(EnvioResponse::desde).toList();
    }

    public List<EnvioResponse> listarPorEstado(EstadoEnvio estado) {
        return envioRepository.findByEstadoActual(estado).stream().map(EnvioResponse::desde).toList();
    }

    public EnvioResponse buscarPorId(Integer id) {
        return EnvioResponse.desde(obtener(id));
    }

    public EnvioResponse buscarPorCodigo(String codigo) {
        return envioRepository.findByCodigoSeguimiento(codigo)
                .map(EnvioResponse::desde)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el envío con código " + codigo));
    }

    public PaqueteResponse obtenerPaquete(Integer idEnvio) {
        obtener(idEnvio);
        return paqueteRepository.findByEnvioId(idEnvio)
                .map(PaqueteResponse::desde)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene paquete"));
    }

    public DestinatarioResponse obtenerDestinatario(Integer idEnvio) {
        obtener(idEnvio);
        return destinatarioRepository.findByEnvioId(idEnvio)
                .map(DestinatarioResponse::desde)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene destinatario"));
    }

    public List<HistorialEstadoResponse> historial(Integer idEnvio) {
        obtener(idEnvio);
        return historialRepository.findByEnvioIdOrderByFechaCambioAsc(idEnvio).stream()
                .map(HistorialEstadoResponse::desde).toList();
    }

    public List<NovedadResponse> novedades(Integer idEnvio) {
        obtener(idEnvio);
        return novedadRepository.findByEnvioIdOrderByFechaNovedadDesc(idEnvio).stream()
                .map(NovedadResponse::desde).toList();
    }

    public List<AuditoriaNovedadResponse> auditoriaNovedades(Integer idEnvio) {
        obtener(idEnvio);
        return auditoriaRepository.findByEnvioId(idEnvio).stream()
                .map(AuditoriaNovedadResponse::desde).toList();
    }

    private EnvioEntity obtener(Integer id) {
        return envioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el envío " + id));
    }
}