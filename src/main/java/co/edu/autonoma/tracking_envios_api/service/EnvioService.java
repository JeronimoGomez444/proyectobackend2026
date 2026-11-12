package co.edu.autonoma.tracking_envios_api.service;

import java.math.BigDecimal;
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
import co.edu.autonoma.tracking_envios_api.dto.EnvioUpdateRequest;
import co.edu.autonoma.tracking_envios_api.dto.HistorialEstadoResponse;
import co.edu.autonoma.tracking_envios_api.dto.NovedadRequest;
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
     * Cambia un envío únicamente a su siguiente estado permitido y registra el historial.
     *
     * @param id identificador del envío
     * @param request estado y usuario responsable del cambio
     * @return envío actualizado
     * @throws ResponseStatusException si el envío, usuario o repartidor no existe,
     *         o si los datos o la transición no son válidos
     */
    @Transactional
    public EnvioResponse actualizarEstado(Integer id, EnvioUpdateRequest request) {
        if (id == null || id <= 0 || request == null
                || request.nuevoEstado() == null || request.idUsuario() == null) {
            throw solicitudInvalida("Se requieren un ID de envío válido, nuevoEstado e idUsuario");
        }
        if (request.observacion() != null && request.observacion().length() > 255) {
            throw solicitudInvalida("La observacion no puede superar 255 caracteres");
        }

        EnvioEntity envio = obtener(id);
        EstadoEnvio estadoAnterior = envio.getEstadoActual();
        validarTransicion(estadoAnterior, request.nuevoEstado());

        UsuarioEntity usuario = obtenerUsuarioActivo(request.idUsuario(), "usuario");
        if (usuario.getRol() == RolUsuario.REPARTIDOR && envio.getRepartidor() != null
                && envio.getRepartidor().getId() != usuario.getId()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "El envío está asignado a otro repartidor");
        }

        if (request.idRepartidor() != null) {
            UsuarioEntity repartidor = obtenerUsuarioActivo(request.idRepartidor(), "repartidor");
            if (repartidor.getRol() != RolUsuario.REPARTIDOR) {
                throw solicitudInvalida("El usuario asignado debe tener el rol REPARTIDOR");
            }
            if (envio.getRepartidor() != null
                    && envio.getRepartidor().getId() != repartidor.getId()) {
                throw solicitudInvalida("No se puede reasignar un envío que ya tiene repartidor");
            }
            if (usuario.getRol() == RolUsuario.REPARTIDOR
                    && usuario.getId() != repartidor.getId()) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Un repartidor no puede asignar el envío a otro usuario");
            }
            envio.setRepartidor(repartidor);
        } else if (estadoAnterior == EstadoEnvio.CREADO
                && request.nuevoEstado() == EstadoEnvio.EN_RUTA
                && usuario.getRol() == RolUsuario.REPARTIDOR
                && envio.getRepartidor() == null) {
            envio.setRepartidor(usuario);
        }

        LocalDateTime fechaCambio = LocalDateTime.now();
        envio.setEstadoActual(request.nuevoEstado());
        if (request.nuevoEstado() == EstadoEnvio.EN_RUTA) {
            envio.setFechaDespacho(fechaCambio);
        } else if (request.nuevoEstado() == EstadoEnvio.ENTREGADO) {
            envio.setFechaEntrega(fechaCambio);
        }
        envioRepository.save(envio);

        HistorialEstadoEntity historial = new HistorialEstadoEntity();
        historial.setEnvio(envio);
        historial.setUsuario(usuario);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(request.nuevoEstado());
        historial.setFechaCambio(fechaCambio);
        historial.setObservacion(request.observacion() == null || request.observacion().isBlank()
                ? "Cambio de estado realizado por API"
                : request.observacion().trim());
        historialRepository.save(historial);

        return EnvioResponse.desde(envio);
    }

    /**
     * Reemplaza los datos del paquete mientras el envío todavía no ha sido despachado.
     *
     * @param idEnvio identificador del envío
     * @param request nuevos datos del paquete
     * @return paquete actualizado
     * @throws ResponseStatusException si el envío o paquete no existe, el envío ya salió
     *         a ruta o la solicitud no es válida
     */
    @Transactional
    public EnvioResponse.Paquete actualizarPaquete(Integer idEnvio, EnvioRequest.Paquete request) {
        if (request == null || esVacio(request.descripcion()) || request.pesoKg() == null
                || request.pesoKg().signum() <= 0) {
            throw solicitudInvalida("El paquete requiere descripcion y pesoKg mayor que cero");
        }
        if (!esPositivoSiExiste(request.altoCm()) || !esPositivoSiExiste(request.anchoCm())
                || !esPositivoSiExiste(request.largoCm())
                || (request.valorDeclarado() != null && request.valorDeclarado().signum() < 0)) {
            throw solicitudInvalida("Las dimensiones deben ser mayores que cero y el valor no negativo");
        }
        if (request.descripcion().length() > 200) {
            throw solicitudInvalida("La descripcion no puede superar 200 caracteres");
        }

        EnvioEntity envio = obtener(idEnvio);
        if (envio.getEstadoActual() != EstadoEnvio.CREADO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se puede actualizar el paquete antes del despacho");
        }

        Optional<PaqueteEntity> resultado = paqueteRepository.findByEnvioId(idEnvio);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El envío " + idEnvio + " no tiene paquete");
        }

        PaqueteEntity paquete = resultado.get();
        paquete.setDescripcion(request.descripcion().trim());
        paquete.setPesoKg(request.pesoKg());
        paquete.setAltoCm(request.altoCm());
        paquete.setAnchoCm(request.anchoCm());
        paquete.setLargoCm(request.largoCm());
        paquete.setValorDeclarado(request.valorDeclarado());
        paquete.setFragil(Boolean.TRUE.equals(request.fragil()));

        return EnvioResponse.Paquete.desde(paqueteRepository.save(paquete));
    }

    /**
     * Registra una novedad durante el recorrido del envío.
     *
     * @param idEnvio identificador del envío
     * @param request usuario, descripción y fecha de la novedad
     * @return novedad creada
     * @throws ResponseStatusException si el envío, usuario no existe o no se cumplen
     *         las condiciones para registrar la novedad
     */
    @Transactional
    public NovedadResponse crearNovedad(Integer idEnvio, NovedadRequest request) {
        if (request == null || request.idUsuario() == null || esVacio(request.descripcion())
                || request.fechaNovedad() == null) {
            throw solicitudInvalida("Se requieren idUsuario, descripcion y fechaNovedad");
        }
        if (request.descripcion().length() > 255) {
            throw solicitudInvalida("La descripcion no puede superar 255 caracteres");
        }

        EnvioEntity envio = obtener(idEnvio);
        if (envio.getEstadoActual() != EstadoEnvio.EN_RUTA) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Solo se puede registrar una novedad durante el recorrido (EN_RUTA)");
        }

        UsuarioEntity usuario = obtenerUsuarioActivo(request.idUsuario(), "usuario");
        if (usuario.getRol() != RolUsuario.REPARTIDOR) {
            throw solicitudInvalida("La novedad debe ser registrada por un usuario REPARTIDOR");
        }
        if (envio.getRepartidor() != null && envio.getRepartidor().getId() != usuario.getId()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El envío está asignado a otro repartidor");
        }

        NovedadEntity novedad = new NovedadEntity();
        novedad.setEnvio(envio);
        novedad.setUsuario(usuario);
        novedad.setDescripcion(request.descripcion().trim());
        novedad.setFechaNovedad(request.fechaNovedad());
        return NovedadResponse.desde(novedadRepository.save(novedad));
    }

    /**
     * Obtiene una entidad de envío a partir de su identificador.
     *
     * @param id identificador del envío
     * @return entidad encontrada
     * @throws ResponseStatusException si no existe un envío con ese identificador
     */
    private EnvioEntity obtener(Integer id) {
        if (id == null || id <= 0) {
            throw solicitudInvalida("El ID del envío debe ser positivo");
        }
        Optional<EnvioEntity> resultado = envioRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el envío " + id);
        }
        return resultado.get();
    }

    private UsuarioEntity obtenerUsuarioActivo(Integer id, String tipoUsuario) {
        Optional<UsuarioEntity> resultado = usuarioRepository.findById(id);
        if (resultado.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe el " + tipoUsuario + " " + id);
        }
        UsuarioEntity usuario = resultado.get();
        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw solicitudInvalida("El " + tipoUsuario + " " + id + " no está activo");
        }
        return usuario;
    }

    private boolean esPositivoSiExiste(BigDecimal valor) {
        return valor == null || valor.signum() > 0;
    }

    private void validarTransicion(EstadoEnvio estadoActual, EstadoEnvio nuevoEstado) {
        boolean transicionPermitida = estadoActual == EstadoEnvio.CREADO
                && nuevoEstado == EstadoEnvio.EN_RUTA;
        transicionPermitida = transicionPermitida
                || (estadoActual == EstadoEnvio.EN_RUTA && nuevoEstado == EstadoEnvio.ENTREGADO);
        if (!transicionPermitida) {
            throw solicitudInvalida("Transicion no permitida: " + estadoActual + " -> " + nuevoEstado
                    + ". Las transiciones válidas son CREADO -> EN_RUTA -> ENTREGADO.");
        }
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