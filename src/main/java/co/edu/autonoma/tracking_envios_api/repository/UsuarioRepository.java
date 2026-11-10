package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

/**
 * Repositorio JPA para consultar usuarios por correo electrónico y rol.
 */
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico por buscar
     * @return usuario encontrado, o {@link Optional#empty()} si no existe
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    Optional<UsuarioEntity> findByEmail(String email);

    /**
     * Lista los usuarios que tienen el rol indicado.
     *
     * @param rol rol por el que se filtran los usuarios
     * @return usuarios con el rol solicitado
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    List<UsuarioEntity> findByRol(RolUsuario rol);
}