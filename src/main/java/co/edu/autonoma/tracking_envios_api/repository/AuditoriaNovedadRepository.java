package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;

/**
 * Repositorio JPA para consultar y persistir auditorías de novedades.
 */
public interface AuditoriaNovedadRepository extends JpaRepository<AuditoriaNovedadEntity, Integer> {

    /**
     * Busca los registros de auditoría asociados a un envío.
     *
     * @param envioId identificador del envío
     * @return registros de auditoría asociados
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    List<AuditoriaNovedadEntity> findByEnvioId(Integer envioId);
}