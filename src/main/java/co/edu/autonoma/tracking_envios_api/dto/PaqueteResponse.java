package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;

import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;

public record PaqueteResponse(
        Integer id,
        Integer idEnvio,
        String descripcion,
        BigDecimal pesoKg,
        BigDecimal altoCm,
        BigDecimal anchoCm,
        BigDecimal largoCm,
        BigDecimal valorDeclarado,
        Boolean fragil) {

    public static PaqueteResponse desde(PaqueteEntity p) {
        return new PaqueteResponse(p.getId(), p.getEnvio().getId(), p.getDescripcion(), p.getPesoKg(),
                p.getAltoCm(), p.getAnchoCm(), p.getLargoCm(), p.getValorDeclarado(), p.getFragil());
    }
}