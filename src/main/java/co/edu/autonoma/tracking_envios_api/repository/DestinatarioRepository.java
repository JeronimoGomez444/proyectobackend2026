package co.edu.autonoma.tracking_envios_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.DestinatarioEntity;

/**
 * Repositorio JPA para acceder a los destinatarios de los envíos.
 */
public interface DestinatarioRepository extends JpaRepository<DestinatarioEntity, Integer> {

    /**
     * Busca el destinatario asociado a un envío.
     *
     * @param envioId identificador del envío
     * @return destinatario asociado, o {@link Optional#empty()} si no existe
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    Optional<DestinatarioEntity> findByEnvioId(Integer envioId);
}