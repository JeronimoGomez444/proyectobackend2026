package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;

/**
 * Repositorio JPA de los cambios de estado registrados para los envíos.
 */
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstadoEntity, Integer> {

    /**
     * Recupera el historial de un envío ordenado por fecha ascendente.
     *
     * @param envioId identificador del envío
     * @return cambios de estado del envío en orden cronológico
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    List<HistorialEstadoEntity> findByEnvioIdOrderByFechaCambioAsc(Integer envioId);
}