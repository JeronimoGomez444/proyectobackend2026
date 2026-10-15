package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
    
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {

    Optional<UsuarioEntity> findByEmail(String email);

    List<UsuarioEntity> findByRol(RolUsuario rol);
}