package co.edu.autonoma.tracking_envios_api.config;

import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    public PersistenciaCheck(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Muestra en consola cuántos usuarios hay guardados actualmente en PostgreSQL
        System.out.println(">>> TOTAL DE USUARIOS EN LA BD: " + usuarioRepository.count());
    }
}