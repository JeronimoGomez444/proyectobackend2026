package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

public record ActualizarUsuarioRequest(String nombre, RolUsuario rol, Boolean activo) { }