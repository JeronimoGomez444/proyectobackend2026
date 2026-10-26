package co.edu.autonoma.tracking_envios_api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

public record EnvioResponse(
        Integer id,
        String codigoSeguimiento,
        EstadoEnvio estadoActual,
        Integer idOperador,
        Integer idRepartidor,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaDespacho,
        LocalDateTime fechaEntrega) {

    public static EnvioResponse desde(EnvioEntity e) {
        Integer idRepartidor = e.getRepartidor() == null ? null : e.getRepartidor().getId();
        return new EnvioResponse(e.getId(), e.getCodigoSeguimiento(), e.getEstadoActual(),
                e.getOperador().getId(), idRepartidor,
                e.getFechaCreacion(), e.getFechaDespacho(), e.getFechaEntrega());
    }

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

        public static Paquete desde(PaqueteEntity p) {
            return new Paquete(p.getId(), p.getEnvio().getId(), p.getDescripcion(), p.getPesoKg(),
                    p.getAltoCm(), p.getAnchoCm(), p.getLargoCm(), p.getValorDeclarado(), p.getFragil());
        }
    }

    public record Destinatario(
            Integer id,
            Integer idEnvio,
            String nombre,
            String telefono,
            String direccionCompleta,
            String barrio,
            String ciudad) {

        public static Destinatario desde(DestinatarioEntity d) {
            return new Destinatario(d.getId(), d.getEnvio().getId(), d.getNombre(), d.getTelefono(),
                    d.getDireccionCompleta(), d.getBarrio(), d.getCiudad());
        }
    }
}