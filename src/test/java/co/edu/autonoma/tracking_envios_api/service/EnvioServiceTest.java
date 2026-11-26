package co.edu.autonoma.tracking_envios_api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import co.edu.autonoma.tracking_envios_api.dto.EnvioRequest;
import co.edu.autonoma.tracking_envios_api.dto.EnvioResponse;
import co.edu.autonoma.tracking_envios_api.dto.EnvioUpdateRequest;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.UsuarioEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.enums.RolUsuario;
import co.edu.autonoma.tracking_envios_api.repository.*;

@ExtendWith(MockitoExtension.class)
class EnvioServiceTest {

    @Mock private EnvioRepository envioRepository;
    @Mock private PaqueteRepository paqueteRepository;
    @Mock private DestinatarioRepository destinatarioRepository;
    @Mock private HistorialEstadoRepository historialRepository;
    @Mock private NovedadRepository novedadRepository;
    @Mock private AuditoriaNovedadRepository auditoriaRepository;
    @Mock private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EnvioService service;

    // Ayudantes para no repetir código en cada prueba
    private UsuarioEntity usuario(int id, RolUsuario rol, boolean activo) {
        UsuarioEntity u = new UsuarioEntity();
        u.setId(id);
        u.setRol(rol);
        u.setActivo(activo);
        return u;
    }

    private EnvioEntity envio(int id, EstadoEnvio estado, UsuarioEntity operador) {
        EnvioEntity e = new EnvioEntity();
        e.setId(id);
        e.setCodigoSeguimiento("ENV-TEST0001");
        e.setEstadoActual(estado);
        e.setOperador(operador);
        return e;
    }

    // pruebas
    // camino feliz

        @Test
    void buscarPorId_existente_devuelveElEnvio() {
        // Arrange
        UsuarioEntity operador = usuario(1, RolUsuario.OPERADOR, true);
        EnvioEntity envio = envio(3, EstadoEnvio.CREADO, operador);
        when(envioRepository.findById(3)).thenReturn(Optional.of(envio));

        // Act
        EnvioResponse respuesta = service.buscarPorId(3);

        // Assert
        assertThat(respuesta.codigoSeguimiento()).isEqualTo("ENV-TEST0001");
        assertThat(respuesta.estadoActual()).isEqualTo(EstadoEnvio.CREADO);
        assertThat(respuesta.idOperador()).isEqualTo(1);
    }

    //camino de error

        @Test
    void buscarPorId_inexistente_lanza404() {
        // Arrange
        when(envioRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.buscarPorId(99));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(error.getReason()).contains("99");
    }

    @Test
void crear_solicitudValida_quedaCreadoConCodigoDeSeguimiento() {
    // Arrange
    UsuarioEntity operador = usuario(1, RolUsuario.OPERADOR, true);
    when(usuarioRepository.findById(1)).thenReturn(Optional.of(operador));
    when(envioRepository.save(any(EnvioEntity.class)))
            .thenAnswer(invocacion -> invocacion.getArgument(0));

    EnvioRequest request = new EnvioRequest(1,
            new EnvioRequest.Paquete("Caja de libros", new BigDecimal("2.5"),
                    null, null, null, null, false),
            new EnvioRequest.Destinatario("Ana Pérez", "3001234567",
                    "Calle 10 #5-20", "Centro", "Manizales"));

    // Act
    EnvioResponse respuesta = service.crear(request);

    // Assert
    assertThat(respuesta.estadoActual()).isEqualTo(EstadoEnvio.CREADO);
    assertThat(respuesta.codigoSeguimiento()).startsWith("ENV-");
    assertThat(respuesta.idOperador()).isEqualTo(1);
    assertThat(respuesta.idRepartidor()).isNull();
}

@Test
void crear_usuarioQueNoEsOperador_lanza400YNoGuardaNada() {
    // Arrange
    UsuarioEntity repartidor = usuario(2, RolUsuario.REPARTIDOR, true);
    when(usuarioRepository.findById(2)).thenReturn(Optional.of(repartidor));

    EnvioRequest request = new EnvioRequest(2,
            new EnvioRequest.Paquete("Caja", new BigDecimal("1"), null, null, null, null, false),
            new EnvioRequest.Destinatario("Ana", null, "Calle 1", null, "Manizales"));

    // Act
    ResponseStatusException error = assertThrows(ResponseStatusException.class,
            () -> service.crear(request));

    // Assert
    assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    verify(envioRepository, never()).save(any());
}


    @Test
    void actualizarEstado_creadoAEnRuta_guardaHistorialYAsignaRepartidor() {
        // Arrange
        UsuarioEntity operador = usuario(1, RolUsuario.OPERADOR, true);
        UsuarioEntity repartidor = usuario(2, RolUsuario.REPARTIDOR, true);
        EnvioEntity envio = envio(5, EstadoEnvio.CREADO, operador);
        when(envioRepository.findById(5)).thenReturn(Optional.of(envio));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(repartidor));
        EnvioUpdateRequest request =
                new EnvioUpdateRequest(EstadoEnvio.EN_RUTA, 2, "Sale a ruta", null);

        // Act
        EnvioResponse respuesta = service.actualizarEstado(5, request);

        // Assert
        assertThat(respuesta.estadoActual()).isEqualTo(EstadoEnvio.EN_RUTA);
        assertThat(respuesta.idRepartidor()).isEqualTo(2);
        verify(historialRepository).save(any(HistorialEstadoEntity.class));
    }

    @Test
    void actualizarEstado_transicionInvalida_lanza400YNoGuardaNada() {
        // Arrange
        UsuarioEntity operador = usuario(1, RolUsuario.OPERADOR, true);
        EnvioEntity envio = envio(5, EstadoEnvio.CREADO, operador);
        when(envioRepository.findById(5)).thenReturn(Optional.of(envio));
        EnvioUpdateRequest request =
                new EnvioUpdateRequest(EstadoEnvio.ENTREGADO, 2, null, null);

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.actualizarEstado(5, request));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(envioRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }

}