package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;

public record DestinatarioResponse(
        Integer id,
        Integer idEnvio,
        String nombre,
        String telefono,
        String direccionCompleta,
        String barrio,
        String ciudad) {

    public static DestinatarioResponse desde(DestinatarioEntity d) {
        return new DestinatarioResponse(d.getId(), d.getEnvio().getId(), d.getNombre(), d.getTelefono(),
                d.getDireccionCompleta(), d.getBarrio(), d.getCiudad());
    }
}