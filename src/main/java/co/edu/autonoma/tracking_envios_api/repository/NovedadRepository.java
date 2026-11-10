package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;

/**
 * Repositorio JPA para consultar y persistir novedades de envíos.
 */
public interface NovedadRepository extends JpaRepository<NovedadEntity, Integer> {

    /**
     * Recupera las novedades de un envío ordenadas desde la más reciente.
     *
     * @param envioId identificador del envío
     * @return novedades asociadas en orden descendente por fecha
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    List<NovedadEntity> findByEnvioIdOrderByFechaNovedadDesc(Integer envioId);

    /**
     * Cuenta las novedades registradas para un envío.
     *
     * @param envioId identificador del envío
     * @return número de novedades del envío
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    long countByEnvioId(Integer envioId);
}