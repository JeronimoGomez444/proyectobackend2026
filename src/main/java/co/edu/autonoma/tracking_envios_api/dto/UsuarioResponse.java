package co.edu.autonoma.tracking_envios_api.dto;

import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

public record UsuarioResponse(Integer id, String nombre, String email, RolUsuario rol, Boolean activo) {

    public static UsuarioResponse desde(UsuarioEntity u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol(), u.getActivo());
    }
}