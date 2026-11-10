package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

/**
 * Representación pública de un usuario, sin incluir su contraseña.
 *
 * @param id identificador del usuario
 * @param nombre nombre visible del usuario
 * @param email correo electrónico del usuario
 * @param rol rol asignado al usuario
 * @param activo indica si la cuenta está habilitada
 */
public record UsuarioResponse(Integer id, String nombre, String email, RolUsuario rol, Boolean activo) {

    /**
     * Convierte una entidad de usuario a su representación pública.
     *
     * @param u entidad persistente del usuario
     * @return respuesta con los datos públicos del usuario
     * @throws NullPointerException si la entidad es nula
     */
    public static UsuarioResponse desde(UsuarioEntity u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getActivo());
    }
}