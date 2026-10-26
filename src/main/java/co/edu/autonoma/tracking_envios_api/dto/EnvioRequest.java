package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;

public record EnvioRequest(Integer idOperador, Paquete paquete, Destinatario destinatario) {

    public record Paquete(
            String descripcion,
            BigDecimal pesoKg,
            BigDecimal altoCm,
            BigDecimal anchoCm,
            BigDecimal largoCm,
            BigDecimal valorDeclarado,
            Boolean fragil) {
    }

    public record Destinatario(
            String nombre,
            String telefono,
            String direccionCompleta,
            String barrio,
            String ciudad) {
    }
}