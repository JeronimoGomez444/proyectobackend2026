package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;

public record PaqueteRequest(
        String descripcion,
        BigDecimal pesoKg,
        BigDecimal altoCm,
        BigDecimal anchoCm,
        BigDecimal largoCm,
        BigDecimal valorDeclarado,
        Boolean fragil) { }