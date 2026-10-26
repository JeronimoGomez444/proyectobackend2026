package co.edu.autonoma.tracking_envios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;

public interface DestinatarioRepository extends JpaRepository<DestinatarioEntity, Integer> {

    Optional<DestinatarioEntity> findByEnvioId(Integer envioId);
}