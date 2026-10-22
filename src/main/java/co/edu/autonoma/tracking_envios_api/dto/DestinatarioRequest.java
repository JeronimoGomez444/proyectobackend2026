package co.edu.autonoma.tracking_envios_api.dto;

public record DestinatarioRequest(
        String nombre,
        String telefono,
        String direccionCompleta,
        String barrio,
        String ciudad) { }