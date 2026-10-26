package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;

public record NovedadResponse(
        Integer id,
        Integer idEnvio,
        Integer idUsuario,
        String descripcion,
        LocalDateTime fechaNovedad,
        LocalDateTime fechaRegistro) {

    public static NovedadResponse desde(NovedadEntity n) {
        return new NovedadResponse(n.getId(), n.getEnvio().getId(), n.getUsuario().getId(),
                n.getDescripcion(), n.getFechaNovedad(), n.getFechaRegistro());
    }

    public record Auditoria(
            Integer id,
            Integer idEnvio,
            Integer idUsuario,
            Integer idNovedadEliminada,
            String descripcionSnapshot,
            LocalDateTime fechaNovedadSnapshot,
            LocalDateTime fechaEliminacion,
            String motivo) {

        public static Auditoria desde(AuditoriaNovedadEntity a) {
            return new Auditoria(a.getId(), a.getEnvio().getId(), a.getUsuario().getId(),
                    a.getIdNovedadEliminada(), a.getDescripcionSnapshot(), a.getFechaNovedadSnapshot(),
                    a.getFechaEliminacion(), a.getMotivo());
        }
    }
}