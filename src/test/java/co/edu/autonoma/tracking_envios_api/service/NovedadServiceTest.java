package co.edu.autonoma.tracking_envios_api.service;

import co.edu.autonoma.tracking_envios_api.entity.AuditoriaNovedadEntity;
import co.edu.autonoma.tracking_envios_api.entity.EnvioEntity;
import co.edu.autonoma.tracking_envios_api.entity.HistorialEstadoEntity;
import co.edu.autonoma.tracking_envios_api.entity.NovedadEntity;
import co.edu.autonoma.tracking_envios_api.enums.EstadoEnvio;
import co.edu.autonoma.tracking_envios_api.exception.EstadoInvalidoException;
import co.edu.autonoma.tracking_envios_api.repository.AuditoriaNovedadRepository;
import co.edu.autonoma.tracking_envios_api.repository.HistorialEstadoRepository;
import co.edu.autonoma.tracking_envios_api.repository.NovedadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NovedadServiceTest {

    @Mock
    private NovedadRepository novedadRepository;

    @Mock
    private HistorialEstadoRepository historialRepository;

    @Mock
    private AuditoriaNovedadRepository auditoriaRepository;

    @InjectMocks
    private NovedadService service;

    // 1. Camino feliz: actualiza estado, guarda novedad y registra historial
    @Test
    void actualizarEstado_existenteYTransicionValida_cambiaEstadoYGuardaHistorial() {
        // Arrange
        EnvioEntity envio = new EnvioEntity();
        envio.setEstadoActual(EstadoEnvio.EN_RUTA);

        NovedadEntity novedad = new NovedadEntity();
        novedad.setId(1);
        novedad.setEnvio(envio);

        when(novedadRepository.findById(1)).thenReturn(Optional.of(novedad));
        when(novedadRepository.save(any(NovedadEntity.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        // Act
        NovedadEntity resultado = service.actualizarEstado(1, "ENTREGADO");

        // Assert
        assertThat(resultado.getEnvio().getEstadoActual()).isEqualTo(EstadoEnvio.ENTREGADO);
        verify(novedadRepository).save(novedad);
        verify(historialRepository).save(any(HistorialEstadoEntity.class));
    }

    // 2. Camino de error: ID no existe lanza 404 y no guarda nada
    @Test
    void actualizarEstado_inexistente_lanza404YNoGuarda() {
        // Arrange
        when(novedadRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.actualizarEstado(99, "ENTREGADO"));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(novedadRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }

    // 3. Regla de negocio: no permite cambiar un envío ENTREGADO a EN_RUTA
    @Test
    void actualizarEstado_deEntregadoAEnRuta_lanzaEstadoInvalidoException() {
        // Arrange
        EnvioEntity envio = new EnvioEntity();
        envio.setEstadoActual(EstadoEnvio.ENTREGADO);

        NovedadEntity novedad = new NovedadEntity();
        novedad.setId(1);
        novedad.setEnvio(envio);

        when(novedadRepository.findById(1)).thenReturn(Optional.of(novedad));

        // Act & Assert
        assertThrows(EstadoInvalidoException.class,
                () -> service.actualizarEstado(1, "EN_RUTA"));

        verify(novedadRepository, never()).save(any());
        verify(historialRepository, never()).save(any());
    }

    // 4. Interacciones (verify): eliminar existente guarda auditoría y borra novedad
    @Test
    void eliminar_existente_guardaAuditoriaYEliminaNovedad() {
        // Arrange
        EnvioEntity envio = new EnvioEntity();
        NovedadEntity novedad = new NovedadEntity();
        novedad.setId(5);
        novedad.setEnvio(envio);
        novedad.setDescripcion("Dirección incorrecta");
        novedad.setFechaNovedad(LocalDateTime.now());

        when(novedadRepository.findById(5)).thenReturn(Optional.of(novedad));

        // Act
        service.eliminar(5);

        // Assert
        verify(auditoriaRepository).save(any(AuditoriaNovedadEntity.class));
        verify(novedadRepository).delete(novedad);
    }

    // 5. Interacciones (never): eliminar inexistente lanza 404 y no toca repositorios
    @Test
    void eliminar_inexistente_lanza404YNoEliminaNiAudita() {
        // Arrange
        when(novedadRepository.findById(99)).thenReturn(Optional.empty());

        // Act
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.eliminar(99));

        // Assert
        assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(auditoriaRepository, never()).save(any());
        verify(novedadRepository, never()).delete(any());
    }
}