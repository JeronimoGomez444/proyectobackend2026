package co.edu.autonoma.tracking_envios_api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.EnvioRequest;
import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.dto.HistorialEstadoResponse;
import co.edu.autonoma.tracking_envios_api.dto.NovedadResponse;
import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
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

/**
 * Coordina la consulta y creación de envíos y sus datos asociados.
 *
 * <p>La clase aplica las reglas de negocio del flujo de creación y convierte
 * entidades persistentes a respuestas de la API.</p>
 */
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

    /**
     * Recupera todos los envíos y los convierte a respuestas.
     *
     * @return lista inmodificable de envíos en el orden del repositorio
     */
    public List<EnvioResponse> listar() {
        List<EnvioResponse> respuestas = new ArrayList<>();
        for (EnvioEntity envio : envioRepository.findAll()) {
            respuestas.add(EnvioResponse.desde(envio));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Busca envíos por su estado actual.
     *
     * @param estado estado por el que se filtran los envíos
     * @return lista inmodificable de envíos que tienen el estado indicado
     */
    public List<EnvioResponse> listarPorEstado(EstadoEnvio estado) {
        List<EnvioResponse> respuestas = new ArrayList<>();
        for (EnvioEntity envio : envioRepository.findByEstadoActual(estado)) {
            respuestas.add(EnvioResponse.desde(envio));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Busca un envío por su identificador.
     *
     * @param id identificador del envío
     * @return datos del envío encontrado
     * @throws ResponseStatusException si el envío no existe
     */
    public EnvioResponse buscarPorId(Integer id) {
        return EnvioResponse.desde(obtener(id));
    }

    /**
     * Busca un envío por su código de seguimiento.
     *
     * @param codigo código de seguimiento que identifica el envío
     * @return datos del envío encontrado
     * @throws ResponseStatusException si no existe un envío con ese código
     */
    public EnvioResponse buscarPorCodigo(String codigo) {
        Optional<EnvioEntity> resultado = envioRepository.findByCodigoSeguimiento(codigo);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No existe el envío con código " + codigo);
        }
        return EnvioResponse.desde(resultado.get());
    }

    /**
     * Recupera el paquete asociado a un envío.
     *
     * @param idEnvio identificador del envío
     * @return datos del paquete asociado
     * @throws ResponseStatusException si el envío o su paquete no existen
     */
    public EnvioResponse.Paquete obtenerPaquete(Integer idEnvio) {
        obtener(idEnvio);
        Optional<PaqueteEntity> resultado = paqueteRepository.findByEnvioId(idEnvio);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene paquete");
        }
        return EnvioResponse.Paquete.desde(resultado.get());
    }

    /**
     * Recupera el destinatario asociado a un envío.
     *
     * @param idEnvio identificador del envío
     * @return datos del destinatario asociado
     * @throws ResponseStatusException si el envío o su destinatario no existen
     */
    public EnvioResponse.Destinatario obtenerDestinatario(Integer idEnvio) {
        obtener(idEnvio);
        Optional<DestinatarioEntity> resultado = destinatarioRepository.findByEnvioId(idEnvio);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene destinatario");
        }
        return EnvioResponse.Destinatario.desde(resultado.get());
    }

    /**
     * Recupera los cambios de estado de un envío en orden cronológico.
     *
     * @param idEnvio identificador del envío
     * @return historial ordenado por fecha ascendente
     * @throws ResponseStatusException si el envío no existe
     */
    public List<HistorialEstadoResponse> historial(Integer idEnvio) {
        obtener(idEnvio);
        List<HistorialEstadoResponse> respuestas = new ArrayList<>();
        for (HistorialEstadoEntity registro : historialRepository.findByEnvioIdOrderByFechaCambioAsc(idEnvio)) {
            respuestas.add(HistorialEstadoResponse.desde(registro));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Recupera las novedades de un envío desde la más reciente.
     *
     * @param idEnvio identificador del envío
     * @return novedades ordenadas por fecha descendente
     * @throws ResponseStatusException si el envío no existe
     */
    public List<NovedadResponse> novedades(Integer idEnvio) {
        obtener(idEnvio);
        List<NovedadResponse> respuestas = new ArrayList<>();
        for (NovedadEntity novedad : novedadRepository.findByEnvioIdOrderByFechaNovedadDesc(idEnvio)) {
            respuestas.add(NovedadResponse.desde(novedad));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Recupera el registro de auditoría de novedades de un envío.
     *
     * @param idEnvio identificador del envío
     * @return registros de auditoría en el orden entregado por el repositorio
     * @throws ResponseStatusException si el envío no existe
     */
    public List<NovedadResponse.Auditoria> auditoriaNovedades(Integer idEnvio) {
        obtener(idEnvio);
        List<NovedadResponse.Auditoria> respuestas = new ArrayList<>();
        for (AuditoriaNovedadEntity auditoria : auditoriaRepository.findByEnvioId(idEnvio)) {
            respuestas.add(NovedadResponse.Auditoria.desde(auditoria));
        }
        return Collections.unmodifiableList(respuestas);
    }

    /**
     * Valida la solicitud y crea el envío junto con su paquete,
     * destinatario e historial inicial.
     *
     * @param request datos requeridos para crear el envío
     * @return respuesta con los datos del envío persistido
     * @throws ResponseStatusException si la solicitud es inválida o el operador no existe
     */
    @Transactional
    public EnvioResponse crear(EnvioRequest request) {
        // Validar primero la estructura antes de leer componentes anidados de la solicitud.
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

        Optional<UsuarioEntity> resultadoOperador = usuarioRepository.findById(request.idOperador());
        if (resultadoOperador.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No existe el usuario " + request.idOperador());
        }
        UsuarioEntity operador = resultadoOperador.get();
        // Solo una cuenta activa con rol operador puede registrar envíos.
        if (operador.getRol() != RolUsuario.OPERADOR || !Boolean.TRUE.equals(operador.getActivo())) {
            throw solicitudInvalida("El usuario " + operador.getId() + " no es un OPERADOR activo");
        }

        // Persistir primero el envío permite asociar sus datos dependientes mediante su identificador.
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

        // Guardar el destinatario con la misma referencia al envío recién persistido.
        DestinatarioEntity destinatario = new DestinatarioEntity();
        destinatario.setEnvio(envio);
        destinatario.setNombre(d.nombre());
        destinatario.setTelefono(d.telefono());
        destinatario.setDireccionCompleta(d.direccionCompleta());
        destinatario.setBarrio(d.barrio());
        destinatario.setCiudad(d.ciudad());
        destinatarioRepository.save(destinatario);

        // El historial inicial registra la creación y el operador responsable del alta.
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

    /**
     * Obtiene una entidad de envío a partir de su identificador.
     *
     * @param id identificador del envío
     * @return entidad encontrada
     * @throws ResponseStatusException si no existe un envío con ese identificador
     */
    private EnvioEntity obtener(Integer id) {
        Optional<EnvioEntity> resultado = envioRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el envío " + id);
        }
        return resultado.get();
    }

    /**
     * Genera un código de seguimiento único y vuelve a intentarlo si ya existe.
     *
     * @return código de seguimiento disponible
     */
    private String generarCodigo() {
        String codigo;
        do {
            codigo = "ENV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (envioRepository.existsByCodigoSeguimiento(codigo));
        return codigo;
    }

    /**
     * Indica si una cadena no contiene texto útil.
     *
     * @param texto cadena que se valida
     * @return {@code true} si es {@code null}, está vacía o solo contiene espacios
     */
    private boolean esVacio(String texto) {
        return texto == null || texto.isBlank();
    }

    /**
     * Crea el error HTTP usado para solicitudes de creación inválidas.
     *
     * @param mensaje detalle de la validación fallida
     * @return excepción que representa una solicitud HTTP incorrecta
     */
    private ResponseStatusException solicitudInvalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}