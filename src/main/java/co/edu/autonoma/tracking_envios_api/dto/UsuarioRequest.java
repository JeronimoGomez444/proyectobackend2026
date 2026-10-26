package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

public record UsuarioRequest(String nombre, String email, String password, RolUsuario rol) { }