package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;

/**
 * Representación de una novedad registrada para un envío.
 *
 * @param id identificador de la novedad
 * @param idEnvio identificador del envío asociado
 * @param idUsuario identificador del usuario que registró la novedad
 * @param descripcion descripción de la novedad
 * @param fechaNovedad fecha y hora en que ocurrió
 * @param fechaRegistro fecha y hora en que se guardó
 */
public record NovedadResponse(
        Integer id,
        Integer idEnvio,
        Integer idUsuario,
        String descripcion,
        LocalDateTime fechaNovedad,
        LocalDateTime fechaRegistro) {

    /**
     * Convierte una entidad de novedad a su representación de API.
     *
     * @param n entidad persistente de la novedad
     * @return respuesta con los datos de la novedad
     * @throws NullPointerException si la entidad o sus relaciones requeridas son nulas
     */
    public static NovedadResponse desde(NovedadEntity n) {
        return new NovedadResponse(n.getId(), n.getEnvio().getId(), n.getUsuario().getId(),
                n.getDescripcion(), n.getFechaNovedad(), n.getFechaRegistro());
    }

    /**
     * Representación histórica de una novedad eliminada.
     *
     * @param id identificador del registro de auditoría
     * @param idEnvio identificador del envío asociado
     * @param idUsuario identificador del usuario que eliminó la novedad
     * @param idNovedadEliminada identificador original de la novedad eliminada
     * @param descripcionSnapshot descripción conservada al eliminar la novedad
     * @param fechaNovedadSnapshot fecha original de la novedad
     * @param fechaEliminacion fecha en que se eliminó
     * @param motivo motivo proporcionado para la eliminación
     */
    public record Auditoria(
            Integer id,
            Integer idEnvio,
            Integer idUsuario,
            Integer idNovedadEliminada,
            String descripcionSnapshot,
            LocalDateTime fechaNovedadSnapshot,
            LocalDateTime fechaEliminacion,
            String motivo) {

        /**
         * Convierte una entidad de auditoría a su representación de API.
         *
         * @param a entidad persistente de auditoría
         * @return respuesta con los datos históricos de la novedad
         * @throws NullPointerException si la entidad o sus relaciones requeridas son nulas
         */
        public static Auditoria desde(AuditoriaNovedadEntity a) {
            return new Auditoria(a.getId(), a.getEnvio().getId(), a.getUsuario().getId(),
                    a.getIdNovedadEliminada(), a.getDescripcionSnapshot(), a.getFechaNovedadSnapshot(),
                    a.getFechaEliminacion(), a.getMotivo());
        }
    }
}