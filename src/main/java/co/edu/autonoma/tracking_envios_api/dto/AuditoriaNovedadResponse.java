package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;

public record AuditoriaNovedadResponse(
        Integer id,
        Integer idEnvio,
        Integer idUsuario,
        Integer idNovedadEliminada,
        String descripcionSnapshot,
        LocalDateTime fechaNovedadSnapshot,
        LocalDateTime fechaEliminacion,
        String motivo) {

    public static AuditoriaNovedadResponse desde(AuditoriaNovedadEntity a) {
        return new AuditoriaNovedadResponse(a.getId(), a.getEnvio().getId(), a.getUsuario().getId(),
                a.getIdNovedadEliminada(), a.getDescripcionSnapshot(), a.getFechaNovedadSnapshot(),
                a.getFechaEliminacion(), a.getMotivo());
    }
}