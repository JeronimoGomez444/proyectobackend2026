package co.edu.autonoma.tracking_envios_api.config;

import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Comprobación de inicio que muestra el total de usuarios persistidos.
 *
 * <p>Este componente implementa el ejecutor de línea de comandos de Spring
 * y consulta el repositorio al arrancar.</p>
 */
@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    /**
     * Crea la comprobación con el repositorio necesario para contar usuarios.
     *
     * @param usuarioRepository repositorio que proporciona el conteo
     */
    public PersistenciaCheck(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Imprime el número actual de usuarios almacenados.
     *
     * @param args argumentos de inicio suministrados por Spring Boot
     * @throws Exception si la ejecución de la comprobación falla
     */
    @Override
    public void run(String... args) throws Exception {
        // Muestra en consola cuántos usuarios hay guardados actualmente en PostgreSQL
        System.out.println(">>> TOTAL DE USUARIOS EN LA BD: " + usuarioRepository.count());
    }
}