package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;

public interface AuditoriaNovedadRepository extends JpaRepository<AuditoriaNovedadEntity, Integer> {

    List<AuditoriaNovedadEntity> findByEnvioId(Integer envioId);
}