package co.edu.autonoma.tracking_envios_api.dto;

import java.time.LocalDateTime;

public record NovedadRequest(Integer idUsuario, String descripcion, LocalDateTime fechaNovedad) { }