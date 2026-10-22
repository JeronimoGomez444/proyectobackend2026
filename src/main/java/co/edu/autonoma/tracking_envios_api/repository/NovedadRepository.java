package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;

public interface NovedadRepository extends JpaRepository<NovedadEntity, Integer> {

    List<NovedadEntity> findByEnvioIdOrderByFechaNovedadDesc(Integer envioId);

    long countByEnvioId(Integer envioId);
}