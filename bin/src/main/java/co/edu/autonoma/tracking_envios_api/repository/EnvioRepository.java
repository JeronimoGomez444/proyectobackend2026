package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

public interface EnvioRepository extends JpaRepository<EnvioEntity, Integer> {

    Optional<EnvioEntity> findByCodigoSeguimiento(String codigoSeguimiento);

    boolean existsByCodigoSeguimiento(String codigoSeguimiento);

    List<EnvioEntity> findByEstadoActual(EstadoEnvio estadoActual);

    long countByEstadoActual(EstadoEnvio estadoActual);
}