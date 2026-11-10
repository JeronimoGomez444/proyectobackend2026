package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;

/**
 * Datos de entrada para crear un envío y sus elementos asociados.
 *
 * @param idOperador identificador del usuario operador que registra el envío
 * @param paquete datos descriptivos y físicos del paquete
 * @param destinatario datos de la persona que recibe el envío
 */
public record EnvioRequest(Integer idOperador, Paquete paquete, Destinatario destinatario) {

    /**
     * Datos físicos y comerciales del paquete que se enviará.
     *
     * @param descripcion descripción del contenido del paquete
     * @param pesoKg peso en kilogramos
     * @param altoCm alto en centímetros
     * @param anchoCm ancho en centímetros
     * @param largoCm largo en centímetros
     * @param valorDeclarado valor declarado del contenido
     * @param fragil indica si el paquete requiere manejo frágil
     */
    public record Paquete(
            String descripcion,
            BigDecimal pesoKg,
            BigDecimal altoCm,
            BigDecimal anchoCm,
            BigDecimal largoCm,
            BigDecimal valorDeclarado,
            Boolean fragil) {
    }

    /**
     * Datos de entrega de la persona destinataria.
     *
     * @param nombre nombre de la persona destinataria
     * @param telefono teléfono de contacto
     * @param direccionCompleta dirección de entrega
     * @param barrio barrio de entrega
     * @param ciudad ciudad de entrega
     */
    public record Destinatario(
            String nombre,
            String telefono,
            String direccionCompleta,
            String barrio,
            String ciudad) {
    }
}