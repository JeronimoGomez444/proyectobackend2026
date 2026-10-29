package co.edu.autonoma.tracking_envios_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.EnvioRequest;
import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.dto.HistorialEstadoResponse;
import co.edu.autonoma.tracking_envios_api.dto.NovedadResponse;
import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.DestinatarioRepository;
import co.edu.autonoma.tracking_envios_api.repository.EnvioRepository;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.PaqueteRepository;
import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;
    private final PaqueteRepository paqueteRepository;
    private final DestinatarioRepository destinatarioRepository;
    private final HistorialEstadoRepository historialRepository;
    private final NovedadRepository novedadRepository;
    private final AuditoriaNovedadRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;

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

    public EnvioResponse.Paquete obtenerPaquete(Integer idEnvio) {
        obtener(idEnvio);
        return paqueteRepository.findByEnvioId(idEnvio)
                .map(EnvioResponse.Paquete::desde)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene paquete"));
    }

    public EnvioResponse.Destinatario obtenerDestinatario(Integer idEnvio) {
        obtener(idEnvio);
        return destinatarioRepository.findByEnvioId(idEnvio)
                .map(EnvioResponse.Destinatario::desde)
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

    public List<NovedadResponse.Auditoria> auditoriaNovedades(Integer idEnvio) {
        obtener(idEnvio);
        return auditoriaRepository.findByEnvioId(idEnvio).stream()
                .map(NovedadResponse.Auditoria::desde).toList();
    }

    @Transactional
    public EnvioResponse crear(EnvioRequest request) {
        if (request == null || request.idOperador() == null
                || request.paquete() == null || request.destinatario() == null) {
            throw solicitudInvalida("Se requiere idOperador, paquete y destinatario");
        }
        EnvioRequest.Paquete p = request.paquete();
        EnvioRequest.Destinatario d = request.destinatario();
        if (esVacio(p.descripcion()) || p.pesoKg() == null) {
            throw solicitudInvalida("El paquete requiere descripcion y pesoKg");
        }
        if (esVacio(d.nombre()) || esVacio(d.direccionCompleta()) || esVacio(d.ciudad())) {
            throw solicitudInvalida("El destinatario requiere nombre, direccionCompleta y ciudad");
        }

        UsuarioEntity operador = usuarioRepository.findById(request.idOperador())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "No existe el usuario " + request.idOperador()));
        if (operador.getRol() != RolUsuario.OPERADOR || !Boolean.TRUE.equals(operador.getActivo())) {
            throw solicitudInvalida("El usuario " + operador.getId() + " no es un OPERADOR activo");
        }

        EnvioEntity envio = new EnvioEntity();
        envio.setCodigoSeguimiento(generarCodigo());
        envio.setEstadoActual(EstadoEnvio.CREADO);
        envio.setOperador(operador);
        envio = envioRepository.save(envio);

        PaqueteEntity paquete = new PaqueteEntity();
        paquete.setEnvio(envio);
        paquete.setDescripcion(p.descripcion());
        paquete.setPesoKg(p.pesoKg());
        paquete.setAltoCm(p.altoCm());
        paquete.setAnchoCm(p.anchoCm());
        paquete.setLargoCm(p.largoCm());
        paquete.setValorDeclarado(p.valorDeclarado());
        paquete.setFragil(Boolean.TRUE.equals(p.fragil()));
        paqueteRepository.save(paquete);

        DestinatarioEntity destinatario = new DestinatarioEntity();
        destinatario.setEnvio(envio);
        destinatario.setNombre(d.nombre());
        destinatario.setTelefono(d.telefono());
        destinatario.setDireccionCompleta(d.direccionCompleta());
        destinatario.setBarrio(d.barrio());
        destinatario.setCiudad(d.ciudad());
        destinatarioRepository.save(destinatario);

        HistorialEstadoEntity historial = new HistorialEstadoEntity();
        historial.setEnvio(envio);
        historial.setUsuario(operador);
        historial.setEstadoAnterior(null);
        historial.setEstadoNuevo(EstadoEnvio.CREADO);
        historial.setFechaCambio(LocalDateTime.now());
        historial.setObservacion("Envío creado");
        historialRepository.save(historial);

        return EnvioResponse.desde(envio);
    }

    private EnvioEntity obtener(Integer id) {
        return envioRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "No existe el envío " + id));
    }

    private String generarCodigo() {
        String codigo;
        do {
            codigo = "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private ResponseStatusException solicitudInvalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}