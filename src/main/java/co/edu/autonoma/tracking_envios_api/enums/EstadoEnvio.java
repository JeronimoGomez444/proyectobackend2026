package co.edu.autonoma.tracking_envios_api.enums;

/**
 * Estados definidos para el ciclo de vida de un envío.
 */
public enum EstadoEnvio {
    /** El envío fue registrado y aún no inició su recorrido. */
    CREADO,
    /** El envío está en tránsito hacia su destino. */
    EN_RUTA,
    /** El envío fue entregado a su destinatario. */
    ENTREGADO
}