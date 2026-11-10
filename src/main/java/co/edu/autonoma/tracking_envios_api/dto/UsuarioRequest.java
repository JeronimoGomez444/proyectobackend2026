package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

/**
 * Datos de entrada para crear o actualizar un usuario.
 *
 * @param nombre nombre visible del usuario
 * @param email correo electrónico del usuario
 * @param password contraseña recibida para su procesamiento
 * @param rol rol asignado al usuario
 * @param activo indica si la cuenta está habilitada
 */
public record UsuarioRequest(
        String nombre,
        String email,
        String password,
        RolUsuario rol,
        Boolean activo) {
}