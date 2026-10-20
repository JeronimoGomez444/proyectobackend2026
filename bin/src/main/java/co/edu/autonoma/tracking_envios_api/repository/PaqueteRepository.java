package co.edu.autonoma.tracking_envios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;

public interface PaqueteRepository extends JpaRepository<PaqueteEntity, Integer> {

    Optional<PaqueteEntity> findByEnvioId(Integer envioId);
}