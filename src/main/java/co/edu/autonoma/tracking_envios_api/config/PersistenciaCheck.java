package co.edu.autonoma.tracking_envios_api.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.DestinatarioRepository;
import co.edu.autonoma.tracking_envios_api.repository.EnvioRepository;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.PaqueteRepository;
import co.edu.autonoma.tracking_envios_api.repository.UsuarioRepository;

@Component
public class PersistenciaCheck implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final EnvioRepository envioRepository;
    private final PaqueteRepository paqueteRepository;
    private final DestinatarioRepository destinatarioRepository;
    private final HistorialEstadoRepository historialRepository;
    private final NovedadRepository novedadRepository;
    private final AuditoriaNovedadRepository auditoriaRepository;

    public PersistenciaCheck(UsuarioRepository usuarioRepository,
                             EnvioRepository envioRepository,
                             PaqueteRepository paqueteRepository,
                             DestinatarioRepository destinatarioRepository,
                             HistorialEstadoRepository historialRepository,
                             NovedadRepository novedadRepository,
                             AuditoriaNovedadRepository auditoriaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.envioRepository = envioRepository;
        this.paqueteRepository = paqueteRepository;
        this.destinatarioRepository = destinatarioRepository;
        this.historialRepository = historialRepository;
        this.novedadRepository = novedadRepository;
        this.auditoriaRepository = auditoriaRepository;
    }

    @Override
    public void run(String... args) {
        // 1. Operador de prueba (solo se crea si no existe)
        UsuarioEntity operador = usuarioRepository.findByEmail("operador@demo.com")
                .orElseGet(() -> {
                    UsuarioEntity u = new UsuarioEntity();
                    u.setNombre("Operador Demo");
                    u.setEmail("operador@demo.com");
                    u.setPasswordHash("hash-de-prueba");
                    u.setRol(RolUsuario.OPERADOR);
                    u.setActivo(true);
                    return usuarioRepository.save(u);
                });

        // 2. Envio de prueba (solo se crea si no existe)
        if (!envioRepository.existsByCodigoSeguimiento("DEMO-0001")) {
            EnvioEntity e = new EnvioEntity();
            e.setCodigoSeguimiento("DEMO-0001");
            e.setEstadoActual(EstadoEnvio.CREADO);
            e.setOperador(operador);
            e.setFechaCreacion(LocalDateTime.now());
            envioRepository.save(e);
        }

        // 3. count() en las 7 tablas: comprueba conexion y mapeo de todo
        System.out.println("Usuarios: " + usuarioRepository.count());
        System.out.println("Envios: " + envioRepository.count());
        System.out.println("Paquetes: " + paqueteRepository.count());
        System.out.println("Destinatarios: " + destinatarioRepository.count());
        System.out.println("Historial: " + historialRepository.count());
        System.out.println("Novedades: " + novedadRepository.count());
        System.out.println("Auditorias: " + auditoriaRepository.count());

        // 4. findAll() y query methods
        envioRepository.findAll().forEach(e ->
                System.out.println(e.getId() + " · " + e.getCodigoSeguimiento() + " · " + e.getEstadoActual()));
        System.out.println("Envios CREADO: " + envioRepository.countByEstadoActual(EstadoEnvio.CREADO));
        System.out.println("Operadores: " + usuarioRepository.findByRol(RolUsuario.OPERADOR).size());
    }
}