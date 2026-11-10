package co.edu.autonoma.tracking_envios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.PaqueteEntity;

/**
 * Repositorio JPA para acceder a los paquetes de los envíos.
 */
public interface PaqueteRepository extends JpaRepository<PaqueteEntity, Integer> {

    /**
     * Busca el paquete asociado a un envío.
     *
     * @param envioId identificador del envío
     * @return paquete asociado, o {@link Optional#empty()} si no existe
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    Optional<PaqueteEntity> findByEnvioId(Integer envioId);
}