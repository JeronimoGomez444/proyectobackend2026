package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

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
}