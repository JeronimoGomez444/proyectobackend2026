package co.edu.autonoma.tracking_envios_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;

/**
 * Repositorio JPA de envíos y consultas derivadas sobre su código y estado.
 */
public interface EnvioRepository extends JpaRepository<EnvioEntity, Integer> {

    /**
     * Busca el envío identificado por su código de seguimiento.
     *
     * @param codigoSeguimiento código público del envío
     * @return envío encontrado, o {@link Optional#empty()} si no existe
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    Optional<EnvioEntity> findByCodigoSeguimiento(String codigoSeguimiento);

    /**
     * Comprueba si ya existe un envío con el código indicado.
     *
     * @param codigoSeguimiento código de seguimiento por comprobar
     * @return {@code true} si el código ya está registrado
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    boolean existsByCodigoSeguimiento(String codigoSeguimiento);

    /**
     * Lista los envíos que tienen el estado actual indicado.
     *
     * @param estadoActual estado por el que se filtran los envíos
     * @return envíos encontrados
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    List<EnvioEntity> findByEstadoActual(EstadoEnvio estadoActual);

    /**
     * Cuenta los envíos que tienen el estado actual indicado.
     *
     * @param estadoActual estado por el que se cuentan los envíos
     * @return cantidad de envíos con ese estado
     * @throws org.springframework.dao.DataAccessException si ocurre un error de acceso a datos
     */
    long countByEstadoActual(EstadoEnvio estadoActual);
}