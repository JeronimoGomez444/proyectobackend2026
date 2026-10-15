package co.edu.autonoma.tracking_envios_api.repository;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnvioRepository extends JpaRepository<EnvioEntity, Long> {
    
    // Método personalizado para buscar un envío por su código de seguimiento único
    Optional<EnvioEntity> findByCodigoSeguimiento(String codigoSeguimiento);
    
}