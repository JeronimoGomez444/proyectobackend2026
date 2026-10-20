package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;

public interface HistorialEstadoRepository extends JpaRepository<HistorialEstadoEntity, Integer> {

    List<HistorialEstadoEntity> findByEnvioIdOrderByFechaCambioAsc(Integer envioId);
}