package co.edu.autonoma.tracking_envios_api.repository;

import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    
    // Método personalizado para buscar un usuario por su correo electrónico
    Optional<UsuarioEntity> findByEmail(String email);
    
}