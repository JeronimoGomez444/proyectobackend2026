package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

/**
 * Representación de un envío expuesta por la API.
 *
 * @param id identificador del envío
 * @param codigoSeguimiento código público de seguimiento
 * @param estadoActual estado actual del envío
 * @param idOperador identificador del operador que creó el envío
 * @param idRepartidor identificador del repartidor asignado, si existe
 * @param fechaCreacion fecha de creación
 * @param fechaDespacho fecha de despacho, si existe
 * @param fechaEntrega fecha de entrega, si existe
 */
public record EnvioResponse(
        Integer id,
        String codigoSeguimiento,
        EstadoEnvio estadoActual,
        Integer idOperador,
        Integer idRepartidor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaDespacho,
        LocalDateTime fechaEntrega) {

    /**
     * Convierte una entidad de envío a su representación pública.
     *
     * @param e entidad persistente que se convierte
     * @return respuesta con los datos del envío
     * @throws NullPointerException si la entidad o el operador requerido son nulos
     */
    public static EnvioResponse desde(EnvioEntity e) {
        Integer idRepartidor = e.getRepartidor() == null ? null : e.getRepartidor().getId();
        return new EnvioResponse(e.getId(), e.getCodigoSeguimiento(), e.getEstadoActual(),
                e.getOperador().getId(), idRepartidor,
                e.getFechaCreacion(), e.getFechaDespacho(), e.getFechaEntrega());
    }

    /**
     * Representación pública de los datos de un paquete.
     *
     * @param id identificador del paquete
     * @param idEnvio identificador del envío asociado
     * @param descripcion descripción del paquete
     * @param pesoKg peso en kilogramos
     * @param altoCm alto en centímetros
     * @param anchoCm ancho en centímetros
     * @param largoCm largo en centímetros
     * @param valorDeclarado valor declarado
     * @param fragil indica si el paquete es frágil
     */
    public record Paquete(
            Integer id,
            Integer idEnvio,
            String descripcion,
            BigDecimal pesoKg,
            BigDecimal altoCm,
            BigDecimal anchoCm,
            BigDecimal largoCm,
            BigDecimal valorDeclarado,
            Boolean fragil) {

        /**
         * Convierte una entidad de paquete a su representación pública.
         *
         * @param p entidad persistente del paquete
         * @return respuesta con los datos del paquete
         * @throws NullPointerException si la entidad o su envío son nulos
         */
        public static Paquete desde(PaqueteEntity p) {
            return new Paquete(p.getId(), p.getEnvio().getId(), p.getDescripcion(), p.getPesoKg(),
                    p.getAltoCm(), p.getAnchoCm(), p.getLargoCm(), p.getValorDeclarado(), p.getFragil());
        }
    }

    /**
     * Representación pública de los datos del destinatario.
     *
     * @param id identificador del destinatario
     * @param idEnvio identificador del envío asociado
     * @param nombre nombre de la persona destinataria
     * @param telefono teléfono de contacto
     * @param direccionCompleta dirección completa de entrega
     * @param barrio barrio de entrega
     * @param ciudad ciudad de entrega
     */
    public record Destinatario(
            Integer id,
            Integer idEnvio,
            String nombre,
            String telefono,
            String direccionCompleta,
            String barrio,
            String ciudad) {

        /**
         * Convierte una entidad de destinatario a su representación pública.
         *
         * @param d entidad persistente del destinatario
         * @return respuesta con los datos del destinatario
         * @throws NullPointerException si la entidad o su envío son nulos
         */
        public static Destinatario desde(DestinatarioEntity d) {
            return new Destinatario(d.getId(), d.getEnvio().getId(), d.getNombre(), d.getTelefono(),
                    d.getDireccionCompleta(), d.getBarrio(), d.getCiudad());
        }
    }
}