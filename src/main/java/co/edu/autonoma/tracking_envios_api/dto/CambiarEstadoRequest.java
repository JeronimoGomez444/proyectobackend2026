package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

public record CambiarEstadoRequest(EstadoEnvio nuevoEstado, Integer idUsuario, String observacion) { }